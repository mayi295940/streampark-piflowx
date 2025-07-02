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
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import com.amazon.deequ.analyzers.DataTypeInstances
import com.amazon.deequ.profiles.{ColumnProfilerRunner, NumericColumnProfile, StringColumnProfile}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

import scala.collection.JavaConverters._

class DataProfiling extends ConfigurableStop[Null, DataFrame, Null] {
  override val authorEmail: String = ""
  override val description: String = "数据探查"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Dqc)
  }

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()

    val inDf: DataFrame = in.read()

    /* Make deequ profile this data. It will execute the three passes over the data and avoid any shuffles. */
    val result = ColumnProfilerRunner().onData(inDf).run()

    // 定义 DataFrame 的 Schema
    val schema = StructType(Seq(
      StructField("columnName", StringType, nullable = false),
      StructField("completeness", DoubleType, nullable = true),
      StructField("approximateNumDistinctValues", LongType, nullable = true),
      StructField("dataType", StringType, nullable = true),
      StructField("typeCounts", MapType(StringType, LongType), nullable = true),
      StructField("minimum", DoubleType, nullable = true),
      StructField("maximum", DoubleType, nullable = true),
      StructField("mean", DoubleType, nullable = true),
      StructField("sum", DoubleType, nullable = true),
      StructField("stdDev", DoubleType, nullable = true),
      StructField("approxPercentiles", StringType, nullable = true),
      StructField("minLength", IntegerType, nullable = true),
      StructField("maxLength", IntegerType, nullable = true)))

    // 将 profiles 数据转换为 Row 集合
    val rows = result.profiles.map { case (name, profile) =>
      profile.dataType match {
        case DataTypeInstances.Integral =>
          val numberProfile = result.profiles(name).asInstanceOf[NumericColumnProfile]
          Row(
            name,
            numberProfile.completeness,
            numberProfile.approximateNumDistinctValues,
            numberProfile.dataType.toString,
            numberProfile.typeCounts.asJava,
            numberProfile.minimum.orNull,
            numberProfile.maximum.orNull,
            numberProfile.mean.orNull,
            numberProfile.sum.orNull,
            numberProfile.stdDev.orNull,
            numberProfile.approxPercentiles.orNull,
            null,
            null)
        case DataTypeInstances.String =>
          val stringColumnProfile = result.profiles(name).asInstanceOf[StringColumnProfile]
          Row(
            name,
            stringColumnProfile.completeness,
            stringColumnProfile.approximateNumDistinctValues,
            stringColumnProfile.dataType.toString,
            stringColumnProfile.typeCounts.asJava,
            null,
            null,
            null,
            null,
            null,
            null,
            stringColumnProfile.minLength.orNull,
            stringColumnProfile.maxLength.orNull)
        case _ =>
          Row(
            name,
            profile.completeness,
            profile.approximateNumDistinctValues,
            profile.dataType.toString,
            profile.typeCounts.asJava,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null)
      }
    }.toSeq

    // 创建 DataFrame
    val profileDf = spark.createDataFrame(rows.asJava, schema)

    out.write(profileDf)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
