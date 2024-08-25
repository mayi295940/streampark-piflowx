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

/** Created by xjzhu@cnic.cn on 4/25/19 */
class ConditionBean {

  var entry: String = _
  var after: List[String] = _

  def init(entry: String, after: String) = {
    this.entry = entry
    this.after = after.split(",").toList
  }
  def init(map: Map[String, Any]) = {
    this.entry = MapUtil.get(map, "entry").asInstanceOf[String]
    this.after = MapUtil.get(map, "after").asInstanceOf[String].split(",").toList
  }

}
object ConditionBean {

  def apply(map: Map[String, Any]): ConditionBean = {
    val conditionBean = new ConditionBean()
    conditionBean.init(map)
    conditionBean
  }
}
