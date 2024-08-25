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

package cn.piflow.bundle.spark.ftp

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.bundle.core.util.FtpDownAndUploadUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class DownloadFromFtpToHDFS extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Download data from FTP and save to HDFS"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var ftpUrl: String = _
  var ftpPort: String = _
  var ftpUsername: String = _
  var ftpPassword: String = _
  var ftpPath: String = _

  var hdfsUrl: String = _
  var hdfsPath: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val ftpUtil = new FtpDownAndUploadUtil

    val ftpClient = ftpUtil.getFtpClient(ftpUrl, ftpPort.toInt, ftpUsername, ftpPassword, hdfsUrl)

    ftpUtil.downloadFromFtpAndUploadToHdfs(ftpClient, ftpPath, hdfsPath)
    ftpUtil.disConnect(ftpClient)

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    ftpUrl = MapUtil.get(map, key = "ftpUrl").asInstanceOf[String]
    ftpPort = MapUtil.get(map, key = "ftpPort").asInstanceOf[String]
    ftpUsername = MapUtil.get(map, key = "ftpUsername").asInstanceOf[String]
    ftpPassword = MapUtil.get(map, key = "ftpPassword").asInstanceOf[String]
    ftpPath = MapUtil.get(map, key = "ftpPath").asInstanceOf[String]
    hdfsUrl = MapUtil.get(map, key = "hdfsUrl").asInstanceOf[String]
    hdfsPath = MapUtil.get(map, key = "hdfsPath").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val ftpUrl = new PropertyDescriptor()
      .name("ftpUrl")
      .displayName("ftpUrl")
      .description("IP of FTP server")
      .required(true)
      .example("128.136.0.1 or ftp.ei.addfc.gak")
    descriptor = ftpUrl :: descriptor

    val ftpPort = new PropertyDescriptor()
      .name("ftpPort")
      .displayName("ftpPort")
      .description("Port of FTP server")
      .required(false)
      .example("")
    descriptor = ftpPort :: descriptor

    val ftpUsername = new PropertyDescriptor()
      .name("ftpUsername")
      .displayName("ftpUsername")
      .description("")
      .required(false)
      .example("ftpUser")
    descriptor = ftpUsername :: descriptor

    val ftpPassword = new PropertyDescriptor()
      .name("ftpPassword")
      .displayName("ftpPassword")
      .description("")
      .required(false)
      .example("123456")
    descriptor = ftpPassword :: descriptor

    val ftpPath = new PropertyDescriptor()
      .name("ftpPath")
      .displayName("ftpPath")
      .description("The path of the file to the FTP server")
      .required(true)
      .example("/test/Ab/ or /test/Ab/test.txt")
    descriptor = ftpPath :: descriptor

    val hdfsUrl = new PropertyDescriptor()
      .name("hdfsUrl")
      .displayName("hdfsUrl")
      .description("The URL of the HDFS file system")
      .required(true)
      .example("hdfs://10.0.88.70:9000")
    descriptor = hdfsUrl :: descriptor

    val hdfsPath = new PropertyDescriptor()
      .name("hdfsPath")
      .displayName("hdfsPath")
      .description("The save path of the HDFS file system")
      .required(true)
      .example("test/Ab/")
    descriptor = hdfsPath :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/ftp/loadFromFtpUrl.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.FtpGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}