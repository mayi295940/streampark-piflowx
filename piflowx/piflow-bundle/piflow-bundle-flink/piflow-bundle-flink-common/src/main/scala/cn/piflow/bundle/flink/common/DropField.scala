package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.{ApiExpression, Table}
import org.apache.flink.table.api.Expressions.$

class DropField extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Delete one or more columns"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val inputTable = in.read()

    val field = columnNames.split(Constants.COMMA).map(x => x.trim)

    val array = new Array[ApiExpression](field.length)

    for (x <- field.indices) {
      array(x) = $(field(x))
    }

    val resultTable = inputTable.dropColumns(array: _*)

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
      .description(
        "Fill in the columns you want to delete,multiple columns names separated by commas")
      .defaultValue("")
      .required(true)
      .example("id")
    descriptor = columnNames :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/DropColumnNames.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
