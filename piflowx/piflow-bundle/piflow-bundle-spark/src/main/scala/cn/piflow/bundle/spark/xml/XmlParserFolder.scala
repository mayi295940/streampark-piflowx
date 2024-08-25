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

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.net.URI

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/** Created by admin on 2018/8/27. */
class XmlParserFolder extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "lijie"
  val description: String = "Parse xml folder"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var rowTag: String = _
  var xmlpath: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val pathArr = getFileName(xmlpath)
    val xmlDF = getResDf(pathArr, spark)
    out.write(xmlDF)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    xmlpath = MapUtil.get(map, "xmlpath").asInstanceOf[String]
    rowTag = MapUtil.get(map, "rowTag").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val xmlpath = new PropertyDescriptor()
      .name("xmlpath")
      .displayName("xmlpath")
      .defaultValue("")
      .required(true)
      .example("/work/test/xml/")

    val rowTag = new PropertyDescriptor()
      .name("rowTag")
      .displayName("rowTag")
      .description("the tag you want to parse in xml file")
      .defaultValue("")
      .required(true)
      .example("name,url")

    descriptor = xmlpath :: descriptor
    descriptor = rowTag :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/xml/FolderXmlParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.XmlGroup.toString)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  // 获取.xml所有文件路径
  private def getFileName(path: String): ArrayBuffer[String] = {
    var arr = ArrayBuffer[String]()
    val conf = new Configuration()
    val fs = FileSystem.get(URI.create(path), conf)
    val statuses = fs.listStatus(new Path(path))
    for (i <- statuses) {
      if (!i.isDirectory) {
        if (i.getPath.getName.endsWith(".xml")) {
          arr += i.getPath.toString
        }
      } else {
        val arr1 = getFileName(i.getPath.toString)
        arr = arr ++ (arr1)
      }
    }
    arr
  }

  // 获取每个xml得dataframe
  private def getDf(path: String, sparkSession: SparkSession): DataFrame = {
    val df = sparkSession.read
      .format("com.databricks.spark.xml")
      .option("rowTag", rowTag)
      .option("treatEmptyValuesAsNulls", value = true)
      .load(path)
    df
  }

  // 获取文件夹最终得dataframe
  private def getResDf(pathArr: ArrayBuffer[String], spark: SparkSession): DataFrame = {
    var index = 0
    breakable {
      for (i <- pathArr.indices) {
        if (getDf(pathArr(i), spark).count() != 0) {
          index = i
          break
        }
      }
    }
    var df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", rowTag)
      .option("treatEmptyValuesAsNulls", value = true)
      .load(pathArr(index))
    for (d <- index + 1 until (pathArr.length)) {
      if (getDf(pathArr(d), spark).count() != 0) {
        val df1 = spark.read
          .format("com.databricks.spark.xml")
          .option("rowTag", rowTag)
          .option("treatEmptyValuesAsNulls", value = true)
          .load(pathArr(d))
        df = df.union(df1)
      }
    }
    df
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
