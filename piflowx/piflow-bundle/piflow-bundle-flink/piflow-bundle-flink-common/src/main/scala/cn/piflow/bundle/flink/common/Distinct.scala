package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table

class Distinct extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String =
    "Duplicate based on the specified column name or all column names"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var columnNames: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val inputTable = in.read()
    val distinctTable = inputTable.distinct()
    out.write(distinctTable)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val fields = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Fill in the column names you want to duplicate," +
        "multiple columns names separated by commas,if not," +
        "all the columns will be deduplicated")
      .defaultValue("")
      .required(false)
      .example("id")
    descriptor = fields :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
