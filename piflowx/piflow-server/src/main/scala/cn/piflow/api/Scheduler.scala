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
