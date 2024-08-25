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

package cn.piflow.bundle.spark.kafka

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.Properties

class WriteToKafka extends ConfigurableStop[DataFrame] {

  val description: String = "Write data to kafka"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  var kafka_host: String = _
  var topic: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val df = in.read()
    val properties: Properties = new Properties()
    properties.put("bootstrap.servers", kafka_host)
    properties.put("acks", "all")
    // properties.put("retries", 0)
    // properties.put("batch.size", 16384)
    // properties.put("linger.ms", 1)
    // properties.put("buffer.memory", 33554432)
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    var producer: Producer[String, String] = new KafkaProducer[String, String](properties)

    df.collect()
      .foreach(row => {
        // var hm:util.HashMap[String,String]=new util.HashMap()
        // row.schema.fields.foreach(f=>(if(!f.name.equals(column_name)&&row.getAs(f.name)!=null)hm.put(f.name,row.getAs(f.name).asInstanceOf[String])))
        var res: List[String] = List()
        row.schema.fields.foreach(f => {
          if (row.getAs(f.name) == null) res = "None" :: res
          else {
            res = row.getAs(f.name).asInstanceOf[String] :: res
          }
        })
        val s: String = res.reverse.mkString(",")
        val record = new ProducerRecord[String, String](topic, s)
        producer.send(record)
      })
    producer.close()
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    kafka_host = MapUtil.get(map, key = "kafka_host").asInstanceOf[String]
    // port=Integer.parseInt(MapUtil.get(map,key="port").toString)
    topic = MapUtil.get(map, key = "topic").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val kafka_host = new PropertyDescriptor()
      .name("kafka_host")
      .displayName("Kafka_Host")
      .defaultValue("")
      .description("Kafka cluster address")
      .required(true)
      .example("10.0.0.101:9092,10.0.0.102:9092,10.0.0.103:9092")

    val topic = new PropertyDescriptor()
      .name("topic")
      .displayName("Topic")
      .defaultValue("")
      .description("Topics of different categories of messages processed by Kafka")
      .required(true)
      .example("hadoop")

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/WriteToKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override val authorEmail: String = "06whuxx@163.com"

  override def getEngineType: String = Constants.ENGIN_SPARK
}
