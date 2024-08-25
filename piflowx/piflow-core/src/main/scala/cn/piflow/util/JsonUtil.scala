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

package cn.piflow.util

import com.alibaba.fastjson2.{JSON, JSONArray, JSONObject}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/** Created by xjzhu@cnic.cn on 4/30/19 */
object JsonUtil {

  def toJson(o: Any): String = {
    val json = new ListBuffer[String]()
    o match {
      case m: Map[_, _] => {
        for ((k, v) <- m) {
          val key = escape(k.asInstanceOf[String])
          v match {
            case a: Map[_, _] => json += "\"" + key + "\":" + toJson(a)
            case a: List[_] => json += "\"" + key + "\":" + toJson(a)
            case a: Int => json += "\"" + key + "\":" + a
            case a: Boolean => json += "\"" + key + "\":" + a
            case a: String => json += "\"" + key + "\":\"" + escape(a) + "\""
            case _ => ;
          }
        }
      }
      case m: List[_] => {
        val list = new ListBuffer[String]()
        for (el <- m) {
          el match {
            case a: Map[_, _] => list += toJson(a)
            case a: List[_] => list += toJson(a)
            case a: Int => list += a.toString
            case a: Boolean => list += a.toString
            case a: String => list += "\"" + escape(a) + "\""
            case _ => ;
          }
        }
        return "[" + list.mkString(",") + "]"
      }
      case _ => ;
    }
    "{" + json.mkString(",") + "}"
  }

  private def escape(s: String): String = {
    s.replaceAll("\"", "\\\\\"");
  }

  def format(t: Any, i: Int = 0): String = {
    val str = JSON.toJSONString(
      JSONObject.parseObject(t.toString),
      com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat)
    str
  }

  def jsonToSome(str: String): Some[Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    val map1: Map[String, Any] = map.map(x => {
      if (x._2.toString.startsWith("["))
        (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else (x._1, x._2)
    })
    Some(map1)
  }

  def jsonToMap(str: String): Map[String, Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    val map1: Map[String, Any] = map.map(x => {
      if (x._2.toString.startsWith("["))
        (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else (x._1, x._2)
    })
    map1
  }

  def mapToObject[T](o: Any, clazz: Class[T]): T = {
    JSONObject.parseObject[T](toJson(o), clazz)
  }

  private def jsonObjectToMapUtil(str: String): Map[String, Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    map.map(x => {
      if (x._2 == null) (x._1, "")
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else if (x._2.toString.startsWith("["))
        (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else (x._1, x._2)
    })
  }

  private def jsonArrayToMapUtil(jsonArray: JSONArray): List[Any] = {
    val list: List[Any] = jsonArray
      .toArray()
      .map {
        case (other) => if (other.toString.startsWith("{")) jsonObjectToMapUtil(other.toString)
        case (other) => other
      }
      .toList
    list
  }

}
