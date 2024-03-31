package cn.piflow.bundle.flink.file

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

class FileRead extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "从文件系统读取。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var path: String = _
  private var format: String = _
  private var monitorInterval: String = _
  private var tableDefinition: FlinkTableDefinition = _
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
         |'connector' = 'filesystem',
         |'path' = '$path',
         |$getWithConf
         |'format' = '$format'
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

    if (StringUtils.isNotBlank(monitorInterval)) {
      result = s"'source.monitor-interval' = '$monitorInterval'," :: result
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
    path = MapUtil.get(map, "path").asInstanceOf[String]
    format = MapUtil.get(map, "format").asInstanceOf[String]
    monitorInterval = MapUtil.get(map, "monitorInterval", "").asInstanceOf[String]
    val tableDefinitionMap =
      MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition =
      JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val path = new PropertyDescriptor()
      .name("path")
      .displayName("path")
      .description("文件路径。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("hdfs://server1:8020/flink/test/text.txt")
    descriptor = path :: descriptor

    val format = new PropertyDescriptor()
      .name("format")
      .displayName("FORMAT")
      .description("文件系统连接器支持format。")
      .allowableValues(
        Set("json", "csv", "avro", "parquet", "orc", "raw", "debezium-json", "canal-json"))
      .defaultValue("")
      .example("json")
      .order(2)
      .required(true)
    descriptor = format :: descriptor

    val monitorInterval = new PropertyDescriptor()
      .name("monitorInterval")
      .displayName("MonitorInterval")
      .description(
        "设置新文件的监控时间间隔，并且必须设置 > 0 的值。 每个文件都由其路径唯一标识，一旦发现新文件，就会处理一次。 已处理的文件在source的整个生命周期内存储在state中，因此，source的state在checkpoint和savepoint时进行保存。更短的时间间隔意味着文件被更快地发现，但也意味着更频繁地遍历文件系统/对象存储。 如果未设置此配置选项，则提供的路径仅被扫描一次，因此源将是有界的。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .order(3)
      .example("test")
    descriptor = monitorInterval :: descriptor

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(4)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(5)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/file/FileRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.FileGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
