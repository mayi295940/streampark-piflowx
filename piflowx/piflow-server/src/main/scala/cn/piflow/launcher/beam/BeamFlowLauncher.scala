package cn.piflow.launcher.beam

import cn.piflow.Flow
import cn.piflow.util._
import org.apache.beam.sdk.values.Row
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import java.util.Date

object BeamFlowLauncher {

  def launch[PCollection[Row]](flow: Flow[PCollection[Row]]): String = {

    val flowJson = flow.getFlowJson
    println("FlowLauncher json:" + flowJson)

    val flowFileName = flow.getFlowName + new Date().getTime
    val flowFile = FlowFileUtil.getFlowFilePath(flowFileName)
    FileUtil.writeFile(flowJson, flowFile)

    StartBeamFlowMain.main(Array(flowFileName))
    println("任务执行完成")

    ""
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
