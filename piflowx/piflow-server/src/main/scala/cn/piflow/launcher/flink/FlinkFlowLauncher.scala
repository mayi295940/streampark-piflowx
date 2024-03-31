package cn.piflow.launcher.flink

import cn.piflow.{Constants, Flow}
import cn.piflow.util._
import com.alibaba.fastjson2.{JSON, JSONObject}
import org.apache.flink.client.deployment.StandaloneClusterId
import org.apache.flink.client.program.{PackagedProgram, PackagedProgramUtils}
import org.apache.flink.client.program.rest.RestClusterClient
import org.apache.flink.configuration.{Configuration, JobManagerOptions, RestOptions}
import org.apache.flink.runtime.jobgraph.{JobGraph, SavepointRestoreSettings}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import java.io.File
import java.util.Date

/** Created by xjzhu@cnic.cn on 4/30/19 */
object FlinkFlowLauncher {

  def launch[Table](flow: Flow[Table]): String = {

    val flowJson = flow.getFlowJson
    println("FlowLauncher json:" + flowJson)

    val flowObject: JSONObject = JSON.parseObject(flowJson)

    val stopsJsonArray = flowObject.getJSONObject("flow").getJSONArray("stops")

    val dockerExecutor = new StringBuilder()
    for (i <- 0 until stopsJsonArray.size()) {
      if (stopsJsonArray.getJSONObject(i).getJSONObject("properties").containsKey("ymlPath")) {
        val ymlPath = stopsJsonArray
          .getJSONObject(i)
          .getJSONObject("properties")
          .getOrDefault("ymlPath", "")
          .toString

        val unzipDir = ymlPath
          .substring(ymlPath.lastIndexOf(Constants.SINGLE_SLASH) + 1)
          .replace(".zip", "")

        dockerExecutor.append(ymlPath + "#" + unzipDir)
        dockerExecutor.append(",")
      }

      if (
        stopsJsonArray
          .getJSONObject(i)
          .getJSONObject("properties")
          .containsKey("zipPath")
      ) {

        val zipPath = stopsJsonArray
          .getJSONObject(i)
          .getJSONObject("properties")
          .getOrDefault("zipPath", "")
          .toString

        val unzipDir = zipPath
          .substring(zipPath.lastIndexOf(Constants.SINGLE_SLASH) + 1)
          .replace(".zip", "")

        dockerExecutor.append(zipPath + "#app/" + unzipDir)
        dockerExecutor.append(",")
      }
    }

    println(dockerExecutor)

    var distArchives = ""
    if (dockerExecutor.length > 1) {
      distArchives = dockerExecutor.toString().stripPrefix(",")
    }

    val flowFileName = flow.getFlowName + new Date().getTime
    val flowFile = FlowFileUtil.getFlowFilePath(flowFileName)
    FileUtil.writeFile(flowJson, flowFile)

    // add plugin jars for application
    val pluginOnList = H2Util.getPluginOn()
    val classPath = PropertyUtil.getClassPath()
    val classPathFile = new File(classPath)
    if (classPathFile.exists()) {
      FileUtil
        .getJarFile(new File(classPath))
        .foreach(
          f => {
            pluginOnList.foreach(
              pluginName => {
                if (pluginName == f.getName) {
                  println(f.getPath)
                  // sparkLauncher.addJar(f.getPath)
                }
              })
          })
    }

    //    val scalaPath = PropertyUtil.getScalaPath()
    //    val scalaPathFile = new File(scalaPath)
    //    if (scalaPathFile.exists()) {
    //      FileUtil.getJarFile(new File(scalaPath)).foreach(f => {
    //        println("Load scala Jar: " + f.getPath)
    //        // sparkLauncher.addJar(f.getPath)
    //      })
    //    }

    // 集群信息
    val configuration = new Configuration()
    configuration.setString(
      JobManagerOptions.ADDRESS,
      PropertyUtil.getPropertyValue("flink.jobmanager.rpc.address"))
    configuration.setInteger(
      JobManagerOptions.PORT,
      PropertyUtil.getPropertyValue("flink.jobmanager.rpc.port").toInt)
    configuration.setInteger(
      RestOptions.PORT,
      PropertyUtil.getPropertyValue("flink.rest.port").toInt)

    val program = PackagedProgram
      .newBuilder()
      .setConfiguration(configuration)
      .setEntryPointClassName("cn.piflow.launcher.flink.StartFlinkFlowMain")
      .setArguments(flowFileName)
      .setJarFile(new File(ConfigureUtil.getPiFlowBundlePath()))
      .setSavepointRestoreSettings(SavepointRestoreSettings.none())
      .build()

    val parallelism = 1

    var jobGraph: JobGraph = null
    try jobGraph = PackagedProgramUtils.createJobGraph(program, configuration, parallelism, false)
    catch {
      case e: Throwable =>
        e.printStackTrace()
        throw new Exception("Flink jobGraph create failed")
    }

    val client =
      new RestClusterClient[StandaloneClusterId](configuration, StandaloneClusterId.getInstance())
    val result = client.submitJob(jobGraph)

    val jobId = result.get()
    println("提交完成")

    jobId.toString
  }

  def stop(appID: String): String = {

    println("Stop Flow !!!!!!!!!!!!!!!!!!!!!!!!!!")
    // yarn application kill appId
    val url = ConfigureUtil.getYarnResourceManagerWebAppAddress() + appID + "/state"
    val client = HttpClients.createDefault()
    val put: HttpPut = new HttpPut(url)
    val body = "{\"state\":\"KILLED\"}"
    put.addHeader("Content-Type", "application/json")
    put.setEntity(new StringEntity(body))
    val response: CloseableHttpResponse = client.execute(put)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")

    // update db
    println("Update flow state after Stop Flow !!!!!!!!!!!!!!!!!!!!!!!!!!")
    H2Util.updateFlowState(appID, FlowState.KILLED)
    H2Util.updateFlowFinishedTime(appID, new Date().toString)

    "ok"
  }

}
