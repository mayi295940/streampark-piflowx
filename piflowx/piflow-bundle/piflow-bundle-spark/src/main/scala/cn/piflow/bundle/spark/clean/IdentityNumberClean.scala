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

package cn.piflow.bundle.spark.clean

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.bundle.spark.util.CleanUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class IdentityNumberClean extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "Cleaning data in ID Card format"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnName: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sqlContext = spark.sqlContext
    val dfOld = in.read()
    dfOld.createOrReplaceTempView("thesis")
    sqlContext.udf.register("regexPro", (str: String) => CleanUtil.processCardCode(str))
    val structFields: Array[String] = dfOld.schema.fieldNames
    val columnNames = columnName.split(",").toSet
    val sqlNewFieldStr = new StringBuilder
    columnNames.foreach(c => {
      if (columnNames.contains(c)) {
        sqlNewFieldStr ++= ",regexPro("
        sqlNewFieldStr ++= c
        sqlNewFieldStr ++= ") as "
        sqlNewFieldStr ++= c
        sqlNewFieldStr ++= "_new "
      }
    })

    val sqlText: String = "select * " + sqlNewFieldStr + " from thesis"

    val dfNew = sqlContext.sql(sqlText)
    dfNew.createOrReplaceTempView("thesis")
    val schemaStr = new StringBuilder
    structFields.foreach(field => {
      schemaStr ++= field
      if (columnNames.contains(field)) {
        schemaStr ++= "_new as "
        schemaStr ++= field
      }
      schemaStr ++= ","
    })
    val sqlTextNew: String = "select " +
      schemaStr.substring(0, schemaStr.length - 1) +
      " from thesis"
    val dfNew1 = sqlContext.sql(sqlTextNew)

    out.write(dfNew1)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    columnName = MapUtil.get(map, key = "columnName").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val columnName = new PropertyDescriptor()
      .name("columnName")
      .displayName("Column_Name")
      .description("Column names are what you want to clean," +
        "multiple column names are separated by commas")
      .defaultValue("")
      .required(true)
      .example("IdCard")

    descriptor = columnName :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/clean/IdentityNumberClean.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CleanGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
