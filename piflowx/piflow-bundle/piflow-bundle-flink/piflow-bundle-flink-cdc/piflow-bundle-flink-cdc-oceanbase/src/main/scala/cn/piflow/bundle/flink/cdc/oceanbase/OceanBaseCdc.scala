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

package cn.piflow.bundle.flink.cdc.oceanbase

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

class OceanBaseCdc extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "OceanBase CDC连接器允许从OceanBase读取快照数据和增量数据。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var scanStartupMode: String = _
  private var username: String = _
  private var password: String = _
  private var tenantName: String = _
  private var logProxyHost: String = _
  private var logProxyPort: Int = _
  private var hostname: String = _
  private var port: Int = _
  private var databaseName: String = _
  private var tableName: String = _
  private var tableList: String = _
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
         |'connector' = 'oceanbase-cdc',
         |$getWithConf
         |'scan.startup.mode' = '$scanStartupMode',
         |'username' = '$username',
         |'password' = '$password',
         |'tenant-name' = '$tenantName',
         |'logproxy.host' = '$logProxyHost',
         |'logproxy.port' = '$logProxyPort'
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

    if (StringUtils.isNotBlank(hostname)) {
      result = s"'hostname' = '$hostname'," :: result
    }

    if (port > 0) {
      result = s"'port' = '$port'," :: result
    }

    if (StringUtils.isNotBlank(databaseName)) {
      result = s"'database-name' = '$databaseName'," :: result
    }

    if (StringUtils.isNotBlank(tableName)) {
      result = s"'table-name' = '$tableName'," :: result
    }

    if (StringUtils.isNotBlank(tableList)) {
      result = s"'table-list' = '$tableList'," :: result
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
    scanStartupMode = MapUtil.get(map, "scanStartupMode", "").asInstanceOf[String]
    username = MapUtil.get(map, "username").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    tenantName = MapUtil.get(map, "tenantName").asInstanceOf[String]
    logProxyHost = MapUtil.get(map, "logProxyHost").asInstanceOf[String]
    logProxyPort = MapUtil.get(map, "logProxyPort").asInstanceOf[String].toInt
    hostname = MapUtil.get(map, "hostname", "").asInstanceOf[String]
    port = MapUtil.get(map, "port", "2881").asInstanceOf[String].toInt
    databaseName = MapUtil.get(map, "databaseName", "").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName", "").asInstanceOf[String]
    tableList = MapUtil.get(map, "tableList", "").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val scanStartupMode = new PropertyDescriptor()
      .name("scanStartupMode")
      .displayName("STARTUP_MODE")
      .description("指定OceanBase CDC消费者的启动模式。")
      .allowableValues(Set("initial", "latest-offset", "group-offsets", "timestamp"))
      .defaultValue("")
      .example("initial")
      .order(1)
      .required(true)
    descriptor = scanStartupMode :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("连接OceanBase数据库的用户的名称。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(2)
      .example("user@test_tenant#cluster_name")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("连接OceanBase数据库时使用的密码。")
      .defaultValue("")
      .required(true)
      .example("12345")
      .language(Language.Text)
      .order(3)
      .sensitive(true)
    descriptor = password :: descriptor

    val tenantName = new PropertyDescriptor()
      .name("tenantName")
      .displayName("TenantName")
      .description("待监控OceanBase数据库的租户名，应该填入精确值。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(4)
      .example("test_tenant")
    descriptor = tenantName :: descriptor

    val logProxyHost = new PropertyDescriptor()
      .name("logProxyHost")
      .displayName("LogProxyHost")
      .description("OceanBase日志代理服务的IP地址或主机名。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(5)
      .example("127.0.0.1")
    descriptor = logProxyHost :: descriptor

    val logProxyPort = new PropertyDescriptor()
      .name("logProxyPort")
      .displayName("LogProxyPort")
      .description("OceanBase日志代理服务的端口号。")
      .defaultValue("2983")
      .required(true)
      .language(Language.Text)
      .order(6)
      .example("2983")
    descriptor = logProxyPort :: descriptor

    val hostname = new PropertyDescriptor()
      .name("hostname")
      .displayName("hostname")
      .description("OceanBase数据库或OceanBase代理ODP的IP地址或主机名。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("127.0.0.1")
    descriptor = hostname :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("port")
      .description("OceanBase数据库服务器的整数端口号。可以是 OceanBase服务器的SQL端口号（默认值为 2881）" +
        "或OceanBase代理服务的端口号（默认值为 2883）")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(8)
      .example("2881")
    descriptor = port :: descriptor

    val databaseName = new PropertyDescriptor()
      .name("databaseName")
      .displayName("DatabaseName")
      .description("待监控 OceanBase 数据库的数据库名，应该是正则表达式，该选项只支持和 'initial' 模式一起使用。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(9)
      .example("^test_db$")
    descriptor = databaseName :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("TableName")
      .description("待监控OceanBase数据库的表名，应该是正则表达式，该选项只支持和 'initial' 模式一起使用。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(10)
      .example("^orders$")
    descriptor = tableName :: descriptor

    val tableList = new PropertyDescriptor()
      .name("tableList")
      .displayName("TableList")
      .description("待监控OceanBase数据库的全路径的表名列表，逗号分隔。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(11)
      .example("db1.table1, db2.table2")
    descriptor = tableList :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(12)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(13)
      .required(false)
    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/oceanbase/OceanBaseCdc.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CdcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
