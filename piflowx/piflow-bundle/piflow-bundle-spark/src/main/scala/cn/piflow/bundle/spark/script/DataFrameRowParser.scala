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

package cn.piflow.bundle.spark.script

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

import scala.beans.BeanProperty

class DataFrameRowParser extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Create dataframe by schema"
  val inportList: List[String] = List(Port.DefaultPort.toString)
  val outportList: List[String] = List(Port.DefaultPort.toString)

  var schema: String = _
  var separator: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
    separator = MapUtil.get(map, "separator").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("schema")
      .description("The schema of dataframe")
      .defaultValue("")
      .required(true)

    val separator = new PropertyDescriptor()
      .name("separator")
      .displayName("separator")
      .description("The separator of schema")
      .defaultValue("")
      .required(true)

    descriptor = schema :: descriptor
    descriptor = separator :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/script/DataFrameRowParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.ScriptGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val inDF = in.read()

    // parse RDD
    val rdd = inDF.rdd.map(row => {
      val fieldArray = row
        .get(0)
        .asInstanceOf[String]
        .split(",")
        .map(x => x.trim)
      Row.fromSeq(fieldArray.toSeq)
    })

    // parse schema
    val field = schema.split(separator).map(x => x.trim)
    val structFieldArray: Array[StructField] = new Array[StructField](field.length)
    for (i <- field.indices) {
      structFieldArray(i) = StructField(field(i), StringType, nullable = true)
    }
    val schemaStructType = StructType(structFieldArray)

    // create DataFrame
    val df = spark.createDataFrame(rdd, schemaStructType)
    // df.show()
    out.write(df)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
