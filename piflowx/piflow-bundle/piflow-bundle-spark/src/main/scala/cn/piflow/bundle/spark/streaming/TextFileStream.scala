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

package cn.piflow.bundle.spark.streaming

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStreamingStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

class TextFileStream
  extends ConfigurableStreamingStop[StreamingContext, DataFrame, DStream[String]] {

  override var batchDuration: Int = _
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Get text file streaming data"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var directory: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    directory = MapUtil.get(map, key = "directory").asInstanceOf[String]
    val timing = MapUtil.get(map, key = "batchDuration")
    batchDuration = if (timing == None) {
      new Integer(1)
    } else {
      timing.asInstanceOf[String].toInt
    }
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val directory = new PropertyDescriptor()
      .name("directory")
      .displayName("directory")
      .description(
        "HDFS directory to monitor for new file. Files must be written to the monitored directory by \"moving\" them from another location within the same file system ")
      .defaultValue("")
      .required(true)

    val batchDuration = new PropertyDescriptor()
      .name("batchDuration")
      .displayName("batchDuration")
      .description("the streaming batch duration")
      .defaultValue("1")
      .required(true)

    descriptor = directory :: descriptor
    descriptor = batchDuration :: descriptor
    descriptor
  }

  // TODO: change icon
  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/streaming/TextFileStream.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.StreamingGroup)
  }

  override def getDStream(ssc: StreamingContext): DStream[String] = {
    val dstream = ssc.textFileStream(directory)
    dstream
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}