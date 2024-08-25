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
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.DataFrame
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

class KafkaStream extends ConfigurableStreamingStop[StreamingContext, DataFrame, DStream[String]] {

  override var batchDuration: Int = _
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Read data from kafka"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var brokers: String = _
  var groupId: String = _
  var topics: Array[String] = _

  override def setProperties(map: Map[String, Any]): Unit = {
    brokers = MapUtil.get(map, key = "brokers").asInstanceOf[String]
    groupId = MapUtil.get(map, key = "groupId").asInstanceOf[String]
    topics = MapUtil.get(map, key = "topics").asInstanceOf[String].split(",").map(x => x.trim)
    val timing = MapUtil.get(map, key = "batchDuration")
    batchDuration = if (timing == None) new Integer(1) else timing.asInstanceOf[String].toInt
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val brokers = new PropertyDescriptor()
      .name("brokers")
      .displayName("brokers")
      .description("kafka brokers, seperated by ','")
      .defaultValue("")
      .required(true)

    val groupId = new PropertyDescriptor()
      .name("groupId")
      .displayName("groupId")
      .description("kafka consumer group")
      .defaultValue("group")
      .required(true)

    val topics = new PropertyDescriptor()
      .name("topics")
      .displayName("topics")
      .description("kafka topics")
      .defaultValue("")
      .required(true)

    val batchDuration = new PropertyDescriptor()
      .name("batchDuration")
      .displayName("batchDuration")
      .description("the streaming batch duration")
      .defaultValue("1")
      .required(true)

    descriptor = brokers :: descriptor
    descriptor = groupId :: descriptor
    descriptor = topics :: descriptor
    descriptor = batchDuration :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/streaming/KafkaStream.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.StreamingGroup)
  }

  override def getDStream(ssc: StreamingContext): DStream[String] = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))
    stream.map(record => record.key() + "," + record.value())
    // stream.asInstanceOf[DStream[ConsumerRecord]]
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK
}
