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
import cn.piflow.conf.util.{ImageUtil, MapUtil, PluginClassLoader}
import org.apache.spark.sql.DataFrame

import java.net.{MalformedURLException, URL}

import scala.language.experimental.macros
import scala.reflect.runtime.{universe => ru}

class ExecuteScala extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Execute scala script"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var packageName: String = "cn.piflow.bundle.script"
  var script: String = _
  var plugin: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    script = MapUtil.get(map, "script").asInstanceOf[String]
    plugin = MapUtil.get(map, "plugin").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val pluginName = new PropertyDescriptor()
      .name("plugin")
      .displayName("Plugin")
      .description("The class name of scala code.")
      .defaultValue("")
      .required(true)
    descriptor = pluginName :: descriptor

    val script = new PropertyDescriptor()
      .name("script")
      .displayName("script")
      .description(
        "The code of scala. \nUse in.read() to get dataframe from upstream component. \nUse out.write() to write datafram to downstream component.")
      .defaultValue("")
      .required(true)
      .example("val df = in.read() \nval df1 = df.select(\"author\").filter($\"author\".like(\"%xjzhu%\")) \ndf1.show() \ndf.createOrReplaceTempView(\"person\") \nval df2 = spark.sql(\"select * from person where author like '%xjzhu%'\") \ndf2.show() \nout.write(df2)")
      .language(Language.Scala)

    descriptor = script :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/script/scala.jpg")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.ScriptGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val execMethod = "perform"
    val loader = new PluginClassLoader

    // when local run, use these codes
    // val scalaDir = PropertyUtil.getScalaPath()
    // val pluginurl = s"jar:file:$scalaDir/$plugin.jar!/"

    val userDir = System.getProperty("user.dir")
    // FileUtil.getJarFile(new File(userDir)).foreach(println(_))

    val pluginurl = s"jar:file:$userDir/$plugin.jar!/"
    println(s"Scala Plugin url : $pluginurl")
    var url: URL = null
    try
      url = new URL(pluginurl)
    catch {
      case e: MalformedURLException =>
        e.printStackTrace()
    }
    loader.addURLFile(url)

    val className = plugin.split(Constants.SINGLE_SLASH).last.split(".jar")(0)
    val classMirror = ru.runtimeMirror(loader)
    println("staticModule: " + s"$packageName.$className")
    val classTest = classMirror.staticModule(s"$packageName.$className")
    val methods = classMirror.reflectModule(classTest)
    val objectMirror = classMirror.reflect(methods.instance)
    val method = methods.symbol.typeSignature.member(ru.TermName(s"$execMethod")).asMethod
    val result = objectMirror.reflectMethod(method)(in, out, pec)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
