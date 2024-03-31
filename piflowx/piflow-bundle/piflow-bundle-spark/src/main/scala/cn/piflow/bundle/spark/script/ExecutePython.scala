package cn.piflow.bundle.spark.script

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.FileUtil
import jep.Jep
import org.apache.spark.sql.DataFrame

import java.util.UUID

/** Created by xjzhu@cnic.cn on 2/24/20 */
class ExecutePython extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Execute python script"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var script: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    script = MapUtil.get(map, "script").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val script = new PropertyDescriptor()
      .name("script")
      .displayName("script")
      .description("The code of python")
      .defaultValue("")
      .required(true)
      .language(Language.Python)

    descriptor = script :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/script/python.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.ScriptGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val jep = new Jep()
    val scriptPath = "/tmp/pythonExcutor-" + UUID.randomUUID() + ".py"
    FileUtil.writeFile(script, scriptPath)
    jep.runScript(scriptPath)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK
}
