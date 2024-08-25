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

import cn.piflow.{Condition, GroupImpl}
import cn.piflow.conf.util.MapUtil

/** Created by xjzhu@cnic.cn on 4/25/19 */
class GroupBean[DataType] extends GroupEntryBean {

  var uuid: String = _
  var name: String = _
  private var groupEntries: List[GroupEntryBean] = List()

  var conditions = scala.collection.mutable.Map[String, ConditionBean]()

  def init(map: Map[String, Any]): Unit = {

    val groupMap = MapUtil.get(map, "group").asInstanceOf[Map[String, Any]]

    this.uuid = MapUtil.get(groupMap, "uuid").asInstanceOf[String]
    this.name = MapUtil.get(groupMap, "name").asInstanceOf[String]

    // construct GroupBean List
    if (MapUtil.get(groupMap, "groups") != None) {
      val groupList = MapUtil.get(groupMap, "groups").asInstanceOf[List[Map[String, Any]]]
      groupList.foreach(groupMap => {
        val group = GroupBean(groupMap)
        this.groupEntries = group +: this.groupEntries
      })
    }

    // construct FlowBean List
    if (MapUtil.get(groupMap, "flows") != None) {
      val flowList = MapUtil.get(groupMap, "flows").asInstanceOf[List[Map[String, Any]]]
      flowList.foreach(flowMap => {
        val flow = FlowBean(flowMap.asInstanceOf[Map[String, Any]])
        this.groupEntries = flow +: this.groupEntries
      })
    }

    // construct ConditionBean List
    if (MapUtil.get(groupMap, "conditions") != None) {
      val conditionList = MapUtil.get(groupMap, "conditions").asInstanceOf[List[Map[String, Any]]]
      conditionList.foreach(conditionMap => {
        val conditionBean = ConditionBean(conditionMap.asInstanceOf[Map[String, Any]])
        conditions(conditionBean.entry) = conditionBean
      })
    }

  }

  def constructGroup(): GroupImpl[DataType] = {
    val group = new GroupImpl[DataType]();
    group.setGroupName(name)

    this.groupEntries.foreach(groupEntryBean => {
      if (!conditions.contains(groupEntryBean.name)) {
        if (groupEntryBean.isInstanceOf[FlowBean[DataType]]) {
          val bean = groupEntryBean.asInstanceOf[FlowBean[DataType]]
          group.addGroupEntry(groupEntryBean.name, bean.constructFlow())
        } else {
          val groupBean = groupEntryBean.asInstanceOf[GroupBean[DataType]]
          group.addGroupEntry(groupBean.name, groupBean.constructGroup())
        }

      } else {
        val conditionBean = conditions(groupEntryBean.name)

        if (conditionBean.after.isEmpty) {

          println(
            groupEntryBean.name + " do not have after flow " + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

          if (groupEntryBean.isInstanceOf[FlowBean[DataType]]) {
            val bean = groupEntryBean.asInstanceOf[FlowBean[DataType]]
            group.addGroupEntry(groupEntryBean.name, bean.constructFlow())
          } else {
            val groupBean = groupEntryBean.asInstanceOf[GroupBean[DataType]]
            group.addGroupEntry(groupBean.name, groupBean.constructGroup())
          }
        } else if (conditionBean.after.size == 1) {
          println(
            groupEntryBean.name + " after " + conditionBean.after.head + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

          if (groupEntryBean.isInstanceOf[FlowBean[DataType]]) {
            val bean = groupEntryBean.asInstanceOf[FlowBean[DataType]]
            group.addGroupEntry(
              groupEntryBean.name,
              bean.constructFlow(),
              Condition.after(conditionBean.after.head))
          } else {
            val groupBean = groupEntryBean.asInstanceOf[GroupBean[DataType]]
            group.addGroupEntry(
              groupBean.name,
              groupBean.constructGroup(),
              Condition.after(conditionBean.after.head))
          }

        } else {
          println(
            groupEntryBean.name + " after " + conditionBean.after.toSeq + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

          var other = new Array[String](conditionBean.after.size - 1)
          conditionBean.after.copyToArray(other, 1)

          if (groupEntryBean.isInstanceOf[FlowBean[DataType]]) {
            val bean = groupEntryBean.asInstanceOf[FlowBean[DataType]]
            group.addGroupEntry(
              groupEntryBean.name,
              bean.constructFlow(),
              Condition.after(conditionBean.after.head, other: _*))
          } else {
            val groupBean = groupEntryBean.asInstanceOf[GroupBean[DataType]]
            group.addGroupEntry(
              groupBean.name,
              groupBean.constructGroup(),
              Condition.after(conditionBean.after.head, other: _*))
          }
        }
      }

    })

    group
  }

}

object GroupBean {
  def apply[DataType](map: Map[String, Any]): GroupBean[DataType] = {

    val groupBean = new GroupBean[DataType]()
    groupBean.init(map)
    groupBean
  }
}
