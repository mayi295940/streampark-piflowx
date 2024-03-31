package cn.piflow.bundle.spark.hive

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.beans.BeanProperty

class PutHiveStreaming extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Save data to hive"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var database: String = _
  var table: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val inDF = in.read()

    val dfTempTable = table + "_temp"
    inDF.createOrReplaceTempView(dfTempTable)
    spark.sql(
      "insert into " + database + "." + table +
        " select * from " + dfTempTable)

  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]) = {
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
      .required(true)
      .example("test")
    descriptor = database :: descriptor

    val table = new PropertyDescriptor()
      .name("table")
      .displayName("Table")
      .description("The table name")
      .defaultValue("")
      .required(true)
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

  override def getEngineType: String = Constants.ENGIN_SPARK

}
