package cn.piflow.bundle.flink.cdc.mysql

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

class MysqlCdc extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "MySQL CDC连接器允许从MySQL数据库读取快照数据和增量数据。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var hostname: String = _
  private var port: Int = _
  private var username: String = _
  private var password: String = _
  private var databaseName: String = _
  private var tableName: String = _
  private var serverId: String = _
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
         |'connector' = 'mysql-cdc',
         |'hostname' = '$hostname',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'database-name' = '$databaseName',
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

    if (StringUtils.isNotBlank(serverId)) {
      result = s"'server-id' = '$serverId'," :: result
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
    serverId = MapUtil.get(map, "serverId", "").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    databaseName = MapUtil.get(map, "databaseName").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    port = MapUtil.get(map, "port", "0").asInstanceOf[String].toInt
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
      .description("MySQL数据库服务器的IP地址或主机名。")
      .defaultValue("")
      .required(true)
      .example("127.0.0.1")
      .order(1)
      .language(Language.Text)
    descriptor = hostname :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("连接到MySQL数据库服务器时要使用的MySQL用户的名称。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(2)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("连接MySQL数据库服务器时使用的密码。")
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
      .description("要监视的MySQL服务器的数据库名称。数据库名称还支持正则表达式，以监视多个与正则表达式匹配的表。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(4)
      .example("test")
    descriptor = databaseName :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("TableName")
      .description(
        "需要监视的MySQL数据库的表名。表名支持正则表达式，以监视满足正则表达式的多个表。注意：MySQL CDC 连接器在正则匹配表名时，会把用户填写的database-name，table-name通过字符串连接成一个全路径的正则表达式，然后使用该正则表达式和MySQL数据库中表的全限定名进行正则匹配。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(5)
      .example("test")
    descriptor = tableName :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("Port")
      .description("MySQL数据库服务器的整数端口号。")
      .defaultValue("3306")
      .required(false)
      .language(Language.Text)
      .order(6)
      .example("3306")
    descriptor = port :: descriptor

    val serverId = new PropertyDescriptor()
      .name("serverId")
      .displayName("ServerId")
      .description(
        "读取数据使用的 server id，server id 可以是个整数或者一个整数范围，比如 '5400' 或 '5400-5408', " +
          "建议在 'scan.incremental.snapshot.enabled' 参数为启用时，配置成整数范围。因为在当前 MySQL 集群中运行的所有 slave 节点，" +
          "标记每个 salve 节点的 id 都必须是唯一的。 所以当连接器加入 MySQL 集群作为另一个 slave 节点（并且具有唯一 id 的情况下），" +
          "它就可以读取 binlog。 默认情况下，连接器会在 5400 和 6400 之间生成一个随机数，但是我们建议用户明确指定 Server id。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("5400-5408")
    descriptor = serverId :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(8)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(9)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/mysql/MysqlCdc.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CdcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
