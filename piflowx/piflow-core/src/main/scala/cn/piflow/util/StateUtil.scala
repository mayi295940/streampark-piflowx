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

object StateUtil {

  val yarnInitState = List(
    FlowYarnState.NEW,
    FlowYarnState.NEW_SAVING,
    FlowYarnState.SUBMITTED,
    FlowYarnState.ACCEPTED)
  val yarnStartedState = List(FlowYarnState.RUNNING)
  val yarnCompletedState = List(FlowYarnState.FINISHED)
  val yarnFailedState = List(FlowYarnState.FAILED)
  val yarnKilledState = List(FlowYarnState.KILLED)

  def FlowStateCheck(flowState: String, flowYarnState: String): Boolean = {

    if (flowState == FlowState.INIT && yarnInitState.contains(flowYarnState)) {
      true
    } else if (flowState == FlowState.STARTED && yarnStartedState.contains(flowYarnState)) {
      true
    } else if (flowState == FlowState.COMPLETED && yarnCompletedState.contains(flowYarnState)) {
      true
    } else if (flowState == FlowState.FAILED && yarnFailedState.contains(flowYarnState)) {
      true
    } else if (flowState == FlowState.KILLED && yarnKilledState.contains(flowYarnState)) {
      true
    } else {
      false
    }

  }

  def getNewFlowState(flowState: String, flowYarnState: String): String = {

    if (yarnStartedState.contains(flowYarnState)) {
      FlowState.STARTED
    } else if (yarnCompletedState.contains(flowYarnState)) {
      FlowState.COMPLETED
    } else if (yarnFailedState.contains(flowYarnState)) {
      FlowState.FAILED
    } else if (yarnKilledState.contains(flowYarnState)) {
      FlowState.KILLED
    } else {
      ""
    }
  }

}
