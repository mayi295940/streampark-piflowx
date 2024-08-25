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
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StructField, StructType}

import java.util.Properties

class ReadFromKafka extends ConfigurableStop[DataFrame] {

  val description: String = "Read data from kafka"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  var kafka_host: String = _
  var topic: String = _
  var schema: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val properties: Properties = new Properties()
    import org.apache.kafka.clients.producer.ProducerConfig
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_host)
    properties.put("acks", "all")
    properties.put("group.id", "mmmm")
    properties.put("enable.auto.commit", "true")
    properties.put("max.poll.records", "1000")
    properties.put("auto.offset.reset", "earliest")
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    // properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    // var res:List[Array[String]]=List()
    // val topicName=topic
    var res: List[Row] = List()

    val dfSchema = StructType(
      schema
        .split(",")
        .map(x => x.trim)
        .map(f => StructField(f, org.apache.spark.sql.types.StringType, nullable = true)))

    val consumer = new KafkaConsumer[String, String](properties)
    consumer.subscribe(java.util.Arrays.asList(topic, "finally"))
    val records: ConsumerRecords[String, String] = consumer.poll(1000)

    val it = records.records(topic).iterator()
    while (it.hasNext) {
      // println(it.next().value())
      val row = Row.fromSeq(it.next().value().split(",").toSeq)
      res = row :: res
    }

    import spark.implicits._
    val rdd = spark.sparkContext.parallelize(res)
    // val newRdd=rdd.map(line=>Row.fromSeq(line.toSeq))
    val df = spark.sqlContext.createDataFrame(rdd, dfSchema)
    // df.show(20)
    out.write(df)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    kafka_host = MapUtil.get(map, key = "kafka_host").asInstanceOf[String]
    topic = MapUtil.get(map, key = "topic").asInstanceOf[String]
    schema = MapUtil.get(map, key = "schema").asInstanceOf[String]
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

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .defaultValue("")
      .description("Specify the schema of the dataframe")
      .required(true)
      .example("id,name")

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = schema :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/ReadFromKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override val authorEmail: String = "06whuxx@163.com"

  override def getEngineType: String = Constants.ENGIN_SPARK

}
