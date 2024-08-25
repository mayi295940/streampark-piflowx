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

package cn.piflow.bundle.spark.hdfs

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, FSDataOutputStream, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

import java.io.InputStream
import java.net.{HttpURLConnection, URL}

class FileDownHdfs extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "yangqidong@cnic.cn"
  val description: String = "Download the data from the url to HDFS"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var hdfsUrl: String = _
  var hdfsPath: String = _
  var url_str: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val url = new URL(url_str)
    val uc: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    uc.setDoInput(true)
    uc.connect()

    val inputStream: InputStream = uc.getInputStream

    val buffer = new Array[Byte](1024 * 1024 * 10)
    var byteRead = -1

    val configuration: Configuration = new Configuration()

    configuration.set("fs.defaultFS", hdfsUrl)

    val fs = FileSystem.get(configuration)
    val fdos: FSDataOutputStream = fs.create(new Path(hdfsUrl + hdfsPath))

    while (((byteRead = inputStream.read(buffer)) != -1) && (byteRead != -1)) {
      fdos.write(buffer, 0, byteRead)
      fdos.flush()
    }

    inputStream.close()
    fdos.close()

    val seq: Seq[String] = Seq(hdfsUrl + hdfsPath)
    val row: Row = Row.fromSeq(seq)
    val list: List[Row] = List(row)
    val rdd: RDD[Row] = spark.sparkContext.makeRDD(list)
    val fields: Array[StructField] = Array(StructField("savePath", StringType, nullable = true))
    val schema: StructType = StructType(fields)
    val df: DataFrame = spark.createDataFrame(rdd, schema)
    out.write(df)

  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    hdfsUrl = MapUtil.get(map, key = "hdfsUrl").asInstanceOf[String]
    hdfsPath = MapUtil.get(map, key = "hdfsPath").asInstanceOf[String]
    url_str = MapUtil.get(map, key = "url_str").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url_str = new PropertyDescriptor()
      .name("url_str")
      .displayName("Url_Str")
      .description("Network address of file")
      .defaultValue("")
      .required(true)
    descriptor = url_str :: descriptor

    val hdfsPath = new PropertyDescriptor()
      .name("hdfsPath")
      .displayName("HdfsPath")
      .defaultValue("")
      .description("File path of HDFS")
      .required(true)
      .example("/work/test.gz")
    descriptor = hdfsPath :: descriptor

    val hdfsUrl = new PropertyDescriptor()
      .name("hdfsUrl")
      .displayName("HdfsUrl")
      .defaultValue("")
      .description("URL address of HDFS")
      .required(true)
      .example("hdfs://192.168.3.138:8020")
    descriptor = hdfsUrl :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/http/LoadZipFromUrl.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HdfsGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
