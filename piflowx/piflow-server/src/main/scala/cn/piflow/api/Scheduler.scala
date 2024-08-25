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

package cn.piflow.api

import akka.actor.Actor
import cn.piflow.util.H2Util

/** Created by xjzhu@cnic.cn on 5/21/19 */

object ScheduleType {
  val FLOW = "Flow"
  val GROUP = "Group"
}

class ExecutionActor(id: String, scheduleType: String) extends Actor {

  override def receive: Receive = {
    case json: String => {
      scheduleType match {
        case ScheduleType.FLOW => {
          val (appId, process) = API.startFlow(json)
          H2Util.addScheduleEntry(id, appId, ScheduleType.FLOW)
        }
        case ScheduleType.GROUP =>
          val groupExecution = API.startGroup(json)
          H2Util.addScheduleEntry(id, groupExecution.getGroupId, ScheduleType.GROUP)
      }
    }
    case _ => println("error type!")
  }
}
