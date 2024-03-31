package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table

class Union extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Union多个输入源，会删除重复记录。输入源必须具有相同的字段类型。"
  override val inportList: List[String] = List(Port.AnyPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var inports: List[String] = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    out.write(in.ports().map(in.read).reduce((x, y) => x.union(y)))
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    val inportStr = MapUtil.get(map, "inports").asInstanceOf[String]
    inports = inportStr.split(Constants.COMMA).map(x => x.trim).toList
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val inports = new PropertyDescriptor()
      .name("inports")
      .displayName("Inports")
      .description("Inports string are separated by commas")
      .defaultValue("")
      .required(true)
    descriptor = inports :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Merge.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
