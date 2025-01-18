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
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.PropertyUtil
import org.apache.spark.deploy.PythonRunner
import org.apache.spark.sql.{DataFrame, SparkSession}

class PythonExecutor extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Run python script by PythonRunner"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var arg1: String = _
  var arg2: String = _
  var arg3: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    arg1 = MapUtil.get(map, "arg1").asInstanceOf[String]
    arg2 = MapUtil.get(map, "arg2").asInstanceOf[String]
    arg3 = MapUtil.get(map, "arg3").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val arg1 = new PropertyDescriptor()
      .name("arg1")
      .displayName("arg1")
      .description("The arg1 of python")
      .defaultValue("")
      .required(true)
      .language(Language.Python)

    val arg2 = new PropertyDescriptor()
      .name("arg2")
      .displayName("arg1")
      .description("The arg1 of python")
      .defaultValue("")
      .required(true)
      .language(Language.Python)

    val arg3 = new PropertyDescriptor()
      .name("arg3")
      .displayName("arg1")
      .description("The arg1 of python")
      .defaultValue("")
      .required(true)
      .language(Language.Python)

    descriptor = arg1 :: descriptor
    descriptor = arg3 :: descriptor
    descriptor = arg1 :: descriptor
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
    val appID = spark.sparkContext.applicationId
    val pyFilePath = "pythonExecutor/PythonExecutor.py"
    val pyFiles = "DataInputStream.py,DataOutputStream.py,ConfigurableStop.py"
    val hdfs = PropertyUtil.getPropertyValue("fs.defaultFS")

    //    val inputPath = hdfs + "/piflow/python/" + appID + "/inport/default"
    //    var outputPath = hdfs + "/piflow/python/" + appID + "/outport/default"
    val inputPath = "/piflow/python/" + appID + "/inport/default/"
    var outputPath = "/piflow/python/" + appID + "/outport/default/"

    val df = in.read()
    df.write.format("csv").mode("overwrite").option("set", "\t").save(inputPath)

    PythonRunner.main(Array(pyFilePath, pyFiles, "-i " + inputPath, "-o " + outputPath))

    println("spark stop ,no restart -----a--------------")
    val outDF = spark.read
      .format("csv")
      .option("header", true)
      .option("mode", "FAILFAST")
      .load(outputPath)
    outDF.show()
    out.write(outDF)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}