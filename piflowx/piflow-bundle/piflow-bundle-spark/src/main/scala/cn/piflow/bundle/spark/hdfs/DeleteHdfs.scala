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
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.DataFrame

class DeleteHdfs extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "ygang@cnic.com"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)
  override val description: String = "Delete files or directories on HDFS"

  var hdfsUrl: String = _
  var hdfsPath: String = _
  var isCustomize: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    if (isCustomize.equals("false")) {
      val inDf = in.read()

      val configuration: Configuration = new Configuration()
      var pathStr: String = inDf.take(1)(0).get(0).asInstanceOf[String]
      val pathARR: Array[String] = pathStr.split("\\/")
      var hdfsUrl: String = ""
      for (x <- (0 until 3)) {
        hdfsUrl += (pathARR(x) + Constants.SINGLE_SLASH)
      }
      configuration.set("fs.defaultFS", hdfsUrl)

      var fs: FileSystem = FileSystem.get(configuration)

      inDf.collect.foreach(row => {
        pathStr = row.get(0).asInstanceOf[String]
        val path = new Path(pathStr)
        fs.delete(path, true)
      })

    } else {
      val array = hdfsPath.split(Constants.COMMA).map(x => x.trim)

      for (i <- array.indices) {
        val hdfsPath = hdfsUrl + Constants.SINGLE_SLASH + array(i)
        val path = new Path(hdfsPath)

        val config = new Configuration()
        config.set("fs.defaultFS", hdfsUrl)
        val fs = FileSystem.get(config)

        fs.delete(path, true)
      }
    }
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    hdfsUrl = MapUtil.get(map, key = "hdfsUrl").asInstanceOf[String]
    hdfsPath = MapUtil.get(map, key = "hdfsPath").asInstanceOf[String]
    isCustomize = MapUtil.get(map, key = "isCustomize").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val hdfsPath = new PropertyDescriptor()
      .name("hdfsPath")
      .displayName("HdfsPath")
      .defaultValue("")
      .description("File path of HDFS")
      .required(true)
      .example("/work/test1/")
    descriptor = hdfsPath :: descriptor

    val hdfsUrl = new PropertyDescriptor()
      .name("hdfsUrl")
      .displayName("HdfsUrl")
      .defaultValue("")
      .description("URL address of HDFS")
      .required(true)
      .example("hdfs://192.168.3.138:8020")
    descriptor = hdfsUrl :: descriptor

    val isCustomize = new PropertyDescriptor()
      .name("isCustomize")
      .displayName("IsCustomize")
      .description("Whether to customize the compressed file path, if true," +
        "you must specify the path where the compressed file is located." +
        "If false,automatically find the file path data from the upstream port")
      .defaultValue("true")
      .allowableValues(Set("true", "false"))
      .required(true)
      .example("true")
    descriptor = isCustomize :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hdfs/DeleteHdfs.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HdfsGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
