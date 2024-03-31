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

class FileWrite extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "往文件系统写入。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var path: String = _
  private var format: String = _
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

    println(ddl)
    tableEnv.executeSql(ddl)

    if (StringUtils.isEmpty(asSelectStatement)) {
      inputTable.executeInsert(tmpTable)
    }

  }

  private def getWithConf: String = {
    var result = List[String]()

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
      .example(" hdfs://server1:8020/flink/test/text.txt")
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

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .order(3)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("自定义参数")
      .description("连接器其他配置")
      .defaultValue("{}")
      .language(Language.CustomProperties)
      .order(4)
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/file/FileWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.FileGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
