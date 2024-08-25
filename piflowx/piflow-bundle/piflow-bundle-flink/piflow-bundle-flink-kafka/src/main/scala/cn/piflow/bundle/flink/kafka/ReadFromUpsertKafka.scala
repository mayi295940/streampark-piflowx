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

import cn.piflow._
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{IdGenerator, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class ReadFromUpsertKafka extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "upsert方式从Kafka topic中读取数据"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var kafka_host: String = _
  private var topic: String = _
  private var tableDefinition: FlinkTableDefinition = _
  private var key_format: String = _
  private var value_format: String = _
  private var value_fields_include: String = _
  private var key_fields_prefix: String = _
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
    val ddl =
      s""" CREATE TABLE $ifNotExists $tableName
         | $columns
         | $tableComment
         | $partitionStatement
         | WITH (
         |'connector' = 'upsert-kafka',
         |'properties.bootstrap.servers' = '$kafka_host',
         | $getWithConf
         |'key.format' = '$key_format',
         |'value.format' = '$value_format',
         |'value.fields-include' = '$value_fields_include',
         |'topic' = '$topic'
         |)
         |$asSelectStatement
         |$likeStatement
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(ddl)
    tableEnv.executeSql(ddl)

    val query = s"SELECT * FROM $tableName"
    out.write(tableEnv.sqlQuery(query))

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(key_fields_prefix)) {
      result = s"'key.fields-prefix' = '$key_fields_prefix'," :: result
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
    key_format = MapUtil.get(map, key = "key_format").asInstanceOf[String]
    value_format = MapUtil.get(map, key = "value_format").asInstanceOf[String]
    value_fields_include = MapUtil.get(map, key = "value_fields_include").asInstanceOf[String]
    key_fields_prefix = MapUtil.get(map, key = "key_fields_prefix", "").asInstanceOf[String]
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
      .description("用于读取Kafka topic名称。")
      .defaultValue("")
      .example("topic-1")
      .order(2)
      .required(true)

    val key_format = new PropertyDescriptor()
      .name("key_format")
      .displayName("keyFormat")
      .description("用于对Kafka消息中key部分反序列化的格式。key字段由PRIMARY KEY语法指定。")
      .allowableValues(Set("json", "csv", "avro"))
      .defaultValue("")
      .example("json")
      .order(3)
      .required(true)

    val value_format = new PropertyDescriptor()
      .name("value_format")
      .displayName("ValueFormat")
      .description("用于对Kafka消息中value部分反序列化的格式")
      .allowableValues(Set("json", "csv", "avro"))
      .defaultValue("")
      .example("json")
      .order(4)
      .required(true)

    val value_fields_include = new PropertyDescriptor()
      .name("value_fields_include")
      .displayName("ValueFieldsInclude")
      .description(
        "控制哪些字段应该出现在 value 中。" +
          "可取值：\n" +
          "ALL：消息的 value 部分将包含 schema 中所有的字段包括定义为主键的字段。\n" +
          "EXCEPT_KEY：记录的 value 部分包含 schema 的所有字段，定义为主键的字段除外。")
      .allowableValues(Set("ALL", "EXCEPT_KEY"))
      .example("ALL")
      .defaultValue("ALL")
      .order(5)
      .required(true)

    val key_fields_prefix = new PropertyDescriptor()
      .name("key_fields_prefix")
      .displayName("KeyFieldsPrefix")
      .description(
        "为所有消息键（Key）格式字段指定自定义前缀，以避免与消息体（Value）格式字段重名。" +
          "默认情况下前缀为空。 如果定义了前缀，表结构和配置项 'key.fields' 都需要使用带前缀的名称。 " +
          "当构建消息键格式字段时，前缀会被移除，消息键格式将会使用无前缀的名称。 " +
          "请注意该配置项要求必须将 'value.fields-include' 配置为 'EXCEPT_KEY'。")
      .defaultValue("")
      .order(6)
      .required(false)

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(7)
      .example("")
      .required(true)

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description(
        "该选项可以传递任意的Kafka参数。选项的后缀名必须匹配定义在Kafka参数文档中的参数名。" +
          " Flink 会自动移除 选项名中的 \"properties.\" 前缀，并将转换后的键名以及值传入KafkaClient。 " +
          "例如，你可以通过 'properties.allow.auto.create.topics' = 'false' 来禁止自动创建 topic。 " +
          "但是，某些选项，例如'key.deserializer'和'value.deserializer'是不允许通过该方式传递参数，因为Flink会重写这些参数的值。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(8)
      .required(false)

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = key_format :: descriptor
    descriptor = value_format :: descriptor
    descriptor = value_fields_include :: descriptor
    descriptor = key_fields_prefix :: descriptor
    descriptor = tableDefinition :: descriptor
    descriptor = properties :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/ReadFromKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
