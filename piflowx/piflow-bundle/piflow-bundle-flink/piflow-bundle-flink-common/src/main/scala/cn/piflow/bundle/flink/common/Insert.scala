package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class Insert extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "从上游流或已注册的输入表向已注册的输出表插入数据。已注册表的schema必须与查询中的schema相匹配"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var inputTableName: String = _
  private var outputTableName: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    inputTableName = MapUtil.get(map, "inputTableName").asInstanceOf[String]
    outputTableName = MapUtil.get(map, "outputTableName").asInstanceOf[String]
  }

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    if (StringUtils.isNotEmpty(inputTableName)) {
      val inputTable = tableEnv.from(inputTableName)
      inputTable.insertInto(outputTableName).execute().print()
    } else {
      in.read().insertInto(outputTableName).execute().print()
    }
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val inputTableName = new PropertyDescriptor()
      .name("inputTableName")
      .displayName("输入表")
      .description("输入表。如果为空，从上游输入流读取，不为空，从已注册的输入表的读取")
      .defaultValue("")
      .required(false)
      .order(1)
      .example("inputTableName")
    descriptor = inputTableName :: descriptor

    val outputTableName = new PropertyDescriptor()
      .name("outputTableName")
      .displayName("输出表")
      .description("已注册的输出表")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("outputTableName")
    descriptor = outputTableName :: descriptor

    descriptor

  }

  override def getIcon(): Array[Byte] = {
    // todo 图片
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK
}
