package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class Trager extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "ygang@cnic.cn"
  val description: String = "Upstream and downstream middleware"
  val inportList: List[String] = List(Port.AnyPort)
  val outportList: List[String] = List(Port.AnyPort)

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {}

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/trager.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
