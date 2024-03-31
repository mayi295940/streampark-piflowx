package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import org.apache.flink.table.api.Table

class Minus extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Minus 返回左表中存在且右表中不存在的记录。" +
    "左表中的重复记录只返回一次，换句话说，结果表中没有重复记录。" +
    "两张表必须具有相同的字段类型。"

  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    val descriptor: List[PropertyDescriptor] = List()

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Subtract.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val leftTable: Table = in.read(Port.LeftPort)
    val rightTable: Table = in.read(Port.RightPort)

    // todo : The MINUS operation on two unbounded tables is currently not supported.
    val subtractTable = leftTable.minus(rightTable)
    out.write(subtractTable)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK
}
