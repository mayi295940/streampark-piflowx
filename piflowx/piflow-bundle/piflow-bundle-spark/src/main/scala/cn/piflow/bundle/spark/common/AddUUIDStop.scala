package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.UUID

class AddUUIDStop extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "ygang@cnic.cn"
  override val description: String = "Add UUID column"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var column: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    var df = in.read()

    spark.udf.register("generateUUID", () => UUID.randomUUID().toString.replace("-", ""))

    df.createOrReplaceTempView("temp")
    df = spark.sql(s"select generateUUID() as $column,* from temp")

    out.write(df)

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    column = MapUtil.get(map, "column").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val column = new PropertyDescriptor()
      .name("column")
      .displayName("Column")
      .description("The column is the name of the uuid you want to add")
      .defaultValue("uuid")
      .required(true)
      .example("uuid")
    descriptor = column :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/addUUID.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
