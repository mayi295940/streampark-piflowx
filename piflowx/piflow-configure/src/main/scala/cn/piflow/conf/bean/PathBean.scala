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

package cn.piflow.conf.bean

import cn.piflow.conf.util.MapUtil

class PathBean {

  var from: String = _
  var outport: String = _
  var inport: String = _
  var to: String = _

  def init(from: String, outport: String, inport: String, to: String): Unit = {
    this.from = from
    this.outport = outport
    this.inport = inport
    this.to = to
  }

  def init(map: Map[String, Any]): Unit = {
    this.from = MapUtil.get(map, "from").asInstanceOf[String]
    this.outport = MapUtil.get(map, "outport").asInstanceOf[String]
    this.inport = MapUtil.get(map, "inport").asInstanceOf[String]
    this.to = MapUtil.get(map, "to").asInstanceOf[String]
  }

}

object PathBean {
  def apply(map: Map[String, Any]): PathBean = {
    val pathBean = new PathBean()
    pathBean.init(map)
    pathBean
  }
}
