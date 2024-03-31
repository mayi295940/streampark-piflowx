package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class Filter extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Filter by condition"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var condition: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    condition = MapUtil.get(map, "condition").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val condition = new PropertyDescriptor()
      .name("condition")
      .displayName("condition")
      .description("The condition you want to filter")
      .defaultValue("name=='zhangsan'")
      .required(true)
      .example("name=='zhangsan'")
    descriptor = condition :: descriptor
    descriptor

  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    val tmpTable = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(tmpTable, inputTable)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable WHERE $condition")

    // todo inputTable.where()

    out.write(resultTable)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK
}
