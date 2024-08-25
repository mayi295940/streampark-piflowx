/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.piflow.api

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{scaladsl, ActorMaterializer, IOResult}
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import cn.piflow.{Constants, GroupExecution}
import cn.piflow.api.HTTPService.pluginManager
import cn.piflow.conf.util.{MapUtil, PluginManager}
import cn.piflow.util._
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.typesafe.config.{Config, ConfigFactory}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.h2.tools.Server
import spray.json.DefaultJsonProtocol

import java.io.File
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date

import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object HTTPService extends DefaultJsonProtocol with Directives with SprayJsonSupport {

  implicit val config: Config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("PiFlowHTTPService", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val scheduler = QuartzSchedulerExtension(system)
  private var actorMap: Map[String, ActorRef] = Map[String, ActorRef]()

  // private var processMap: Map[String, SparkAppHandle] = Map[String, SparkAppHandle]()
  private var processMap: Map[String, Any] = Map[String, Any]()

  private var flowGroupMap: Map[String, GroupExecution] = Map[String, GroupExecution]()
  // var projectMap = Map[String, GroupExecution]()

  val pluginManager: PluginManager = PluginManager.getInstance

  private val SUCCESS_CODE = 200
  private val FAIL_CODE = 500
  private val UNKNOWN_CODE = 404

  def toJson(entity: RequestEntity): Map[String, Any] = {
    entity match {
      case HttpEntity.Strict(_, data) =>
        if (data.isEmpty) {
          Map()
        } else {
          val temp = JsonUtil.jsonToMap(data.utf8String)
          temp
        }
      case _ => Map()
    }
  }

  private def route(req: HttpRequest): Future[HttpResponse] = req match {

    case HttpRequest(GET, Uri.Path("/"), headers, entity, protocol) =>
      Future.successful(HttpResponse(SUCCESS_CODE, entity = "Get OK!"))

    case HttpRequest(GET, Uri.Path("/flow/info"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      if (!appID.equals("")) {
        // server state in h2db
        var result = API.getFlowInfo(appID)
        println("getFlowInfo result: " + result)
        val resultMap = JsonUtil.jsonToMap(result)
        val flowInfoMap = MapUtil.get(resultMap, "flow").asInstanceOf[Map[String, Any]]
        val flowState = MapUtil.get(flowInfoMap, "state").asInstanceOf[String]

        // yarn flow state
        val flowYarnInfoJson = API.getFlowYarnInfo(appID)
        val map = JsonUtil.jsonToMap(flowYarnInfoJson)
        val yarnFlowInfoMap = MapUtil.get(map, "app").asInstanceOf[Map[String, Any]]
        val name = MapUtil.get(yarnFlowInfoMap, "name").asInstanceOf[String]
        val flowYarnState = MapUtil.get(yarnFlowInfoMap, "state").asInstanceOf[String]

        if (flowInfoMap.contains("state")) {
          val checkState = StateUtil.FlowStateCheck(flowState, flowYarnState)
          if (checkState) {
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          } else {
            val newFlowState = StateUtil.getNewFlowState(flowState, flowYarnState)
            if (newFlowState != flowState) {
              H2Util.updateFlowState(appID, newFlowState)
            }
            result = API.getFlowInfo(appID)
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          }
        } else if (yarnFlowInfoMap.contains("state")) {
          var flowInfoMap = Map[String, Any]()
          flowInfoMap += ("id" -> appID)
          flowInfoMap += ("name" -> name)
          flowInfoMap += ("state" -> flowYarnState)
          flowInfoMap += ("startTime" -> "")
          flowInfoMap += ("endTime" -> "")
          flowInfoMap += ("stops" -> List())
          result = JsonUtil.format(JsonUtil.toJson(Map("flow" -> flowInfoMap)))
          println("getFlowInfo on Yarn: " + result)
          Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
        } else { // Both h2db and yarn do not exist appId
          var flowInfoMap = Map[String, Any]()
          flowInfoMap += ("id" -> appID)
          flowInfoMap += ("name" -> "")
          flowInfoMap += ("state" -> FlowState.FAILED)
          flowInfoMap += ("startTime" -> "")
          flowInfoMap += ("endTime" -> "")
          flowInfoMap += ("stops" -> List())
          result = JsonUtil.format(JsonUtil.toJson(Map("flow" -> flowInfoMap)))
          Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
        }

      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "appID is null or flow run failed!"))
      }

    }

    case HttpRequest(GET, Uri.Path("/flow/progress"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      if (!appID.equals("")) {
        var result = API.getFlowProgress(appID)
        println("getFlowProgress result: " + result)
        if (result.equals("NaN")) {
          result = "0"
        }
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "appID is null or flow run failed!"))
      }

    }

    case HttpRequest(GET, Uri.Path("/flow/log"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      if (!appID.equals("")) {
        val result = API.getFlowYarnInfo(appID)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "appID is null or flow does not exist!"))
      }
    }

    case HttpRequest(GET, Uri.Path("/flow/checkpoints"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      if (!appID.equals("")) {
        val result = API.getFlowCheckpoint(appID)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "appID is null or flow does not exist!"))
      }
    }

    case HttpRequest(GET, Uri.Path("/flow/debugData"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      val stopName = req.getUri().query().getOrElse("stopName", "")
      val port = req.getUri().query().getOrElse("port", "default")
      if (!appID.equals("") && !stopName.equals()) {
        val result = API.getFlowDebugData(appID, stopName, port)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(
          HttpResponse(FAIL_CODE, entity = "appID is null or stop does not have debug data!"))
      }

    }
    case HttpRequest(GET, Uri.Path("/flow/visualizationData"), headers, entity, protocol) => {

      val appID = req.getUri().query().getOrElse("appID", "")
      val stopName = req.getUri().query().getOrElse("stopName", "")
      val visualizationType = req.getUri().query().getOrElse("visualizationType", "")
      // val port = req.getUri().query().getOrElse("port","default")
      if (!appID.equals("") && !stopName.equals()) {
        val result = API.getFlowVisualizationData(appID, stopName, visualizationType)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(
          HttpResponse(
            FAIL_CODE,
            entity = "appID is null or stop does not have visualization data!"))
      }

    }

    case HttpRequest(GET, Uri.Path("/flow/testDataPath"), headers, entity, protocol) => {

      val testDataPath = ConfigureUtil.getTestDataPath()
      if (!testDataPath.equals("")) {
        Future.successful(HttpResponse(SUCCESS_CODE, entity = testDataPath))
      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "test data path is null!"))
      }

    }

    case HttpRequest(POST, Uri.Path("/flow/start"), headers, entity, protocol) => {

      try {

        val bodyFeature = Unmarshal(entity).to[String]
        val flowJson = Await.result(bodyFeature, scala.concurrent.duration.Duration(1, "second"))
        val (appId, process) = API.startFlow(flowJson)
        processMap += (appId -> process)
        val result = "{\"flow\":{\"id\":\"" + appId + "\"}}"
        println("Start Flow Succeed : " + result + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } catch {
        case ex: Exception => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not start flow!"))
        }
      }
    }

    case HttpRequest(POST, Uri.Path("/flow/stop"), headers, entity, protocol) => {

      val data = toJson(entity)
      val appId = data.getOrElse("appID", "").asInstanceOf[String]

      if (appId.equals("")) {
        Future.failed(new Exception("Can not found application Error!"))
      } else {
        if (processMap.contains(appId)) {
          processMap.get(appId) match {
            case Some(process) =>
              val result = API.stopFlow(appId, process)
              processMap.-(appId)
              Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
            case ex => {
              println(ex)
              Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found process Error!"))
            }
          }
        } else {
          val result = API.stopFlowOnYarn(appId)
          Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
        }
      }
    }

    case HttpRequest(GET, Uri.Path("/stop/info"), headers, entity, protocol) => {
      val bundle = req.getUri().query().getOrElse("bundle", "")
      if (bundle.equals("")) {
        Future.failed(new Exception("Can not found bundle Error!"))
      } else {
        try {
          val stopInfo = API.getStopInfo(bundle)
          Future.successful(HttpResponse(SUCCESS_CODE, entity = stopInfo))
        } catch {
          case ex: Throwable => {
            println(ex)
            Future.successful(
              HttpResponse(
                FAIL_CODE,
                entity = "getPropertyDescriptor or getIcon Method Not Implemented Error!"))
          }
        }
      }
    }
    case HttpRequest(GET, Uri.Path("/stop/groups"), headers, entity, protocol) => {

      try {
        val stopGroups = API.getAllGroups()
        Future.successful(HttpResponse(SUCCESS_CODE, entity = stopGroups))
      } catch {
        case ex: Throwable => {
          println(ex)
          Future.successful(
            HttpResponse(FAIL_CODE, entity = "getGroup Method Not Implemented Error!"))
        }
      }
    }
    case HttpRequest(GET, Uri.Path("/stop/list"), headers, entity, protocol) => {

      try {
        val stops = API.getAllStops()
        Future.successful(HttpResponse(SUCCESS_CODE, entity = stops))
      } catch {
        case ex: Throwable => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found stop !"))
        }
      }

    }
    case HttpRequest(GET, Uri.Path("/stop/listWithGroup"), headers, entity, protocol) => {

      try {
        val engineType = req.getUri().query().getOrElse("engineType", "")
        val stops = API.getAllStopsWithGroup(engineType)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = stops))
      } catch {
        case ex: Throwable => {
          ex.printStackTrace()
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found stop !"))
        }
      }

    }

    case HttpRequest(POST, Uri.Path("/group/start"), headers, entity, protocol) => {

      Thread.sleep(1000)
      val entityStream = entity.dataBytes
      // Use fold to concatenate chunks into a single string
      val concatenateChunks: Future[String] =
        entityStream.runFold("")((acc, chunk) => acc + chunk.utf8String)
      // Construct a successful Future[HttpResponse] in onComplete
      val responseFuture: Future[HttpResponse] = concatenateChunks
        .flatMap {
          concatenatedString =>
            val flowGroupJson = concatenatedString
            val flowGroupExecution = API.startGroup(flowGroupJson)
            flowGroupMap += (flowGroupExecution.getGroupId -> flowGroupExecution)
            val result = "{\"group\":{\"id\":\"" + flowGroupExecution.getGroupId + "\"}}"
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
        }
        .recover {
          case ex =>
            println(s"An error occurred: ${ex.getMessage}")
            HttpResponse(FAIL_CODE, entity = "Can not start flow!!!")
        }
      responseFuture

    }

    case HttpRequest(POST, Uri.Path("/group/stop"), headers, entity, protocol) => {
      val data = toJson(entity)
      val groupId = data.getOrElse("groupId", "").asInstanceOf[String]
      if (groupId.equals("") || !flowGroupMap.contains(groupId)) {
        Future.failed(new Exception("Can not found flowGroup Error!"))
      } else {

        flowGroupMap.get(groupId) match {
          case Some(flowGroupExecution) =>
            val result = API.stopGroup(flowGroupExecution)
            flowGroupMap.-(groupId)
            Future.successful(HttpResponse(SUCCESS_CODE, entity = "Stop FlowGroup Ok!!!"))
          case ex => {
            println(ex)
            Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found FlowGroup Error!"))
          }

        }

      }
    }

    case HttpRequest(GET, Uri.Path("/group/info"), headers, entity, protocol) => {

      val groupId = req.getUri().query().getOrElse("groupId", "")
      if (!groupId.equals("")) {
        var result = API.getFlowGroupInfo(groupId)
        println("getFlowGroupInfo result: " + result)

        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(
          HttpResponse(FAIL_CODE, entity = "groupId is null or flowGroup run failed!"))
      }
    }

    case HttpRequest(GET, Uri.Path("/group/progress"), headers, entity, protocol) => {

      val groupId = req.getUri().query().getOrElse("groupId", "")
      if (!groupId.equals("")) {
        var result = API.getFlowGroupProgress(groupId)
        println("getFlowGroupProgress result: " + result)

        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(
          HttpResponse(FAIL_CODE, entity = "groupId is null or flowGroup progress exception!"))
      }
    }

    // schedule related API
    case HttpRequest(POST, Uri.Path("/schedule/start"), headers, entity, protocol) => {

      Thread.sleep(1000)
      val entityStream = entity.dataBytes
      // Use fold to concatenate chunks into a single string
      val concatenateChunks: Future[String] =
        entityStream.runFold("")((acc, chunk) => acc + chunk.utf8String)
      // Construct a successful Future[HttpResponse] in onComplete
      val responseFuture: Future[HttpResponse] = concatenateChunks
        .flatMap {
          concatenatedString =>
            val data = concatenatedString
            val dataMap = toJson(data)
            val expression = dataMap.getOrElse("expression", "").asInstanceOf[String]
            val startDateStr = dataMap.getOrElse("startDate", "").asInstanceOf[String]
            val endDateStr = dataMap.getOrElse("endDate", "").asInstanceOf[String]
            val scheduleInstance =
              dataMap.getOrElse("schedule", Map[String, Any]()).asInstanceOf[Map[String, Any]]
            println("scheduleInstance:" + scheduleInstance)

            val id: String = "schedule_" + IdGenerator.uuid

            var scheduleType = ""
            if (!scheduleInstance.getOrElse("flow", "").equals("")) {
              println("====ScheduleType:Flow")
              scheduleType = ScheduleType.FLOW
            } else if (!scheduleInstance.getOrElse("group", "").equals("")) {
              println("=====ScheduleType:Group")
              scheduleType = ScheduleType.GROUP
            } else {
              Future.successful(
                HttpResponse(FAIL_CODE, entity = "Can not schedule, please check the json format!"))
            }
            val flowActor = system.actorOf(Props(new ExecutionActor(id, scheduleType)))
            scheduler.createSchedule(id, cronExpression = expression)
            if (startDateStr.equals("")) {
              scheduler.schedule(id, flowActor, JsonUtil.format(JsonUtil.toJson(scheduleInstance)))
            } else {
              val startDate: Option[Date] =
                Some(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateStr))
              scheduler.schedule(
                id,
                flowActor,
                JsonUtil.format(JsonUtil.toJson(scheduleInstance)),
                startDate)
            }
            actorMap += (id -> flowActor)

            H2Util.addScheduleInstance(
              id,
              expression,
              startDateStr,
              endDateStr,
              ScheduleState.STARTED)

            // save schedule json file
            val flowFile = FlowFileUtil.getScheduleFilePath(id)
            FileUtil.writeFile(data, flowFile)
            Future.successful(HttpResponse(SUCCESS_CODE, entity = id))
        }
        .recover {
          case ex =>
            println(s"An error occurred: ${ex.getMessage}")
            HttpResponse(FAIL_CODE, entity = "Can not start flow!!!")
        }
      responseFuture
    }

    case HttpRequest(POST, Uri.Path("/schedule/stop"), headers, entity, protocol) => {

      val data = toJson(entity)
      val scheduleId = data.getOrElse("scheduleId", "").asInstanceOf[String]
      if (scheduleId.equals("") || !actorMap.contains(scheduleId)) {
        Future.failed(new Exception("Can not found scheduleId Error!"))
      } else {

        actorMap.get(scheduleId) match {
          case Some(actorRef) =>
            system.stop(actorRef)
            processMap.-(scheduleId)
            H2Util.updateScheduleInstanceStatus(scheduleId, ScheduleState.STOPED)
            Future.successful(HttpResponse(SUCCESS_CODE, entity = "Stop schedule ok!"))
          case ex => {
            println(ex)
            Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found schedule Error!"))
          }

        }

      }

    }

    case HttpRequest(GET, Uri.Path("/schedule/info"), headers, entity, protocol) => {

      val scheduleId = req.getUri().query().getOrElse("scheduleId", "")
      if (!scheduleId.equals("")) {
        var result = API.getScheduleInfo(scheduleId)
        println("getScheduleInfo result: " + result)

        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } else {
        Future.successful(
          HttpResponse(FAIL_CODE, entity = "scheduleId is null or schedule info is error!"))
      }
    }

    case HttpRequest(GET, Uri.Path("/resource/info"), headers, entity, protocol) => {

      val resourceInfo = API.getResourceInfo
      if (resourceInfo != "") {
        Future.successful(HttpResponse(SUCCESS_CODE, entity = resourceInfo))
      } else {
        Future.successful(HttpResponse(FAIL_CODE, entity = "get resource info error!"))
      }

    }

    case HttpRequest(POST, Uri.Path("/plugin/add"), headers, entity, protocol) => {

      entity match {
        case HttpEntity.Strict(_, data) => {
          val data = toJson(entity)
          val pluginName = data.getOrElse("plugin", "").asInstanceOf[String]
          val pluginID = API.addPlugin(pluginManager, pluginName)
          if (pluginID != "") {
            val stopsInfo = API.getConfigurableStopInfoInPlugin(pluginManager, pluginName)
            val result =
              "{\"plugin\":{\"id\":\"" + pluginID + "\"},\"stopsInfo\":" + stopsInfo + "}"
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          } else {
            Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
          }
        }
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
        }
      }

    }

    case HttpRequest(POST, Uri.Path("/plugin/remove"), headers, entity, protocol) => {

      entity match {
        case HttpEntity.Strict(_, data) => {
          val data = toJson(entity)
          val pluginId = data.getOrElse("pluginId", "").asInstanceOf[String]
          val pluginName = H2Util.getPluginInfoMap(pluginId).getOrElse("name", "")
          val stopsInfo = API.getConfigurableStopInfoInPlugin(pluginManager, pluginName)

          val isOk = API.removePlugin(pluginManager, pluginId)
          if (isOk) {

            val result =
              "{\"plugin\":{\"id\":\"" + pluginId + "\"},\"stopsInfo\":" + stopsInfo + "}"
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          } else {
            Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
          }
        }

        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
        }
      }

    }

    case HttpRequest(GET, Uri.Path("/plugin/info"), headers, entity, protocol) => {

      val pluginId = req.getUri().query().getOrElse("pluginId", "")
      try {
        val pluginInfo = API.getPluginInfo(pluginId)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = pluginInfo))
      } catch {
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found plugins !"))
        }
      }

    }

    case HttpRequest(GET, Uri.Path("/plugin/path"), headers, entity, protocol) => {

      try {
        val pluginPath = PropertyUtil.getClassPath()
        val result = "{\"pluginPath\":\"" + pluginPath + "\"}"
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } catch {
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found plugin path !"))
        }
      }
    }

    case HttpRequest(GET, Uri.Path("/sparkJar/path"), headers, entity, protocol) => {

      try {
        val sparkJarPath = PropertyUtil.getSpartJarPath()
        val result = "{\"sparkJarPath\":\"" + sparkJarPath + "\"}"
        Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
      } catch {
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found spark jar path !"))
        }
      }
    }

    case HttpRequest(POST, Uri.Path("/sparkJar/add"), headers, entity, protocol) => {

      entity match {
        case HttpEntity.Strict(_, data) => {
          val data = toJson(entity)
          val sparkJarName = data.getOrElse("sparkJar", "").asInstanceOf[String]
          val sparkJarID = API.addSparkJar(sparkJarName)
          if (sparkJarID != "") {

            val result = "{\"sparkJar\":{\"id\":\"" + sparkJarID + "\"}}"
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          } else {
            Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
          }
        }
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
        }
      }

    }

    case HttpRequest(POST, Uri.Path("/sparkJar/remove"), headers, entity, protocol) => {

      entity match {
        case HttpEntity.Strict(_, data) => {
          val data = toJson(entity)
          val sparkJarId = data.get("sparkJarId").getOrElse("").asInstanceOf[String]
          val isOK = API.removeSparkJar(sparkJarId)

          if (isOK == true) {

            val result = "{\"sparkJar\":{\"id\":\"" + sparkJarId + "\"}}"
            Future.successful(HttpResponse(SUCCESS_CODE, entity = result))
          } else {
            Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
          }
        }

        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Fail"))
        }
      }

    }

    /*
    case HttpRequest(GET, Uri.Path("/monitor/throughout"), headers, entity, protocol) => {

      val appId = req.getUri().query().getOrElse("appId", "")
      val stopName = req.getUri().query().getOrElse("stopName", "")
      val portName = req.getUri().query().getOrElse("portName", "")
      try {
        val pluginInfo = API.getThroughput(appId,stopName,portName)
        Future.successful(HttpResponse(SUCCESS_CODE, entity = pluginInfo))
      } catch {
        case ex => {
          println(ex)
          Future.successful(HttpResponse(FAIL_CODE, entity = "Can not found plugins !"))
        }
      }
    }
     */

    case HttpRequest(GET, Uri.Path("/visualDataDirectory/data"), headers, entity, protocol) => {
      try {
        val dataCenter = req.getUri().query().getOrElse("dataCenter", "")
        val appID = req.getUri().query().getOrElse("appID", "")
        val stopName = req.getUri().query().getOrElse("stopName", "")
        val visualDataDirectoryPath =
          PropertyUtil.getVisualDataDirectoryPath() + appID + "/" + stopName
        val visualDataDirectoryData = API.getHdfsDataByPath(visualDataDirectoryPath)
        val returnValue: scaladsl.Source[ByteString, Future[IOResult]] =
          StreamConverters.fromInputStream(() => visualDataDirectoryData)
        Future.successful(
          HttpResponse(
            SUCCESS_CODE,
            entity = HttpEntity(ContentTypes.`application/octet-stream`, returnValue)))
      } catch {
        case ex => {
          println(ex)
          Future.successful(
            HttpResponse(FAIL_CODE, entity = "Can not found visualDataDirectory path !"))
        }
      }
    }

    case _: HttpRequest =>
      Future.successful(HttpResponse(UNKNOWN_CODE, entity = "Unknown resource!"))
  }

  def run(): Unit = {

    val ip = InetAddress.getLocalHost.getHostAddress

    val port = PropertyUtil.getIntPropertyValue("server.port")
    Http().bindAndHandleAsync(route, ip, port)
    println("Server:" + ip + ":" + port + " Started!!!")

    // initSchedule()
    new MonitorScheduler().start()

  }

  private class MonitorScheduler extends Thread {

    override def run(): Unit = {
      while (true) {
        val needStopSchedule = H2Util.getNeedStopSchedule()
        needStopSchedule.foreach {
          scheduleId =>
            {
              actorMap.get(scheduleId) match {
                case Some(actorRef) =>
                  system.stop(actorRef)
                  processMap.-(scheduleId)
                case ex => {
                  println(ex)
                }
              }
              H2Util.updateScheduleInstanceStatus(scheduleId, ScheduleState.STOPED)
            }
        }
        Thread.sleep(10000)
      }
    }
  }

  private def initSchedule(): Unit = {

    val scheduleList = H2Util.getStartedSchedule()
    scheduleList.foreach(id => {
      val scheduleContent = FlowFileUtil.readFlowFile(FlowFileUtil.getScheduleFilePath(id))
      val dataMap = JsonUtil.jsonToMap(scheduleContent)

      val expression = dataMap.getOrElse("expression", "").asInstanceOf[String]
      val startDateStr = dataMap.getOrElse("startDate", "").asInstanceOf[String]
      val endDateStr = dataMap.getOrElse("endDate", "").asInstanceOf[String]
      val scheduleInstance =
        dataMap.getOrElse("schedule", Map[String, Any]()).asInstanceOf[Map[String, Any]]

      var scheduleType = ""
      if (!scheduleInstance.getOrElse("flow", "").equals("")) {
        scheduleType = ScheduleType.FLOW
      } else if (!scheduleInstance.getOrElse("group", "").equals("")) {
        scheduleType = ScheduleType.GROUP
      }
      val flowActor = system.actorOf(Props(new ExecutionActor(id, scheduleType)))
      scheduler.createSchedule(id, cronExpression = expression)

      if (startDateStr.equals("")) {
        scheduler.schedule(id, flowActor, JsonUtil.format(JsonUtil.toJson(scheduleInstance)))
      } else {
        val startDate: Option[Date] =
          Some(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateStr))
        scheduler.schedule(
          id,
          flowActor,
          JsonUtil.format(JsonUtil.toJson(scheduleInstance)),
          startDate)
      }
      actorMap += (id -> flowActor)
    })

  }
}

object Main {

  private def flywayInit() = {

    val ip = InetAddress.getLocalHost.getHostAddress
    // Create the Flyway instance
    val flyway: Flyway = new Flyway()
    val url = "jdbc:h2:tcp://" + ip + Constants.COLON + PropertyUtil.getPropertyValue(
      "h2.port") + "/~/piflow"
    // Point it to the database
    flyway.setDataSource(url, null, null)
    flyway.setLocations("db/migrations")
    flyway.setEncoding("UTF-8")
    flyway.setTable("FLYWAY_SCHEMA_HISTORY")
    flyway.setBaselineOnMigrate(true)
    try {
      // Start the migration
      flyway.migrate()
    } catch {
      case e: FlywayException =>
        flyway.repair()
        print(e)
    }
  }

  private def initPlugin(): Unit = {

    val pluginOnList = H2Util.getPluginOn()
    val classpathFile = new File(pluginManager.getPluginPath)
    val jarFile = FileUtil.getJarFile(classpathFile)

    pluginOnList.foreach(pluginName => {
      jarFile.foreach(pluginJar => {
        if (pluginName == pluginJar.getName) {
          println(pluginJar.getAbsolutePath)
          pluginManager.loadPlugin(pluginJar.getAbsolutePath)
        }
      })
    })
  }

  def main(argv: Array[String]): Unit = {

    val h2Server = Server
      .createTcpServer(
        "-tcp",
        "-tcpAllowOthers",
        "-ifNotExists",
        "-tcpPort",
        PropertyUtil.getPropertyValue("h2.port"))
      .start()

    flywayInit()

    HTTPService.run()

    initPlugin()
  }
}
