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

package cn.piflow.bundle.spark.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._
import org.json4s.JsonAST._
import org.json4s.jackson.JsonMethods._

import java.time.{Instant, LocalDate, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import scala.collection.mutable
import scala.util.Random

class MockData extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Mock dataframe."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: List[Map[String, Any]] = _
  var count: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[List[Map[String, Any]]]
    count = MapUtil.get(map, "count").asInstanceOf[String].toInt
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .language(Language.MockDataSchema)
      .description("The schema of mock data,columnType can be STRING/BYTE/INT/LONG/BIGINT/FLOAT/DOUBLE/DECIMAL/BOOLEAN/DATE/TIMESTAMP.")
      .defaultValue("")
      .required(true)
      .example(
        "[{\"id\":\"317974\",\"filedName\":\"id\",\"filedType\":\"STRING\",\"index\":0},{\"id\":\"808911\",\"filedName\":\"age\",\"filedType\":\"INT\"}]")
    descriptor = schema :: descriptor

    val count = new PropertyDescriptor()
      .name("count")
      .displayName("Count")
      .description("The count of dataframe")
      .defaultValue("")
      .required(true)
      .example("10")
    descriptor = count :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    import spark.implicits._

    val field = schema.toArray

    val structFieldArray: Array[StructField] = new Array[StructField](field.length)

    for (i <- field.indices) {
      val item = field(i)
      val filedMap = mutable.Map(item.toSeq: _*)
      val column = MapUtil.get(filedMap, "filedName").toString
      val columnType = MapUtil.get(filedMap, "filedType").toString
      var isNullable = false
      val isNullableValue = MapUtil.get(filedMap, "isNullable")
      if (isNullableValue != null) {
        isNullable = MapUtil.get(filedMap, "isNullable").toString.toBoolean
      }

      columnType match {
        case "STRING" => structFieldArray(i) = StructField(column, StringType, isNullable)
        case "BYTE" => structFieldArray(i) = StructField(column, ByteType, isNullable)
        case "INT" => structFieldArray(i) = StructField(column, IntegerType, isNullable)
        case "DOUBLE" => structFieldArray(i) = StructField(column, DoubleType, isNullable)
        case "FLOAT" => structFieldArray(i) = StructField(column, FloatType, isNullable)
        case "LONG" => structFieldArray(i) = StructField(column, LongType, isNullable)
        case "BIGINT" => structFieldArray(i) = StructField(column, LongType, isNullable)
        case "DECIMAL" => structFieldArray(i) = StructField(column, DecimalType.apply(10, 2), isNullable)
        case "BOOLEAN" => structFieldArray(i) = StructField(column, BooleanType, isNullable)
        case "DATE" => structFieldArray(i) = StructField(column, DateType, nullable = true)
        case "TIMESTAMP" =>
          structFieldArray(i) = StructField(column, TimestampType, nullable = true)
      }
    }
    val schemaStructType = StructType(structFieldArray)
    val rnd: Random = new Random()
    val df = spark.read
      .schema(schemaStructType)
      .json(
        (0 until count)
          .map(_ => compact(randomJson(rnd, schemaStructType)))
          .toDS())
    out.write(df)
  }

  private val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val startDate = LocalDate.of(1900, 1, 1)
  private val endDate = LocalDate.of(2100, 1, 1)

  private def randomJson(rnd: Random, dataType: DataType): JValue = {
    dataType match {
      case v: DoubleType =>
        JDouble(rnd.nextDouble())
      case v: StringType =>
        JString((1 to 10).map(x => alpha(Random.nextInt.abs % alpha.length)).mkString)
      case v: ByteType =>
        JInt(rnd.nextInt(100))
      case v: IntegerType =>
        JInt(rnd.nextInt(100))
      case v: LongType =>
        JInt(rnd.nextLong())
      case v: FloatType =>
        JDouble(rnd.nextFloat())
      case v: BooleanType =>
        JBool(rnd.nextBoolean())
      case v: DecimalType =>
        JDecimal(rnd.nextInt(10))
      case v: DateType =>
        JString(startDate.plusDays(rnd.nextInt(endDate.toEpochDay.toInt - startDate.toEpochDay.toInt)).toString)
      case v: TimestampType =>
        JString(Instant.now().minus(rnd.nextLong().abs % 365, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).format(formatter))
      case v: ArrayType =>
        val size = rnd.nextInt(10)
        JArray(
          (0 to size).map(_ => randomJson(rnd, v.elementType)).toList)
      case v: StructType =>
        JObject(
          v.fields.flatMap {
            f =>
              if (f.nullable && rnd.nextBoolean())
                None
              else
                Some(JField(f.name, randomJson(rnd, f.dataType)))
          }.toList)
    }
  }

  override def getEngineType: String = Constants.ENGIN_SPARK
}
