package cn.piflow.bundle.spark.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class Filter extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Filter by condition"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var condition: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    condition = MapUtil.get(map, "condition").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val condition = new PropertyDescriptor()
      .name("condition")
      .displayName("condition")
      .description("The condition you want to filter")
      .defaultValue("name=='zhangsan'")
      .required(true)
      .example("name=='zhangsan'")
    descriptor = condition :: descriptor
    descriptor

  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    out.write(in.read().filter(condition))
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
