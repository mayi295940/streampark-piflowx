package cn.piflow.util

import cn.piflow.Constants

import java.io.{File, InputStream}
import java.util.Properties

object FlowFileUtil {
  private val prop: Properties = new Properties()
  var fis: InputStream = null
  val userDir = System.getProperty("user.dir")
  var path: String = ""
  var schedulePath: String = ""
  var file: File = null

  try {

    path = userDir + "/flowFile"
    schedulePath = path + "/scheduleFile"
    file = new File(schedulePath)
    if (!file.exists()) {
      file.mkdirs()
    }

  } catch {
    case ex: Exception => ex.printStackTrace()
  }

  def getFlowFilePath(flowName: String): String = {
    path + Constants.SINGLE_SLASH + flowName + ".json"
  }

  def getFlowFileInUserDir(flowName: String): String = {
    userDir + Constants.SINGLE_SLASH + flowName + ".json"
  }

  def writeFlowFile(flowJson: String, flowFilePath: String) = {
    FileUtil.writeFile(flowJson, flowFilePath)
  }

  def readFlowFile(flowFilePath: String): String = {

    FileUtil.readFile(flowFilePath)
  }

  def getScheduleFilePath(fileName: String): String = {
    schedulePath + Constants.SINGLE_SLASH + fileName + ".json"
  }

  def main(args: Array[String]): Unit = {

    val json =
      """
        |{
        |  "flow": {
        |    "name": "MockData",
        |    "executorMemory": "1g",
        |    "executorNumber": "1",
        |    "uuid": "8a80d63f720cdd2301723b7461d92600",
        |    "paths": [
        |      {
        |        "inport": "",
        |        "from": "MockData",
        |        "to": "ShowData",
        |        "outport": ""
        |      }
        |    ],
        |    "executorCores": "1",
        |    "driverMemory": "1g",
        |    "stops": [
        |      {
        |        "name": "MockData",
        |        "bundle": "cn.piflow.bundle.common.MockData",
        |        "uuid": "8a80d63f720cdd2301723b7461d92604",
        |        "properties": {
        |          "schema": "title:String, author:String, age:Int",
        |          "count": "10"
        |        },
        |        "customizedProperties": {
        |
        |        }
        |      },
        |      {
        |        "name": "ShowData",
        |        "bundle": "cn.piflow.bundle.external.ShowData",
        |        "uuid": "8a80d63f720cdd2301723b7461d92602",
        |        "properties": {
        |          "showNumber": "5"
        |        },
        |        "customizedProperties": {
        |
        |        }
        |      }
        |    ]
        |  }
        |}
        |
      """.stripMargin
    val flowFile = FlowFileUtil.getFlowFilePath("test")
    FileUtil.writeFile(json, flowFile)

    val readFlowFile = FileUtil.readFile(flowFile)
    println(readFlowFile)
  }

}
