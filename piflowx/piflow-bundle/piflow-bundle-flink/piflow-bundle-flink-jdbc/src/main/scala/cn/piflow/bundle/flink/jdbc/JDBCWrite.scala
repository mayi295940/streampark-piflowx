package cn.piflow.bundle.flink.jdbc

import cn.piflow._
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.enums.DataBaseType
import cn.piflow.util.{IdGenerator, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class JDBCWrite extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "使用JDBC驱动向任意类型的关系型数据库写入数据"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var username: String = _
  private var password: String = _
  private var tableName: String = _
  private var driver: String = _
  private var tableDefinition: FlinkTableDefinition = _
  private var properties: Map[String, Any] = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    var (columns, ifNotExists, tableComment, partitionStatement, asSelectStatement, likeStatement) =
      RowTypeUtil.getTableSchema(tableDefinition)

    if (StringUtils.isEmpty(columns)) {
      columns = RowTypeUtil.getTableSchema(inputTable)
    }

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
         |'connector' = 'jdbc',
         |'url' = '$url',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'table-name' = '$tableName'
         |)
         |$asSelectStatement
         |$likeStatement
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(ddl)
    tableEnv.executeSql(ddl)

    if (
      tableDefinition.getAsSelectStatement != null &&
      StringUtils.isEmpty(tableDefinition.getAsSelectStatement.getSelectStatement)
    ) {
      inputTable.insertInto(tmpTable).execute().print()
    }

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(driver)) {
      result = s"'driver' = '$driver'," :: result
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
    url = MapUtil.get(map, "url").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    driver = MapUtil.get(map, "driver", "").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("JDBC数据库url")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("jdbc:mysql://127.0.0.1:3306/test")
    descriptor = url :: descriptor

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("用于连接到此URL的JDBC驱动类名，如果不设置，将自动从URL中推导")
      .defaultValue("")
      .required(false)
      .order(2)
      .example(DataBaseType.MySQL8.getDriverClassName)
    descriptor = driver :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("JDBC 用户名。如果指定了 'username' 和 'password' 中的任一参数，则两者必须都被指定。")
      .defaultValue("")
      .required(true)
      .order(3)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("JDBC密码")
      .defaultValue("")
      .required(true)
      .example("123456")
      .order(4)
      .sensitive(true)
    descriptor = password :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("DBTable")
      .description("连接到JDBC表的名称")
      .defaultValue("")
      .required(true)
      .order(5)
      .example("test")
    descriptor = tableName :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(6)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(7)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/MysqlWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
