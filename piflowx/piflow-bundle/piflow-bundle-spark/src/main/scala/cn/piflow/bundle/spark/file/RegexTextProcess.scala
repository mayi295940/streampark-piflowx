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

package cn.piflow.bundle.spark.file

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class RegexTextProcess extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "Replace values in a column with regex"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var regex: String = _
  var columnName: String = _
  var replaceStr: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sqlContext = spark.sqlContext
    val dfOld = in.read()
    val regexText = regex
    val replaceText = replaceStr
    dfOld.createOrReplaceTempView("thesis")
    sqlContext.udf.register("regexPro", (str: String) => str.replaceAll(regexText, replaceText))
    val sqlText: String =
      "select *,regexPro(" + columnName + ") as " + columnName + "_new from thesis"
    val dfNew = sqlContext.sql(sqlText)
    out.write(dfNew)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    regex = MapUtil.get(map, key = "regex").asInstanceOf[String]
    columnName = MapUtil.get(map, key = "columnName").asInstanceOf[String]
    replaceStr = MapUtil.get(map, key = "replaceStr").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val regex = new PropertyDescriptor()
      .name("regex")
      .displayName("Regex")
      .description("regex")
      .defaultValue("")
      .required(true)
      .example("0001")
    descriptor = regex :: descriptor

    val columnName = new PropertyDescriptor()
      .name("columnName")
      .displayName("ColumnName")
      .description("The columns you want to replace")
      .defaultValue("")
      .required(true)
      .example("id")
    descriptor = columnName :: descriptor

    val replaceStr = new PropertyDescriptor()
      .name("replaceStr")
      .displayName("ReplaceStr")
      .description("Value after replacement")
      .defaultValue("")
      .required(true)
      .example("1111")
    descriptor = replaceStr :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/file/RegexTextProcess.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.FileGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}