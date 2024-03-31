package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import org.apache.flink.table.api.Table

class MinusAll extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "MinusAll 返回右表中不存在的记录。" +
    "在左表中出现 n 次且在右表中出现 m 次的记录，在结果表中出现 (n - m) 次，" +
    "例如，也就是说结果中删掉了在右表中存在重复记录的条数的记录。两张表必须具有相同的字段类型。"

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
    val subtractTable = leftTable.minusAll(rightTable)

    out.write(subtractTable)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK
}
