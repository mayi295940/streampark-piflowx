package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class Merge extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Merge data into one stop"
  val inportList: List[String] = List(Port.AnyPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var inports: List[String] = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    out.write(in.ports().map(in.read).reduce((x, y) => x.union(y)));
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
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

  override def getEngineType: String = Constants.ENGIN_SPARK

}
