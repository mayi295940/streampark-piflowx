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

class DorisRead extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "从Doris存储读取数据"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var tableDefinition: FlinkTableDefinition = _
  private var fenodes: String = _
  private var username: String = _
  private var password: String = _
  private var benodes: String = _
  private var tableIdentifier: String = _
  private var jdbcUrl: String = _
  private var batchSize: Int = _
  private var readField: String = _
  private var queryFilter: String = _
  private var properties: Map[String, Any] = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns, ifNotExists, tableComment, partitionStatement, asSelectStatement, likeStatement) =
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
         |'connector' = 'doris',
         |'fenodes' = '$fenodes',
         |'username' = '$username',
         |'password' = '$password',
         |$getWithConf
         |'table.identifier' = '$tableIdentifier'
         |)
         |$asSelectStatement
         |$likeStatement
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(benodes)) {
      result = s"'benodes' = '$benodes'," :: result
    }

    if (StringUtils.isNotBlank(jdbcUrl)) {
      result = s"'jdbc-url' = '$jdbcUrl'," :: result
    }

    if (StringUtils.isNotBlank(readField)) {
      result = s"'doris.read.field' = '$readField'," :: result
    }

    if (StringUtils.isNotBlank(queryFilter)) {
      result = s"'doris.filter.query' = '$queryFilter'," :: result
    }

    if (batchSize > 0) {
      result = s"'doris.batch.size' = '$batchSize'," :: result
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
    queryFilter = MapUtil.get(map, "queryFilter", "").asInstanceOf[String]
    readField = MapUtil.get(map, "readField", "").asInstanceOf[String]
    batchSize = MapUtil.get(map, "batchSize", "0").asInstanceOf[String].toInt
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
      .displayName("Fenodes")
      .description("Doris FE http地址， 支持多个地址，使用逗号分隔。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("127.0.0.1:8030")
    descriptor = fenodes :: descriptor

    val benodes = new PropertyDescriptor()
      .name("benodes")
      .displayName("Benodes")
      .description("Doris BE http地址， 支持多个地址，使用逗号分隔。")
      .defaultValue("")
      .required(false)
      .order(2)
      .example("")
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
      .displayName("Password")
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
      .language(Language.Text)
      .order(5)
      .example("db.tbl")
    descriptor = tableIdentifier :: descriptor

    val jdbcUrl = new PropertyDescriptor()
      .name("jdbcUrl")
      .displayName("JdbcUrl")
      .description("jdbc连接信息。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(6)
      .example("jdbc:mysql://127.0.0.1:9030")
    descriptor = jdbcUrl :: descriptor

    val batchSize = new PropertyDescriptor()
      .name("batchSize")
      .displayName("BatchSize")
      .description("一次从BE读取数据的最大行数。增大此数值可减少Flink与Doris之间建立连接的次数。" +
        "从而减轻网络延迟所带来的额外时间开销。")
      .defaultValue("1024")
      .required(false)
      .language(Language.Text)
      .order(7)
      .example("1024")
    descriptor = batchSize :: descriptor

    val readField = new PropertyDescriptor()
      .name("readField")
      .displayName("ReadField")
      .description("读取Doris表的列名列表，多列之间使用逗号分隔。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(8)
      .example("")
    descriptor = readField :: descriptor

    val queryFilter = new PropertyDescriptor()
      .name("queryFilter")
      .displayName("QueryFilter")
      .description("过滤读取数据的表达式，此表达式透传给Doris。Doris使用此表达式完成源端数据过滤。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(9)
      .example("age=18")
    descriptor = queryFilter :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(10)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(11)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/MysqlRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.DorisGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
