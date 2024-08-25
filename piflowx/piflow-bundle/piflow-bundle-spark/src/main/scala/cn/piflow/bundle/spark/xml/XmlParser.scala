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
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.beans.BeanProperty

class XmlParser extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Parse xml file"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var xmlpath: String = _
  var rowTag: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val xmlDF = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", rowTag)
      .option("treatEmptyValuesAsNulls", value = true)
      .load(xmlpath)

    out.write(xmlDF)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]) = {
    xmlpath = MapUtil.get(map, "xmlpath").asInstanceOf[String]
    rowTag = MapUtil.get(map, "rowTag").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val xmlpath = new PropertyDescriptor()
      .name("xmlpath")
      .displayName("xmlpath")
      .description("the path of xml file")
      .defaultValue("")
      .required(true)
      .example("hdfs://192.168.3.138:8020/work/test/testxml.xml")

    val rowTag = new PropertyDescriptor()
      .name("rowTag")
      .displayName("rowTag")
      .description("the tag you want to parse in xml file")
      .defaultValue("")
      .required(true)
      .example("name")

    descriptor = xmlpath :: descriptor
    descriptor = rowTag :: descriptor
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
