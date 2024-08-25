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

package cn.piflow.bundle.flink.cdc.oracle

import cn.piflow._
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{IdGenerator, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class OracleCdc extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Oracle CDC连接器允许从Oracle数据库读取快照数据和增量数据。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var hostname: String = _
  private var port: Int = _
  private var username: String = _
  private var password: String = _
  private var databaseName: String = _
  private var schemaName: String = _
  private var tableName: String = _
  private var tableDefinition: FlinkTableDefinition = _
  private var properties: Map[String, Any] = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns, ifNotExists, tableComment, partitionStatement, _, _) =
      RowTypeUtil.getTableSchema(tableDefinition)

    var tmpTable: String = ""
    if (StringUtils.isEmpty(tableDefinition.getRegisterTableName)) {
      tmpTable = this.getClass.getSimpleName
        .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit
    } else {
      tmpTable += tableDefinition.getFullRegisterTableName
    }

    val ddl =
      s""" CREATE TABLE $ifNotExists $tmpTable
         | $columns
         | $tableComment
         | $partitionStatement
         | WITH (
         |'connector' = 'oracle-cdc',
         |'hostname' = '$hostname',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'database-name' = '$databaseName',
         |'schema-name' = '$schemaName',
         |'table-name' = '$tableName'
         |)
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (port > 0) {
      result = s"'port' = '$port'," :: result
    }

    if (StringUtils.isNotBlank(hostname)) {
      result = s"'hostname' = '$hostname'," :: result
    }

    if (StringUtils.isNotBlank(url)) {
      result = s"'url' = '$url'," :: result
    }

    if (properties != null && properties.nonEmpty) {
      for ((k, v) <- properties) {
        result = s"'$k' = '$v'," :: result
      }
    }

    result.mkString("")
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    hostname = MapUtil.get(map, "hostname").asInstanceOf[String]
    port = MapUtil.get(map, "port", "0").asInstanceOf[String].toInt
    url = MapUtil.get(map, "url", "").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    databaseName = MapUtil.get(map, "databaseName").asInstanceOf[String]
    schemaName = MapUtil.get(map, "schemaName").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val hostname = new PropertyDescriptor()
      .name("hostname")
      .displayName("Hostname")
      .description("Oracle数据库服务器的IP地址或主机名。如果url不为空，则可能未配置hostname，否则hostname不能为空。")
      .defaultValue("")
      .required(false)
      .order(1)
      .example("127.0.0.1")
      .language(Language.Text)
    descriptor = hostname :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("连接到Oracle数据库服务器时要使用的Oracle用户的名称。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(2)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("连接Oracle数据库服务器时使用的密码。")
      .defaultValue("")
      .required(true)
      .example("12345")
      .language(Language.Text)
      .order(3)
      .sensitive(true)
    descriptor = password :: descriptor

    val databaseName = new PropertyDescriptor()
      .name("databaseName")
      .displayName("DatabaseName")
      .description("要监视的Oracle服务器的数据库名称。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(4)
      .example("test")
    descriptor = databaseName :: descriptor

    val schemaName = new PropertyDescriptor()
      .name("schemaName")
      .displayName("Schema")
      .description("要监视的Oracle数据库的Schema。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(5)
      .example("test")
    descriptor = schemaName :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("TableName")
      .description("需要监视的Oracle数据库的表名。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(6)
      .example("test")
    descriptor = tableName :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("Port")
      .description("Oracle数据库服务器的整数端口号。")
      .defaultValue("1521")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("1521")
    descriptor = port :: descriptor

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("url")
      .description("Oracle数据库服务器的JdbcUrl。如果配置了hostname和port参数，" +
        "则默认情况下URL由SID格式的hostname port database-name连接。否则，您需要配置 URL参数。")
      .defaultValue("")
      .required(false)
      .example("")
      .order(8)
      .language(Language.Text)
    descriptor = url :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(9)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(10)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/oracle/OracleCdc.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CdcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
