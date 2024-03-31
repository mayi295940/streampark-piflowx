package cn.piflow.bundle.spark.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{Column, DataFrame}

class SelectField extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Select data column"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val df = in.read()

    val field = columnNames.split(Constants.COMMA).map(x => x.trim)
    val columnArray: Array[Column] = new Array[Column](field.length)

    for (i <- field.indices) {
      columnArray(i) = new Column(field(i))
    }

    val finalFieldDF: DataFrame = df.select(columnArray: _*)
    out.write(finalFieldDF)
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
      .description("Select the column you want," +
        "multiple columns separated by commas")
      .defaultValue("")
      .required(true)
      .example("id,name")
    descriptor = inPorts :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
