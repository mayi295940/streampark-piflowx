package cn.piflow.bundle.flink.cdc.postgres

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

class PostgresCdc extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Postgres CDC连接器允许从PostgreSQL数据库读取快照数据和增量数据。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var hostname: String = _
  private var port: Int = _
  private var username: String = _
  private var password: String = _
  private var databaseName: String = _
  private var schemaName: String = _
  private var tableName: String = _
  private var slotName: String = _
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
         |'connector' = 'postgres-cdc',
         |'hostname' = '$hostname',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'database-name' = '$databaseName',
         |'schema-name' = '$schemaName',
         |'slot.name' = '$slotName',
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
    username = MapUtil.get(map, "username").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    databaseName = MapUtil.get(map, "databaseName").asInstanceOf[String]
    schemaName = MapUtil.get(map, "schemaName").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    slotName = MapUtil.get(map, "slotName").asInstanceOf[String]
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
      .description("PostgreSQL数据库服务器的IP地址或主机名。")
      .defaultValue("")
      .required(true)
      .example("127.0.0.1")
      .order(1)
      .language(Language.Text)
    descriptor = hostname :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("连接到PostgreSQL数据库服务器时要使用的用户名。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(2)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("连接PostgreSQL数据库服务器时使用的密码。")
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
      .description("要监视的PostgreSQL服务器的数据库名称。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(4)
      .example("test")
    descriptor = databaseName :: descriptor

    val schemaName = new PropertyDescriptor()
      .name("schemaName")
      .displayName("Schema")
      .description("要监视的PostgreSQL数据库的Schema。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(5)
      .example("public")
    descriptor = schemaName :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("TableName")
      .description("需要监视的PostgreSQL数据库的表名。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(6)
      .example("test")
    descriptor = tableName :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("Port")
      .description("PostgreSQL数据库服务器的整数端口号。")
      .defaultValue("5432")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("5432")
    descriptor = port :: descriptor

    val slotName = new PropertyDescriptor()
      .name("slotName")
      .displayName("SlotName")
      .description(
        "The name of the PostgreSQL logical decoding slot that was created for streaming changes from a particular plug-in for a particular database/schema. The server uses this slot to stream events to the connector that you are configuring." +
          "Slot names must conform to PostgreSQL replication slot naming rules, which state: \"Each replication slot has a name, which can contain lower-case letters, numbers, and the underscore character.")
      .defaultValue("")
      .required(true)
      .example("")
      .order(8)
      .language(Language.Text)
    descriptor = slotName :: descriptor

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
    ImageUtil.getImage("icon/postgres/PostgresCdc.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CdcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
