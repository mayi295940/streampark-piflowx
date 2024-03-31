package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class Join extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String =
    "Table joins include full join, left join, right join and inner join"
  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var joinMode: String = _
  var correlationColumn: String = _

  // todo 1.查询的列  2.多节点join
  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val leftTable = in.read(Port.LeftPort)
    val rightTable = in.read(Port.RightPort)

    val leftTmpTable = "Left_" + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(leftTmpTable, leftTable)

    val rightTmpTable = "Right_" + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(rightTmpTable, rightTable)

    val correlationColumnArr = correlationColumn.split(Constants.COMMA).map(x => x.trim)
    val leftColumnName = leftTmpTable + Constants.DOT + correlationColumnArr(0)
    val rightColumnName = rightTmpTable + Constants.DOT + correlationColumnArr(1)
    var sql: String = null

    joinMode match {
      case "inner" =>
        sql = s"SELECT * FROM $leftTmpTable " +
          s"INNER JOIN $rightTmpTable " +
          s"ON $leftColumnName = $rightColumnName"
      case "left" =>
        sql = s"SELECT * FROM $leftTmpTable " +
          s"LEFT JOIN $rightTmpTable " +
          s"ON $leftColumnName = $rightColumnName"
      case "right" =>
        sql = s"SELECT * FROM $leftTmpTable " +
          s"RIGHT JOIN $rightTmpTable " +
          s"ON $leftColumnName = $rightColumnName"
      case "full" =>
        sql = s"SELECT * FROM $leftTmpTable " +
          s"FULL OUTER JOIN $rightTmpTable " +
          s"ON $leftColumnName = $rightColumnName"
    }

    val resultTable = tableEnv.sqlQuery(sql)
    out.write(resultTable)

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    joinMode = MapUtil.get(map, "joinMode").asInstanceOf[String]
    correlationColumn = MapUtil.get(map, "correlationColumn").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val joinMode = new PropertyDescriptor()
      .name("joinMode")
      .displayName("JoinMode")
      .description("For table associations,you can choose inner,left,right,full")
      .allowableValues(Set("inner", "left", "right", "full"))
      .defaultValue("inner")
      .required(true)
      .order(1)
      .example("left")
    descriptor = joinMode :: descriptor

    val correlationColumn = new PropertyDescriptor()
      .name("correlationColumn")
      .displayName("CorrelationColumn")
      .description("Columns associated with tables,if multiple are separated by commas")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("id,name")
    descriptor = correlationColumn :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Join.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK
}
