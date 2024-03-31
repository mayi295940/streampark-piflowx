package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class Route extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Route data by custom properties,key is port,value is filter"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.RoutePort)

  override val isCustomized: Boolean = true

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val df = in.read().cache()

    if (this.customizedProperties != null || this.customizedProperties.nonEmpty) {
      val it = this.customizedProperties.keySet.iterator
      while (it.hasNext) {
        val port = it.next()
        val filterCondition = MapUtil.get(this.customizedProperties, port).asInstanceOf[String]
        val filterDf = df.filter(filterCondition)
        out.write(port, filterDf)
      }
    }
    out.write(df);
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

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
