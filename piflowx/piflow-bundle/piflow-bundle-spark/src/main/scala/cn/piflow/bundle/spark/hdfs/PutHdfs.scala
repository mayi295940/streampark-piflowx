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

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.{DataFrame, SparkSession}

class PutHdfs extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "ygang@cnic.com"
  override val description: String = "Put data into hdfs"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var hdfsPath: String = _
  var hdfsUrl: String = _
  var types: String = _
  var partition: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val inDF = in.read()

    val config = new Configuration()
    config.set("fs.defaultFS", hdfsUrl)
    val fs = FileSystem.get(config)

    if (types == "json") {
      inDF.repartition(partition.toInt).write.json(hdfsUrl + hdfsPath)
    } else if (types == "csv") {
      inDF.repartition(partition.toInt).write.csv(hdfsUrl + hdfsPath)
    } else {
      // parquet
      inDF.repartition(partition.toInt).write.save(hdfsUrl + hdfsPath)
    }
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    hdfsUrl = MapUtil.get(map, key = "hdfsUrl").asInstanceOf[String]
    hdfsPath = MapUtil.get(map, key = "hdfsPath").asInstanceOf[String]
    types = MapUtil.get(map, key = "types").asInstanceOf[String]
    partition = MapUtil.get(map, key = "partition").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val hdfsPath = new PropertyDescriptor()
      .name("hdfsPath")
      .displayName("HdfsPath")
      .defaultValue("")
      .description("File path of HDFS")
      .required(true)
      .example("/work/")
    descriptor = hdfsPath :: descriptor

    val hdfsUrl = new PropertyDescriptor()
      .name("hdfsUrl")
      .displayName("HdfsUrl")
      .defaultValue("")
      .description("URL address of HDFS")
      .required(true)
      .example("hdfs://192.168.3.138:8020")
    descriptor = hdfsUrl :: descriptor

    val types = new PropertyDescriptor()
      .name("types")
      .displayName("Types")
      .description("The format you want to write is json,csv,parquet")
      .defaultValue("csv")
      .allowableValues(Set("json", "csv", "parquet"))
      .required(true)
      .example("csv")
    descriptor = types :: descriptor

    val partition = new PropertyDescriptor()
      .name("partition")
      .displayName("Partition")
      .description("Write several partitions")
      .defaultValue("1")
      .required(true)
      .example("1")
    descriptor = partition :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hdfs/PutHdfs.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HdfsGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}