package cn.piflow.bundle.spark.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class DropField extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "ygang@cnic.cn"
  val description: String = "Delete one or more columns"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    var df = in.read()

    val field = columnNames.split(",").map(x => x.trim)
    for (x <- field.indices) {
      df = df.drop(field(x))
    }

    out.write(df)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val inPorts = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Fill in the columns you want to delete," +
        "multiple columns names separated by commas")
      .defaultValue("")
      .required(true)
      .example("id")
    descriptor = inPorts :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/DropColumnNames.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
