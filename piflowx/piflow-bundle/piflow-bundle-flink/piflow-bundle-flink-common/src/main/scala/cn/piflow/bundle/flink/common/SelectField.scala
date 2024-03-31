package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.{ApiExpression, Table}
import org.apache.flink.table.api.Expressions.$

class SelectField extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Select data column"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val inputTable: Table = in.read()

    val field = columnNames.split(Constants.COMMA).map(x => x.trim)

    val array = new Array[ApiExpression](field.length)

    for (x <- field.indices) {
      array(x) = $(field(x))
    }

    val resultTable = inputTable.select(array: _*)

    out.write(resultTable)
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val columnNames = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Select the column you want,multiple columns separated by commas")
      .defaultValue("")
      .required(true)
      .example("id,name")
    descriptor = columnNames :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
