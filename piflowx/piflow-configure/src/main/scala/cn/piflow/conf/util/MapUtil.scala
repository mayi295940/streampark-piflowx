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

import scala.collection.mutable.{Map => MMap}

object MapUtil {

  def get(map: Map[String, Any], key: String, defaultValue: Any = null): Any = {
    map.get(key) match {
      case None =>
        if (defaultValue != null) {
          defaultValue
        } else {
          None
        }
      case Some(x: String) => x
      case Some(x: Integer) => x
      case Some(x: List[String]) => x
      case Some(x: List[Map[String, String]]) => x
      case Some(x: Map[String, Any]) => x
      case _ => throw new IllegalArgumentException
    }
  }

  def get(map: MMap[String, Any], key: String): Any = {
    map.get(key) match {
      case None => None
      case Some(x: String) => x
      case Some(x: Integer) => x
      case Some(x: List[String]) => x
      case Some(x: List[Map[String, String]]) => x
      case Some(x: Map[String, Any]) => MMap(x.toSeq: _*)
      case Some(x: MMap[String, Any]) => x
      case _ => throw new IllegalArgumentException
    }
  }
}
