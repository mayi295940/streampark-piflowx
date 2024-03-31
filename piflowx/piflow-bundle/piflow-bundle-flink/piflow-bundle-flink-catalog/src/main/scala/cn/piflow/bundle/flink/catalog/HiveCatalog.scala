package cn.piflow.bundle.flink.catalog

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class HiveCatalog extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "通过JDBC协议将Flink连接到关系数据库,目前支持Postgres Catalog和MySQL Catalog。"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var catalogName: String = _
  private var hiveConfDir: String = _
  private var hadoopConDir: String = _
  private var defaultDatabase: String = _
  private var hiveVersion: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val ddl =
      s"""CREATE CATALOG $catalogName WITH (
         |$getWithConf
         |'type' = 'hive'
         |)
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)
  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(hiveConfDir)) {
      result = s"'hive-conf-dir' = '$hiveConfDir'," :: result
    }

    if (StringUtils.isNotBlank(hadoopConDir)) {
      result = s"'hadoop-conf-dir' = '$hadoopConDir'," :: result
    }

    if (StringUtils.isNotBlank(defaultDatabase)) {
      result = s"'default-database' = '$defaultDatabase'," :: result
    }

    if (StringUtils.isNotBlank(hiveVersion)) {
      result = s"'hive-version' = '$hiveVersion'," :: result
    }

    result.mkString("")
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    catalogName = MapUtil.get(map, "catalogName").asInstanceOf[String]
    hiveConfDir = MapUtil.get(map, "hiveConfDir", "").asInstanceOf[String]
    hadoopConDir = MapUtil.get(map, "hadoopConDir", "").asInstanceOf[String]
    defaultDatabase = MapUtil.get(map, "defaultDatabase", "").asInstanceOf[String]
    hiveVersion = MapUtil.get(map, "hiveVersion", "").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val catalogName = new PropertyDescriptor()
      .name("catalogName")
      .displayName("CatalogName")
      .description("catalog名称。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("my_catalog")
    descriptor = catalogName :: descriptor

    val hiveConfDir = new PropertyDescriptor()
      .name("hiveConfDir")
      .displayName("HiveConfDir")
      .description(
        "指向包含hive-site.xml目录的URI。该URI必须是Hadoop文件系统所支持的类型。 如果指定一个相对URI，即不包含scheme，则默认为本地文件系统。如果该参数没有指定，我们会在class path下查找hive-site.xml。")
      .defaultValue("")
      .required(false)
      .order(2)
      .example("/opt/hive-conf")
    descriptor = hiveConfDir :: descriptor

    val hadoopConDir = new PropertyDescriptor()
      .name("hadoopConDir")
      .displayName("HadoopConDir")
      .description(
        "Hadoop配置文件目录的路径。目前仅支持本地文件系统路径。推荐使用HADOOP_CONF_DIR环境变量来指定Hadoop配置。因此仅在环境变量不满足您的需求时再考虑使用该参数，例如当您希望为每个HiveCatalog单独设置Hadoop配置时。")
      .defaultValue("")
      .required(false)
      .order(3)
      .example("/opt/hadoop-conf")
    descriptor = hadoopConDir :: descriptor

    val defaultDatabase = new PropertyDescriptor()
      .name("defaultDatabase")
      .displayName("DefaultDatabase")
      .description("当一个catalog被设为当前catalog时，所使用的默认当前database。")
      .defaultValue("")
      .required(false)
      .order(4)
      .example("my_database")
    descriptor = defaultDatabase :: descriptor

    val hiveVersion = new PropertyDescriptor()
      .name("hiveVersion")
      .displayName("HiveVersion")
      .description("HiveCatalog能够自动检测使用的Hive版本。建议不要手动设置Hive版本，除非自动检测机制失败。")
      .defaultValue("")
      .required(false)
      .order(5)
      .example("3.1.3")
    descriptor = hiveVersion :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/catalog/JdbcCatalog.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CatalogGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
