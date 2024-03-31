package cn.piflow.bundle.spark.hive

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.beans.BeanProperty

class SelectHiveQL extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Execute select clause of hiveQL"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var hiveQL: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    import spark.sql
    val df = sql(hiveQL)
    out.write(df)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    hiveQL = MapUtil.get(map, "hiveQL").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val hiveQL = new PropertyDescriptor()
      .name("hiveQL")
      .displayName("HiveQL")
      .defaultValue("")
      .allowableValues(Set(""))
      .description("Execute select clause of hiveQL")
      .required(true)
      .language(Language.Sql)
      .example("select * from test.user1")
    descriptor = hiveQL :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/SelectHiveQL.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
