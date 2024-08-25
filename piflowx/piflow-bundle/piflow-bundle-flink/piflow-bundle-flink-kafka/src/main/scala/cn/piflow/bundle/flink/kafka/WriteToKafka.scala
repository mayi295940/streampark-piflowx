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

package cn.piflow.bundle.flink.kafka

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{IdGenerator, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class WriteToKafka extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Write data to kafka"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var kafka_host: String = _
  private var topic: String = _
  private var tableDefinition: FlinkTableDefinition = _
  private var format: String = _
  private var properties: Map[String, Any] = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns, ifNotExists, tableComment, partitionStatement, asSelectStatement, likeStatement) =
      RowTypeUtil.getTableSchema(tableDefinition)

    var tableName: String = ""
    if (StringUtils.isEmpty(tableDefinition.getRegisterTableName)) {
      tableName = this.getClass.getSimpleName
        .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit
    } else {
      tableName += tableDefinition.getFullRegisterTableName
    }

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $ifNotExists $tableName
         | $columns
         | $tableComment
         | $partitionStatement
         | WITH (
         |'connector' = 'kafka',
         |'properties.bootstrap.servers' = '$kafka_host',
         |'topic' = '$topic',
         | $getWithConf
         |'format' = '$format'
         |)
         |$asSelectStatement
         |$likeStatement
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    if (StringUtils.isEmpty(asSelectStatement)) {
      val inputTable = in.read()
      inputTable.executeInsert(tableName)
    }
  }

  private def getWithConf: String = {

    var result = List[String]()

    if (StringUtils.isNotBlank(topic)) {
      result = s"'topic' = '$topic'," :: result
    }

    if (properties != null && properties.nonEmpty) {
      for ((k, v) <- properties) {
        result = s"'$k' = '$v'," :: result
      }
    }

    result.mkString("")
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    kafka_host = MapUtil.get(map, key = "kafka_host").asInstanceOf[String]
    topic = MapUtil.get(map, key = "topic").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    format = MapUtil.get(map, key = "format").asInstanceOf[String]
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val kafka_host = new PropertyDescriptor()
      .name("kafka_host")
      .displayName("KAFKA_HOST")
      .description("逗号分隔的Kafka broker列表。")
      .defaultValue("")
      .example("127.0.0.1:9092")
      .order(1)
      .required(true)

    val topic = new PropertyDescriptor()
      .name("topic")
      .displayName("TOPIC")
      .description("写入的topic名。注意不支持topic列表。")
      .defaultValue("")
      .example("test")
      .order(2)
      .required(true)

    val format = new PropertyDescriptor()
      .name("format")
      .displayName("FORMAT")
      .description("用来序列化Kafka消息的格式。注意：该配置项和 'value.format' 二者必需其一。")
      .allowableValues(
        Set(
          "json",
          "csv",
          "avro",
          "parquet",
          "orc",
          "raw",
          "protobuf",
          "debezium-json",
          "canal-json",
          "maxwell-json",
          "ogg-json"))
      .defaultValue("")
      .order(3)
      .required(true)

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(100)
      .example("")
      .order(4)
      .required(true)

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("Kafka source连接器其他配置")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(5)
      .required(false)

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = format :: descriptor
    descriptor = tableDefinition :: descriptor
    descriptor = properties :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/WriteToKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
