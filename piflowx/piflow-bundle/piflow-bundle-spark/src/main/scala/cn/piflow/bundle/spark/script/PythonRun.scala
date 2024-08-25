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

package cn.piflow.bundle.spark.script

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.deploy.PythonRunner
import org.apache.spark.sql.{DataFrame, SparkSession}

class PythonRun extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = ""
  override val description: String = ""
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var arg_1: String = _
  var arg_2: String = _
  var arg_3: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    arg_1 = MapUtil.get(map, "arg_1").asInstanceOf[String]
    arg_2 = MapUtil.get(map, "arg_2").asInstanceOf[String]
    arg_3 = MapUtil.get(map, "arg_3").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val arg_1 = new PropertyDescriptor()

    val arg_2 = new PropertyDescriptor()

    val arg_3 = new PropertyDescriptor()

    descriptor = arg_1 :: descriptor
    descriptor = arg_2 :: descriptor
    descriptor = arg_3 :: descriptor
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

    val spark = pec.get[SparkSession]()
    val ID = spark.sparkContext.applicationId
    val pyPath = "pythonExecutor/PythonRun.py"
    val pyFileshelp = "DataInputStream.py,DataOutputStream.py,ConfigurableStop.py"

    val inputPath = "/piflow/python/" + ID + "/inport/default/"
    var outputPath = "/piflow/python/" + ID + "/outport/default/"

    val dataFrame = in.read()
    dataFrame.write
      .format("csv")
      .mode("overwrite")
      .option("set", "\t")
      .save(inputPath)

    PythonRunner.main(Array(pyPath, pyFileshelp, "-i " + inputPath, "-o " + outputPath))

    val outDataFrame = spark.read
      .format("csv")
      .option("mode", "FAILFAST")
      .option("header", true)
      .load(outputPath)

    outDataFrame.show()
    out.write(outDataFrame)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
