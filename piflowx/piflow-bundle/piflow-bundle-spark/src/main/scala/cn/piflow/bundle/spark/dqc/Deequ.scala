/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.piflow.bundle.spark.dqc

import cn.piflow._
import cn.piflow.bundle.spark.entity.deequ.{ConstraintConfig, DeequRuleConfig}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.JsonUtil
import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.{Check, CheckLevel, CheckStatus}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.ClassTag

// 定义类型类，用于处理不同类型的转换
trait NumericConverter[T] {
  def convert(value: String): T

  def minValue: T

  def maxValue: T
}

// 为 Double 和 Long 实现类型类
object NumericConverter {
  implicit val doubleConverter: NumericConverter[Double] = new NumericConverter[Double] {
    override def convert(value: String): Double = value.toDouble

    override def minValue: Double = 0.0

    override def maxValue: Double = Double.MaxValue
  }

  implicit val longConverter: NumericConverter[Long] = new NumericConverter[Long] {
    override def convert(value: String): Long = value.toDouble.toLong

    override def minValue: Long = 0L

    override def maxValue: Long = Long.MaxValue
  }
}

/**
 * 数据质量检查
 *
 * @author mayi
 * @since 2025/07/06
 */
class Deequ extends ConfigurableStop[Null, DataFrame, Null] {

  override val authorEmail: String = ""
  override val description: String = "数据质量检查"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var ruleConfig: DeequRuleConfig = _
  private var deequRuleMap: Map[String, Any] = _

  override def setProperties(map: Map[String, Any]): Unit = {
    deequRuleMap = MapUtil.get(map, key = "deequRule", Map()).asInstanceOf[Map[String, Any]]
    ruleConfig = DeequRuleConfig.get(JsonUtil.toJson(deequRuleMap))
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val deequRule = new PropertyDescriptor()
      .name("deequRule")
      .displayName("质量规则")
      .language(Language.DeequRule)
      .description("检查级别：当规则失败时如何处理 - Error（停止处理）或 Warning（记录但继续）\n\n常见约束类型：\n\nhasSize：验证数据集行数\nisComplete：确保列没有空值\nisUnique：确保列值唯一\nisContainedIn：确保列值在指定集合中\nisNonNegative：确保数值列没有负值\nsatisfies：自定义条件表达式\n最佳实践：\n\n为每个数据集创建独立的规则集\n使用有意义的规则描述\n从关键约束开始，逐步添加更复杂的规则\n定期审查规则的有效性")
      .required(true)

    descriptor = deequRule :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Dqc)
  }

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  // 提取通用的断言函数创建方法
  private def createAssertion[T: NumericConverter: ClassTag: Ordering](op: String, constraint: ConstraintConfig): T => Boolean = {
    val converter = implicitly[NumericConverter[T]]
    val ord = implicitly[Ordering[T]]
    op match {
      case "==" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => ord.equiv(v, value)
      case "!=" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => !ord.equiv(v, value)
      case ">" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => ord.gt(v, value)
      case ">=" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => ord.gteq(v, value)
      case "<" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => ord.lt(v, value)
      case "<=" =>
        val value = Option(constraint.getParams).map(p => converter.convert(p.getValue)).getOrElse(converter.minValue)
        v => ord.lteq(v, value)
      case "between" =>
        val min = Option(constraint.getParams).map(p => converter.convert(p.getMin)).getOrElse(converter.minValue)
        val max = Option(constraint.getParams).map(p => converter.convert(p.getMax)).getOrElse(converter.maxValue)
        v => ord.gteq(v, min) && ord.lteq(v, max)
      case _ => throw new IllegalArgumentException(s"Unsupported operator: $op")
    }
  }

  // 简化的 Long 断言函数
  private def createLongAssertion(op: String, constraint: ConstraintConfig): Long => Boolean = {
    createAssertion[Long](op, constraint)
  }

  // 简化的 Double 断言函数
  private def createDoubleAssertion(op: String, constraint: ConstraintConfig): Double => Boolean = {
    createAssertion[Double](op, constraint)
  }

  import org.apache.spark.sql.Row
  import org.apache.spark.sql.types._

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()
    val inDf: DataFrame = in.read()

    // 创建 Check 对象
    var check = Check(
      if (ruleConfig.getCheckLevel == "Error") CheckLevel.Error else CheckLevel.Warning,
      ruleConfig.getDescription)

    import scala.collection.JavaConverters._

    val constraints: Seq[ConstraintConfig] = ruleConfig.getConstraints.asScala
    constraints.foreach { constraint =>
      constraint.getConstraintType match {
        case "hasSize" =>
          Option(constraint.getParams).map(_.getOperator()).foreach { op =>
            val assertion = createLongAssertion(op, constraint)
            check = check.hasSize(assertion)
          }
        case "isComplete" =>
          Option(constraint.getParams).map(_.getColumn()).foreach { col =>
            check = check.isComplete(col)
          }
        case "isUnique" =>
          Option(constraint.getParams).map(_.getColumn()).foreach { col =>
            check = check.isUnique(col)
          }
        case "isPrimaryKey" =>
          Option(constraint.getParams).map(_.getColumn()).foreach { col =>
            check = check.isPrimaryKey(col)
          }
        case "isContainedIn" =>
          for {
            col <- Option(constraint.getParams).map(_.getColumn())
            vals <- Option(constraint.getParams).map(_.getValues())
          } yield {
            // 如果 vals 是 Scala 集合，toArray 可用；如果是 Java 集合，需要转换
            val arr = vals match {
              case javaList: java.util.List[_] => javaList.asScala.toArray
              case scalaSeq: Seq[_] => scalaSeq.asScala.toArray
              case _ => throw new IllegalArgumentException("Unsupported collection type")
            }
            check = check.isContainedIn(col, arr)
          }
        case "isNonNegative" =>
          Option(constraint.getParams).map(_.getColumn()).foreach { col =>
            check = check.isNonNegative(col)
          }
        case "satisfies" =>
          for {
            condition <- Option(constraint.getParams).map(_.getCondition())
            name <- Option(constraint.getParams).map(_.getName())
          } yield {
            check = check.satisfies(condition, name)
          }
        case "hasMin" =>
          for {
            col <- Option(constraint.getParams).map(_.getColumn())
            op <- Option(constraint.getParams).map(_.getOperator())
          } yield {
            val assertion = createDoubleAssertion(op, constraint)
            check = check.hasMin(col, assertion)
          }
        case "hasMax" =>
          for {
            col <- Option(constraint.getParams).map(_.getColumn())
            op <- Option(constraint.getParams).map(_.getOperator())
          } yield {
            val assertion = createDoubleAssertion(op, constraint)
            check = check.hasMax(col, assertion)
          }
        case "hasMean" =>
          for {
            col <- Option(constraint.getParams).map(_.getColumn())
            op <- Option(constraint.getParams).map(_.getOperator())
          } yield {
            val assertion = createDoubleAssertion(op, constraint)
            check = check.hasMean(col, assertion)
          }
        case other =>
          throw new IllegalArgumentException(s"Unsupported constraint type: $other")
      }
    }

    // 执行规则验证
    val verificationResult = VerificationSuite()
      .onData(inDf)
      .addCheck(check)
      .run()

    if (verificationResult.status == CheckStatus.Success) {
      println("The data passed the test, everything is fine!")
    } else {
      println("We found errors in the data, the following constraints were not satisfied:\n")
    }

    val resultsForAllConstraints = verificationResult.checkResults
      .flatMap { case (_, checkResult) => checkResult.constraintResults }

    // 定义 DataFrame 的结构
    val schema = StructType(Seq(
      StructField("constraint", StringType, nullable = true),
      StructField("status", StringType, nullable = true),
      StructField("message", StringType, nullable = true),
      StructField("metricValue", DoubleType, nullable = true)))

    // 将 resultsForAllConstraints 转换为 Row 对象列表
    val rows: Seq[Row] = resultsForAllConstraints.map { result =>
      val metricValue = result.metric.flatMap(_.value.toOption).getOrElse(Double.NaN)
      Row(
        result.constraint.toString,
        result.status.toString,
        result.message.orNull,
        metricValue)
    }.toSeq

    val resultsDf = spark.createDataFrame(spark.sparkContext.parallelize(rows), schema)
    resultsDf.show(false)
    out.write(resultsDf)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
