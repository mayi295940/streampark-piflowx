package cn.piflow.conf.bean

import cn.piflow.{Constants, FlowImpl, Path}
import cn.piflow.conf.util.{MapUtil, ScalaExecutorUtil}
import cn.piflow.util.JsonUtil
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import scala.collection.mutable.{Map => MMap}
import scala.util.matching.Regex

class FlowBean[DataType] extends GroupEntryBean {

  /*@BeanProperty*/
  var uuid: String = _
  var name: String = _
  var checkpoint: String = _
  var checkpointParentProcessId: String = _
  private var runMode: String = _
  private var showData: String = _

  var stops: List[StopBean[DataType]] = List()
  var paths: List[PathBean] = List()

  private var environment: Map[String, Any] = _

  // flow environment variable
  var environmentVariable: Map[String, Any] = _

  // flow json string
  var flowJson: String = _

  def init(map: Map[String, Any]): Unit = {

    val flowJsonOjb = JsonUtil.toJson(map)
    this.flowJson = JsonUtil.format(flowJsonOjb)

    val flowMap = MapUtil.get(map, "flow").asInstanceOf[Map[String, Any]]

    this.uuid = MapUtil.get(flowMap, "uuid").asInstanceOf[String]
    this.name = MapUtil.get(flowMap, "name").asInstanceOf[String]
    this.checkpoint = flowMap.getOrElse("checkpoint", "").asInstanceOf[String]
    this.checkpointParentProcessId =
      flowMap.getOrElse("checkpointParentProcessId", "").asInstanceOf[String]
    this.runMode = flowMap.getOrElse("runMode", "RUN").asInstanceOf[String]
    this.showData = flowMap.getOrElse("showData", "0").asInstanceOf[String]

    this.environmentVariable =
      flowMap.getOrElse("environmentVariable", Map()).asInstanceOf[Map[String, Any]]
    this.environment = flowMap.getOrElse("environment", Map()).asInstanceOf[Map[String, Any]]

    // construct StopBean List
    val stopsList = MapUtil.get(flowMap, "stops").asInstanceOf[List[Map[String, Any]]]

    // replace environment variable
    if (this.environmentVariable.keySet.nonEmpty) {
      val pattern = new Regex("\\$\\{+[^\\}]*\\}")
      stopsList.foreach(
        stopMap => {
          val stopMutableMap = MMap(stopMap.toSeq: _*)
          val stopPropertiesMap =
            MapUtil.get(stopMutableMap, "properties").asInstanceOf[MMap[String, Any]]
          stopPropertiesMap.keySet.foreach {
            key =>
              {

                val value = MapUtil.get(stopPropertiesMap, key).asInstanceOf[String]

                val it = (pattern.findAllIn(value))
                while (it.hasNext) {
                  val item = it.next()
                  val newValue =
                    value.replace(item, MapUtil.get(environmentVariable, item).asInstanceOf[String])
                  stopPropertiesMap(key) = newValue
                  println(key + " -> " + newValue)
                }
              }
          }
          stopMutableMap("properties") = stopPropertiesMap.toMap
          val stop = StopBean[DataType](this.name, stopMutableMap.toMap)
          this.stops = stop +: this.stops
        })
    } else { // no environment variables
      stopsList.foreach(
        stopMap => {
          val stop = StopBean[DataType](this.name, stopMap)
          this.stops = stop +: this.stops
        })
    }

    // construct PathBean List
    val pathsList = MapUtil.get(flowMap, "paths").asInstanceOf[List[Map[String, Any]]]
    pathsList.foreach(
      pathMap => {
        val path = PathBean(pathMap)
        this.paths = path +: this.paths
      })

  }

  // create Flow by FlowBean
  def constructFlow(buildScalaJar: Boolean = true): FlowImpl[DataType] = {

    if (buildScalaJar) {
      ScalaExecutorUtil.buildScalaExcutorJar(this)
    }

    val flow = new FlowImpl[DataType]()

    flow.setFlowJson(this.flowJson)
    flow.setFlowName(this.name)
    flow.setUUID(uuid)
    flow.setCheckpointParentProcessId(this.checkpointParentProcessId)
    flow.setRunMode(this.runMode)
    flow.setEnvironment(this.environment)

    this.stops.foreach(
      stopBean => {
        flow.addStop(stopBean.name, stopBean.constructStop())
      })

    this.paths.foreach(
      pathBean => {
        flow.addPath(
          Path.from(pathBean.from).via(pathBean.outport, pathBean.inport).to(pathBean.to))
      })

    if (!this.checkpoint.equals("")) {
      val checkpointList = this.checkpoint.split(Constants.COMMA)
      checkpointList.foreach(checkpoint => flow.addCheckPoint(checkpoint))
    }

    flow
  }

  def toJson: String = {
    val json =
      "flow" ->
        ("uuid" -> this.uuid) ~
        ("name" -> this.name) ~
        ("stops" ->
          stops.map {
            stop =>
              ("uuid" -> stop.uuid) ~
                ("name" -> stop.name) ~
                ("bundle" -> stop.bundle)
          }) ~
        ("paths" ->
          paths.map {
            path =>
              ("from" -> path.from) ~
                ("outport" -> path.outport) ~
                ("inport" -> path.inport) ~
                ("to" -> path.to)
          })

    val jsonString = compactRender(json)
    jsonString
  }

}

object FlowBean {
  def apply[DataType](map: Map[String, Any]): FlowBean[DataType] = {
    val flowBean = new FlowBean[DataType]()
    flowBean.init(map)
    flowBean
  }
}
