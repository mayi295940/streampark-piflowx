package cn.piflow.bundle.flink.hive

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog

class PutHiveStreaming extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Save data to hive"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var database: String = _
  var table: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()
    tableEnv.createTemporaryView("inputTable", inputTable)

    // connect hive by flink
    val name = "myhive"
    val defaultDatabase = "mydatabase"
    val hiveConfDir = "/piflow-configure/hive-conf"

    val hive = new HiveCatalog(name, defaultDatabase, hiveConfDir)
    tableEnv.registerCatalog("myhive", hive)

    // set the HiveCatalog as the current catalog of the session
    tableEnv.useCatalog("myhive")
    tableEnv.useDatabase(database)

    // save data to hive
    tableEnv.executeSql(s"INSERT INTO $database.$table SELECT * FROM $inputTable")
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    database = MapUtil.get(map, "database").asInstanceOf[String]
    table = MapUtil.get(map, "table").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val database = new PropertyDescriptor()
      .name("database")
      .displayName("DataBase")
      .description("The database name")
      .defaultValue("")
      .order(1)
      .required(true)
    descriptor = database :: descriptor

    val table = new PropertyDescriptor()
      .name("table")
      .displayName("Table")
      .description("The table name")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("stream")
    descriptor = table :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/PutHiveStreaming.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
