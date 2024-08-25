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

package cn.piflow.bundle.spark.xml

import cn.piflow._
import cn.piflow.bundle.spark.util.XmlToJson
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

class XmlParserColumns extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "ygang@cnic.cn"
  val description: String = "Parse xml data in columns in upstream data"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var xmlColumns: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val df = in.read()

    spark.sqlContext.udf.register(
      "xmlToJson",
      (str: String) => {
        XmlToJson.xmlParse(str.replaceAll(Constants.LINE_SPLIT_N, Constants.TAB_SIGN))
      })
    val columns: Array[String] = xmlColumns.toLowerCase.split(Constants.COMMA).map(x => x.trim)

    val fields: Array[String] = df.schema.fieldNames
    val fieldString = new StringBuilder
    fields.foreach(x => {
      if (columns.contains(x.toLowerCase)) {
        fieldString.append(s"xmlToJson($x) as $x ,")
      } else {
        fieldString.append(s"$x,")
      }
    })

    df.createOrReplaceTempView("temp")
    val sqlText = "select " + fieldString.stripSuffix(Constants.COMMA) + " from temp"
    val frame: DataFrame = spark.sql(sqlText)

    val rdd: RDD[String] = frame.toJSON.rdd.map(x => {
      x.replace("\\n", "")
        .replace("}\"", "}")
        .replace(":\"{", ":{")
        .replace("\\", "")
    })

    val outDF: DataFrame = spark.read.json(rdd)
    outDF.printSchema()

    out.write(outDF)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]) = {
    xmlColumns = MapUtil.get(map, "xmlColumns").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val xmlColumns = new PropertyDescriptor()
      .name("xmlColumns")
      .displayName("xmlColumns")
      .description("Parsed column names containing xml,Multiple columns separated by commas")
      .defaultValue("")
      .required(true)
      .example("product_xml")
    descriptor = xmlColumns :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/xml/XmlParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.XmlGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
