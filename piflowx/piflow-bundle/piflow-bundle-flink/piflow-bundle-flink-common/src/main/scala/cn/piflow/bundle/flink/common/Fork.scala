package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table

class Fork extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Forking data to different stops"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.AnyPort)

  var outports: List[String] = _

  override def setProperties(map: Map[String, Any]): Unit = {
    val outPortStr = MapUtil.get(map, "outports").asInstanceOf[String]
    outports = outPortStr.split(Constants.COMMA).map(x => x.trim).toList
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {
    // todo val df = in.read().cache()
    val df = in.read()
    outports.foreach(out.write(_, df));
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val outports = new PropertyDescriptor()
      .name("outports")
      .displayName("outports")
      .description("Output ports string with comma")
      .defaultValue("")
      .required(true)
    descriptor = outports :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Fork.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
