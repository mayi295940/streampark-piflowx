package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.{ApiExpression, Table}
import org.apache.flink.table.api.Expressions.$

class ConvertSchema extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Change field name"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var schema: String = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val inputTable = in.read()

    val fields = schema.split(Constants.COMMA).map(x => x.trim)

    val array = new Array[ApiExpression](fields.length)

    for (x <- fields.indices) {
      val old_new: Array[String] = fields(x).split(Constants.ARROW_SIGN).map(x => x.trim)
      array(x) = $(old_new(0)).as(old_new(1))
    }

    val resultTable = inputTable.renameColumns(array: _*)

    out.write(resultTable)
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("Change column names,multiple column names are separated by commas")
      .defaultValue("")
      .required(true)
      .example("id->uuid")
    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ConvertSchema.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
