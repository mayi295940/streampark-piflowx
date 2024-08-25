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

package cn.piflow.conf.util

import cn.piflow.Constants
import cn.piflow.conf.ConfigurableStop
import cn.piflow.conf.bean.PropertyDescriptor
import javassist.Modifier
import net.liftweb.json.{compactRender, JValue}
import net.liftweb.json.JsonDSL._
import org.reflections.Reflections
import sun.misc.BASE64Encoder

import scala.util.control.Breaks._

object ClassUtil {

  val configurableStopClass: String = "cn.piflow.conf.ConfigurableStop"
  val configurableIncrementalStop: String = "cn.piflow.conf.ConfigurableIncrementalStop"

  def findAllConfigurableStop[DataType](
      packagePrefix: String = ""): List[ConfigurableStop[DataType]] = {

    var stopList: List[ConfigurableStop[DataType]] = List()

    // find internal stop
    val reflections = new Reflections(packagePrefix)
    val allClasses = reflections.getSubTypesOf(classOf[ConfigurableStop[DataType]])
    val it = allClasses.iterator()
    while (it.hasNext) {
      breakable {
        val stop = it.next
        val stopName = stop.getName
        val stopClass = Class.forName(stopName)

        if (Modifier.isAbstract(stopClass.getModifiers)) {
          println("Stop " + stop.getName + " is interface!")
          break
        } else {
          val plugin = stopClass.newInstance()
          val stop = plugin.asInstanceOf[ConfigurableStop[DataType]]
          println("Find ConfigurableStop: " + stopName)
          stopList = stop +: stopList
        }
      }
    }

    // find external stop
    val pluginManager = PluginManager.getInstance
    val externalStopList = findAllConfigurableStopInClasspath[DataType]()

    stopList ::: externalStopList
  }

  private def findAllConfigurableStopInClasspath[DataType](): List[ConfigurableStop[DataType]] = {

    val pluginManager = PluginManager.getInstance
    val stopList = pluginManager.getPluginConfigurableStops[DataType]
    stopList

    /*val pluginManager = PluginManager.getInstance()
    var stopList:List[ConfigurableStop] = List()
    val classpath = System.getProperty("user.dir")+ "/classpath/"
    val classpathFile = new File(classpath)
    val jarFile = getJarFile(classpathFile)
    if(jarFile.size != 0){
      val finder = ClassFinder(jarFile)
      val classes = finder.getClasses

      val it = classes.iterator

      try{
        while(it.hasNext) {

          val externalClass = it.next()

          if(externalClass.superClassName.equals(configurableStopClass) &&
            !externalClass.name.equals(configurableStreamingStop) &&
            !externalClass.name.equals(configurableIncrementalStop)){

            //var classLoader = new URLClassLoader(Array(new File(classpath).toURI.toURL),this.getClass.getClassLoader )
            //val stopInstance = classLoader.loadClass(externalClass.name).newInstance()

            val stopInstance = pluginManager.getConfigurableStop(externalClass.name)
            println("Find Stop: " + externalClass.name)
            stopList = stopInstance.asInstanceOf[ConfigurableStop] +: stopList

          }
        }
      }catch {
        case e: UnsupportedOperationException => println("error")
      }

    }
    stopList*/
  }

  def findAllGroups(): List[String] = {

    val stopList = findAllConfigurableStop()

    val groupList = stopList
      .flatMap(stop => {
        // stop.getGroup()
        var group = List("")
        try {
          group = stop.getGroup()

        } catch {
          // case ex : Exception => println(ex)
          case ex: scala.NotImplementedError => println(stop.getClass.getName + " -> " + ex)
        }

        group

      })
      .distinct
      .filter(_ != "")
    groupList
  }

  private def findConfigurableStopInClasspath[DataType](
      bundle: String): Option[ConfigurableStop[DataType]] = {

    val pluginManager = PluginManager.getInstance
    val stopInstance = pluginManager.getConfigurableStop(bundle)
    val stop = Some(stopInstance.asInstanceOf[ConfigurableStop[DataType]])
    stop

    /*val pluginManager = PluginManager.getInstance
    val classpath = System.getProperty("user.dir")+ "/classpath/"
    var stop:Option[ConfigurableStop] = None

    val classpathFile = new File(classpath)
    //println("classpath is " + classpath)
    val finder = ClassFinder(getJarFile(classpathFile))
    val classes = finder.getClasses
    val it = classes.iterator

    while(it.hasNext) {
      val externalClass = it.next()
      if(externalClass.superClassName.equals(configurableStopClass)){
        if (externalClass.name.equals(bundle)){
          //val stopIntance = Class.forName(externalClass.name).newInstance()
          val stopInstance = pluginManager.getConfigurableStop(externalClass.name)
          stop = Some(stopInstance.asInstanceOf[ConfigurableStop])
          return stop
        }
      }
    }

    stop*/
  }

  def findConfigurableStop[DataType](bundle: String): ConfigurableStop[DataType] = {
    try {
      println("find ConfigurableStop by Class.forName: " + bundle)
      val stop = Class.forName(bundle).newInstance()
      stop.asInstanceOf[ConfigurableStop[DataType]]
    } catch {
      case classNotFoundException: ClassNotFoundException =>
        val pluginManager = PluginManager.getInstance
        if (pluginManager != null) {
          println("find ConfigurableStop in Classpath: " + bundle)
          val stop: Option[ConfigurableStop[DataType]] =
            ClassUtil.findConfigurableStopInClasspath(bundle)
          stop match {
            case Some(s) => s
            case _ => throw new ClassNotFoundException(bundle + " is not found!!!")
          }
        } else {
          println("Can not find Configurable: " + bundle)
          throw classNotFoundException
        }
      case ex: Exception =>
        println("Can not find Configurable: " + bundle)
        throw ex
    }
  }

  def findConfigurableStopPropertyDescriptor(bundle: String): List[PropertyDescriptor] = {
    val stopPropertyDesc = ClassUtil.findConfigurableStop(bundle)
    stopPropertyDesc.getPropertyDescriptor()
  }

  private def constructStopInfoJValue[DataType](
      bundle: String,
      stop: ConfigurableStop[DataType]): JValue = {
    val stopName = bundle.split("\\.").last
    val propertyDescriptorList: List[PropertyDescriptor] = stop.getPropertyDescriptor()
    propertyDescriptorList.foreach(p => if (p.allowableValues == null || p.allowableValues == None) p.allowableValues = List(""))
    val base64Encoder = new BASE64Encoder()
    var iconArrayByte: Array[Byte] = Array[Byte]()
    try {
      iconArrayByte = stop.getIcon()
    } catch {
      case ex: ClassNotFoundException => println(ex)
      case ex: NoSuchMethodError => println(ex)
    }

    // TODO: add properties for visualization stop
    //    var visualizationType = ""
    //    if (stop.isInstanceOf[VisualizationStop]){
    //      visualizationType = stop.asInstanceOf[VisualizationStop].visualizationType.toString()
    //    }

    val jsonValue =
      "StopInfo" ->
        ("name" -> stopName) ~
        ("bundle" -> bundle) ~
        ("engineType" -> stop.getEngineType) ~
        ("owner" -> stop.authorEmail) ~
        ("inports" -> stop.inportList.mkString(Constants.COMMA)) ~
        ("outports" -> stop.outportList.mkString(Constants.COMMA)) ~
        ("groups" -> stop.getGroup().mkString(Constants.COMMA)) ~
        ("isCustomized" -> stop.getCustomized().toString) ~
        // ("isDataSource" -> stop.getIsDataSource().toString) ~
        /*("customizedAllowKey" -> "") ~
          ("customizedAllowValue" -> "")*/
        // ("visualizationType" -> visualizationType) ~
        ("description" -> stop.description) ~
        ("icon" -> base64Encoder.encode(iconArrayByte)) ~
        ("properties" ->
          propertyDescriptorList.map {
            property =>
              ("name" -> property.name) ~
                ("displayName" -> property.displayName) ~
                ("description" -> property.description) ~
                ("defaultValue" -> property.defaultValue) ~
                ("allowableValues" -> property.allowableValues) ~
                ("required" -> property.required.toString) ~
                ("sensitive" -> property.sensitive.toString) ~
                ("example" -> property.example) ~
                ("order" -> property.order) ~
                ("language" -> property.language)
          })
    jsonValue
  }

  def findConfigurableStopInfo(bundle: String): String = {
    val stop = ClassUtil.findConfigurableStop(bundle)
    val jvalue = constructStopInfoJValue(bundle, stop)
    val jsonString = compactRender(jvalue)
    jsonString
  }

  def findConfigurableStopListInfo(bundleList: List[String]): String = {

    var stopInfoJValueList = List[JValue]()
    bundleList.foreach(bundle => {
      val stop = ClassUtil.findConfigurableStop(bundle)
      val stopJValue = constructStopInfoJValue(bundle, stop)
      stopInfoJValueList = stopJValue +: stopInfoJValueList
    })
    val stopInfoJValue = stopInfoJValueList
    val jsonString = compactRender(stopInfoJValue)
    jsonString
  }

}
