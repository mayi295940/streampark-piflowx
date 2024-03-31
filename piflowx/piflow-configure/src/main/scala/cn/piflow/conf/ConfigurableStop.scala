package cn.piflow.conf

import cn.piflow.Stop
import cn.piflow.conf.bean.PropertyDescriptor

abstract class ConfigurableStop[DataType] extends Stop[DataType] {

  val authorEmail: String
  val description: String
  // PortEnum.AnyPort: any port; PortEnum.DefaultPort: default port
  // null: no port; userdefinePort: port1, port2...
  val inportList: List[String] // = List(PortEnum.DefaultPort.toString)
  val outportList: List[String] // = List(PortEnum.DefaultPort.toString)

  // Have customized properties or not
  val isCustomized = false
  val customizedAllowKey = List[String]()
  val customizedAllowValue = List[String]()

  var customizedProperties: Map[String, String] = _

  def setProperties(map: Map[String, Any]): Unit

  def getPropertyDescriptor(): List[PropertyDescriptor]

  def getIcon(): Array[Byte]

  def getGroup(): List[String]

  def setCustomizedProperties(customizedProperties: Map[String, String]): Unit = {
    this.customizedProperties = customizedProperties
  }

  def getCustomized(): Boolean = {
    this.isCustomized
  }

  def getEngineType: String

}
