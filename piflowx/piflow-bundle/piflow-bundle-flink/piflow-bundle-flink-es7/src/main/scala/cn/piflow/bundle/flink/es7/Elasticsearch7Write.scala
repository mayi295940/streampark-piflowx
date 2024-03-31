package cn.piflow.bundle.flink.es7

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

class Elasticsearch7Write extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "将数据写入到Elasticsearch-7引擎的索引中"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var tableDefinition: FlinkTableDefinition = _
  private var hosts: String = _
  private var index: String = _
  private var username: String = _
  private var password: String = _
  private var properties: Map[String, Any] = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    var (columns, ifNotExists, tableComment, partitionStatement, asSelectStatement, likeStatement) =
      RowTypeUtil.getTableSchema(tableDefinition)

    if (StringUtils.isAllEmpty(columns, asSelectStatement)) {
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
         |'connector' = 'elasticsearch-7',
         |'hosts' = '$hosts',
         |$getWithConf
         |'index' = '$index'
         |)
         |$asSelectStatement
         |$likeStatement
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)

    if (StringUtils.isEmpty(asSelectStatement)) {
      inputTable.insertInto(tmpTable).execute().print()
    }

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(username)) {
      result = s"'username' = '$username'," :: result
    }

    if (StringUtils.isNotBlank(password)) {
      result = s"'password' = '$password'," :: result
    }

    result.mkString("")
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    hosts = MapUtil.get(map, "hosts").asInstanceOf[String]
    index = MapUtil.get(map, "index").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val hosts = new PropertyDescriptor()
      .name("hosts")
      .displayName("hosts")
      .description("要连接到的一台或多台Elasticsearch主机。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("http://host_name:9092;http://host_name:9093")
    descriptor = hosts :: descriptor

    val index = new PropertyDescriptor()
      .name("index")
      .displayName("index")
      .description(
        "Elasticsearch中每条记录的索引。可以是一个静态索引（例如 'myIndex'）或一个动态索引（例如 'index-{log_ts|yyyy-MM-dd}'）。")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("myIndex")
    descriptor = index :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("用于连接Elasticsearch实例的用户名。")
      .defaultValue("")
      .required(false)
      .order(3)
      .example("")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("password")
      .description("用于连接Elasticsearch实例的密码。如果配置了username，则此选项也必须配置为非空字符串。")
      .defaultValue("")
      .required(false)
      .example("")
      .order(4)
      .sensitive(true)
    descriptor = password :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(5)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(6)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/es7/Es.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.ESGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
