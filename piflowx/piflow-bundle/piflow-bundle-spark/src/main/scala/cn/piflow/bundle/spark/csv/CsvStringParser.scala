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

package cn.piflow.bundle.spark.csv

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

class CsvStringParser extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  override val description: String = "Parse csv string"

  var string: String = _
  var delimiter: String = _
  var schema: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val session: SparkSession = pec.get[SparkSession]
    val context: SparkContext = session.sparkContext
    val arrStr: Array[String] = string.split(Constants.LINE_SPLIT_N).map(x => x.trim)
    var num: Int = 0

    val listROW: List[Row] = arrStr
      .map(line => {
        val seqSTR: Seq[String] = line.split(delimiter).map(x => x.trim).toSeq
        num = seqSTR.size
        val row = Row.fromSeq(seqSTR)
        row
      })
      .toList

    val rowRDD: RDD[Row] = context.makeRDD(listROW)

    if (schema.isEmpty) {
      (0 until num).foreach(x => {
        schema += ("_" + x + ",")
      })
    }

    val fields: Array[StructField] = schema
      .split(",")
      .map(d => StructField(d.trim, StringType, nullable = true))

    val NewSchema: StructType = StructType(fields)
    val Fdf: DataFrame = session.createDataFrame(rowRDD, NewSchema)
    out.write(Fdf)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    string = MapUtil.get(map, "string").asInstanceOf[String]
    delimiter = MapUtil.get(map, "delimiter").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val string = new PropertyDescriptor()
      .name("string")
      .displayName("String")
      .defaultValue("")
      .required(true)
      .example("1,zs\n2,ls\n3,ww")
    descriptor = string :: descriptor

    val delimiter = new PropertyDescriptor()
      .name("delimiter")
      .displayName("Delimiter")
      .description("The delimiter of CSV string")
      .defaultValue("")
      .required(true)
      .example(",")
    descriptor = delimiter :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of CSV string")
      .defaultValue("")
      .required(false)
      .example("")
    descriptor = schema :: descriptor

    descriptor

  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvStringParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CsvGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
