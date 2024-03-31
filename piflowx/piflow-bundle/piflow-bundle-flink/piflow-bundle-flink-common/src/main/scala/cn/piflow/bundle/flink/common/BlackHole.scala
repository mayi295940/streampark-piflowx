package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class BlackHole extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "接收所有输入记录"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()
    val tmpTable = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    val inputTable: Table = in.read()
    val columns = RowTypeUtil.getTableSchema(inputTable)

    val ddl =
      s""" CREATE TABLE $tmpTable
         | ($columns)
         | WITH (
         |'connector' = 'blackhole'
         |)
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {}

  // get descriptor of customized properties
  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .defaultValue("10")
      .required(true)
      .example("10")
    descriptor = showNumber :: descriptor
    descriptor
  }

  // get icon of Stop
  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ShowData.png")
  }

  // get group of Stop
  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
