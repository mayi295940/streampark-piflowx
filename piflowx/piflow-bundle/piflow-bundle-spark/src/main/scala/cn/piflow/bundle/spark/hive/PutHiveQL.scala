package cn.piflow.bundle.spark.hive

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.HdfsUtil
import org.apache.spark.sql.{DataFrame, SparkSession}

class PutHiveQL extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xiaoxiao@cnic.cn"
  val description: String = "Execute hiveQL script"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var database: String = _
  private var hiveQL_Path: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    import spark.sql

    sql(sqlText = "use " + database)
    val sqlString: String = HdfsUtil.getLines(hiveQL_Path)
    sqlString
      .split(";")
      .foreach(
        s => {
          println("Sql is " + s)
          sql(s)
        })
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    hiveQL_Path = MapUtil.get(map, "hiveQL_Path").asInstanceOf[String]
    database = MapUtil.get(map, "database").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val hiveQL_Path = new PropertyDescriptor()
      .name("hiveQL_Path")
      .displayName("HiveQL_Path")
      .description("The hdfs path of the hiveQL file")
      .defaultValue("")
      .required(true)
      .example("hdfs://192.168.3.138:8020/test/PutHiveQL.hiveql")
    descriptor = hiveQL_Path :: descriptor

    val database = new PropertyDescriptor()
      .name("database")
      .displayName("DataBase")
      .description("The database name which the hiveQL will execute on")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = database :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/PutHiveQL.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
