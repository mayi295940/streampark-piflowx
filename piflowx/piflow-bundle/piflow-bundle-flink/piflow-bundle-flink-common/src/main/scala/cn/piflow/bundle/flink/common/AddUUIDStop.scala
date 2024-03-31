package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class AddUUIDStop extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Add UUID column"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var column: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    val resultTable = tableEnv.sqlQuery(s"SELECT UUID() AS $column, * FROM $inputTable")

    out.write(resultTable)

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

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK
}
