package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class Fork extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Forking data to different stops"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.AnyPort)

  var outports: List[String] = _

  override def setProperties(map: Map[String, Any]): Unit = {
    val outportStr = MapUtil.get(map, "outports").asInstanceOf[String]
    outports = outportStr.split(",").map(x => x.trim).toList
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {
    val df = in.read().cache()
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

  override def getEngineType: String = Constants.ENGIN_SPARK

}
