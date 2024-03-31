package cn.piflow.bundle.flink.doris

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

class DorisWrite extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "往Doris存储写入数据"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var tableDefinition: FlinkTableDefinition = _
  private var fenodes: String = _
  private var username: String = _
  private var password: String = _
  private var benodes: String = _
  private var tableIdentifier: String = _
  private var jdbcUrl: String = _
  private var sinkLabelPrefix: String = _
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
         |'connector' = 'doris',
         |'fenodes' = '$fenodes',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'table.identifier' = '$tableIdentifier',
         |'sink.label-prefix' = '$sinkLabelPrefix'
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

    if (StringUtils.isNotBlank(benodes)) {
      result = s"'benodes' = '$benodes'," :: result
    }

    if (StringUtils.isNotBlank(jdbcUrl)) {
      result = s"'jdbc-url' = '$jdbcUrl'," :: result
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
    fenodes = MapUtil.get(map, "fenodes").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    benodes = MapUtil.get(map, "benodes", "").asInstanceOf[String]
    tableIdentifier = MapUtil.get(map, "tableIdentifier").asInstanceOf[String]
    jdbcUrl = MapUtil.get(map, "jdbcUrl", "").asInstanceOf[String]
    sinkLabelPrefix = MapUtil.get(map, "sinkLabelPrefix").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val fenodes = new PropertyDescriptor()
      .name("fenodes")
      .displayName("fenodes")
      .description("Doris FE http地址， 支持多个地址，使用逗号分隔。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("127.0.0.1:8030")
    descriptor = fenodes :: descriptor

    val benodes = new PropertyDescriptor()
      .name("benodes")
      .displayName("benodes")
      .description("Doris BE http地址， 支持多个地址，使用逗号分隔。")
      .defaultValue("")
      .required(false)
      .order(2)
      .example("127.0.0.1:8030")
    descriptor = benodes :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("访问Doris的用户名。")
      .defaultValue("")
      .required(true)
      .order(3)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("password")
      .description("访问Doris的密码。")
      .defaultValue("")
      .required(true)
      .example("12345")
      .order(4)
      .sensitive(true)
    descriptor = password :: descriptor

    val tableIdentifier = new PropertyDescriptor()
      .name("tableIdentifier")
      .displayName("TableIdentifier")
      .description("Doris表名。")
      .defaultValue("")
      .required(true)
      .order(5)
      .language(Language.Text)
      .example("db.tbl")
    descriptor = tableIdentifier :: descriptor

    val sinkLabelPrefix = new PropertyDescriptor()
      .name("sinkLabelPrefix")
      .displayName("SinkLabelPrefix")
      .description("Stream load导入使用的label前缀。2pc场景下要求全局唯一 ，用来保证Flink的EOS语义。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .order(6)
      .example("")
    descriptor = sinkLabelPrefix :: descriptor

    val jdbcUrl = new PropertyDescriptor()
      .name("jdbcUrl")
      .displayName("JdbcUrl")
      .description("jdbc连接信息。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("jdbc:mysql://127.0.0.1:9030")
    descriptor = jdbcUrl :: descriptor

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
    ImageUtil.getImage("icon/jdbc/MysqlWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.DorisGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
