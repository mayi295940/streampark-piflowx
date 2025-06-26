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

package cn.piflow

import cn.piflow.util._

import java.util.Date

import scala.collection.mutable.ArrayBuffer

trait Runner[StreamingContext, DataType, DStream] {

  def bind(key: String, value: Any): Runner[StreamingContext, DataType, DStream]

  def start(flow: Flow[StreamingContext, DataType, DStream]): Process[StreamingContext, DataType, DStream]

  def start(group: Group[StreamingContext, DataType, DStream]): GroupExecution

  def addListener(listener: RunnerListener[StreamingContext, DataType, DStream]): Unit

  def removeListener(listener: RunnerListener[StreamingContext, DataType, DStream]): Unit

  def getListener: RunnerListener[StreamingContext, DataType, DStream]
}

object Runner {

  def create[StreamingContext, DataType, DStream](): Runner[StreamingContext, DataType, DStream] = new Runner[StreamingContext, DataType, DStream]() {

    val listeners: ArrayBuffer[RunnerListener[StreamingContext, DataType, DStream]] =
      ArrayBuffer[RunnerListener[StreamingContext, DataType, DStream]](new RunnerLogger())

    val compositeListener: RunnerListener[StreamingContext, DataType, DStream] = new RunnerListener[StreamingContext, DataType, DStream]() {
      override def onProcessStarted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onProcessStarted(ctx))
      }

      override def onProcessFailed(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onProcessFailed(ctx))
      }

      override def onProcessCompleted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onProcessCompleted(ctx))
      }

      override def onJobStarted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onJobStarted(ctx))
      }

      override def onJobCompleted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onJobCompleted(ctx))
      }

      override def onJobInitialized(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onJobInitialized(ctx))
      }

      override def onProcessForked(
          ctx: ProcessContext[StreamingContext, DataType, DStream],
          child: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onProcessForked(ctx, child))
      }

      override def onJobFailed(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onJobFailed(ctx))
      }

      override def onProcessAborted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onProcessAborted(ctx))
      }

      override def monitorJobCompleted(
          ctx: JobContext[StreamingContext, DataType, DStream],
          outputs: JobOutputStream[StreamingContext, DataType, DStream]): Unit = {
        // TODO:
        listeners.foreach(_.monitorJobCompleted(ctx, outputs))
      }

      override def onGroupStarted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onGroupStarted(ctx))
      }

      override def onGroupCompleted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onGroupCompleted(ctx))
      }

      override def onGroupFailed(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
        listeners.foreach(_.onGroupFailed(ctx))
      }

      override def onGroupStoped(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
        // TODO
      }
    }

    override def addListener(listener: RunnerListener[StreamingContext, DataType, DStream]): Unit = {
      listeners += listener
    }

    override def getListener: RunnerListener[StreamingContext, DataType, DStream] = compositeListener

    val ctx = new CascadeContext[StreamingContext, DataType, DStream]()

    override def bind(key: String, value: Any): this.type = {
      ctx.put(key, value)
      this
    }

    override def start(flow: Flow[StreamingContext, DataType, DStream]): Process[StreamingContext, DataType, DStream] = {
      new ProcessImpl[StreamingContext, DataType, DStream](flow, ctx, this)
    }

    override def start(group: Group[StreamingContext, DataType, DStream]): GroupExecution = {
      new GroupExecutionImpl(group, ctx, this)
    }

    override def removeListener(listener: RunnerListener[StreamingContext, DataType, DStream]): Unit = {
      listeners -= listener
    }
  }
}

trait RunnerListener[StreamingContext, DataType, DStream] {
  def onProcessStarted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def onProcessForked(ctx: ProcessContext[StreamingContext, DataType, DStream], child: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def onProcessCompleted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def onProcessFailed(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def onProcessAborted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def onJobInitialized(ctx: JobContext[StreamingContext, DataType, DStream]): Unit

  def onJobStarted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit

  def onJobCompleted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit

  def onJobFailed(ctx: JobContext[StreamingContext, DataType, DStream]): Unit

  def monitorJobCompleted(ctx: JobContext[StreamingContext, DataType, DStream], outputs: JobOutputStream[StreamingContext, DataType, DStream]): Unit

  def onGroupStarted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit

  def onGroupCompleted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit

  def onGroupFailed(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit

  def onGroupStoped(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit

}

class RunnerLogger[StreamingContext, DataType, DStream] extends RunnerListener[StreamingContext, DataType, DStream] with Logging {
  // TODO: add GroupID or ProjectID
  override def onProcessStarted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val flowName = ctx.getFlow.toString
    val time = new Date().toString
    logger.debug(s"process started: $pid, flow: $flowName, time: $time")
    println(s"process started: $pid, flow: $flowName, time: $time")
    // update flow state to STARTED
    val appId = getAppId(ctx)
    DataBaseUtil.addFlow(appId, pid, ctx.getFlow.getFlowName, ctx.get("jobId").asInstanceOf[String])
    DataBaseUtil.updateFlowState(appId, FlowState.STARTED)
    DataBaseUtil.updateFlowStartTime(appId, time)
  }

  override def onJobStarted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
    val jid = ctx.getStopJob.jid()
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job started: $jid, stop: $stopName, time: $time")
    println(s"job started: $jid, stop: $stopName, time: $time")
    // update stop state to STARTED
    val appId = getAppId(ctx)
    DataBaseUtil.updateStopState(appId, stopName, StopState.STARTED)
    DataBaseUtil.updateStopStartTime(appId, stopName, time)
  }

  override def onJobFailed(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
    ctx.getProcessContext
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job failed: $stopName, time: $time")
    println(s"job failed: $stopName, time: $time")
    // update stop state to FAILED
    DataBaseUtil.updateStopFinishedTime(getAppId(ctx), stopName, time)
    DataBaseUtil.updateStopState(getAppId(ctx), stopName, StopState.FAILED)

  }

  override def onJobInitialized(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job initialized: $stopName, time: $time")
    println(s"job initialized: $stopName, time: $time")
    // add stop into h2 db and update stop state to INIT
    val appId = getAppId(ctx)
    DataBaseUtil.addStop(appId, stopName)
    DataBaseUtil.updateStopState(appId, stopName, StopState.INIT)
  }

  override def onProcessCompleted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process completed: $pid, time: $time")
    println(s"process completed: $pid, time: $time")
    // update flow state to COMPLETED
    val appId = getAppId(ctx)
    DataBaseUtil.updateFlowFinishedTime(appId, time)
    DataBaseUtil.updateFlowState(appId, FlowState.COMPLETED)

  }

  override def onJobCompleted(ctx: JobContext[StreamingContext, DataType, DStream]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job completed: $stopName, time: $time")
    println(s"job completed: $stopName, time: $time")
    // update stop state to COMPLETED
    val appId = getAppId(ctx)
    DataBaseUtil.updateStopFinishedTime(appId, stopName, time)
    DataBaseUtil.updateStopState(appId, stopName, StopState.COMPLETED)

  }

  override def onProcessFailed(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process failed: $pid, time: $time")
    println(s"process failed: $pid, time: $time")
    // update flow state to FAILED
    val appId = getAppId(ctx)
    DataBaseUtil.updateFlowFinishedTime(appId, time)
    DataBaseUtil.updateFlowState(getAppId(ctx), FlowState.FAILED)

  }

  override def onProcessAborted(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process aborted: $pid, time: $time")
    println(s"process aborted: $pid, time: $time")
    // update flow state to ABORTED
    val appId = getAppId(ctx)
    DataBaseUtil.updateFlowFinishedTime(appId, time)
    DataBaseUtil.updateFlowState(appId, FlowState.ABORTED)

  }

  override def onProcessForked(
      ctx: ProcessContext[StreamingContext, DataType, DStream],
      child: ProcessContext[StreamingContext, DataType, DStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val cid = child.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process forked: $pid, child flow execution: $cid, time: $time")
    println(s"process forked: $pid, child flow execution: $cid, time: $time")
    // update flow state to FORK
    DataBaseUtil.updateFlowState(getAppId(ctx), FlowState.FORK)
  }

  private def getAppId(ctx: Context[StreamingContext, DataType, DStream]): String = {
    ctx.get("applicationId").asInstanceOf[String]
  }

  override def monitorJobCompleted(
      ctx: JobContext[StreamingContext, DataType, DStream],
      outputs: JobOutputStream[StreamingContext, DataType, DStream]): Unit = {
    val appId = getAppId(ctx)
    val stopName = ctx.getStopJob.getStopName
    logger.debug(s"job completed: monitor $stopName")
    println(s"job completed: monitor $stopName")

  }

  override def onGroupStarted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val childCount = ctx.getGroupExecution.getChildCount
    val time = new Date().toString
    // val flowCount = ctx.getGroupExecution().getFlowCount()
    logger.debug(s"Group started: $groupId, group: $flowGroupName, time: $time")
    println(s"Group started: $groupId, group: $flowGroupName, time: $time")
    // update flow group state to STARTED
    DataBaseUtil.addGroup(groupId, flowGroupName, childCount)
    DataBaseUtil.updateGroupState(groupId, GroupState.STARTED)
    DataBaseUtil.updateGroupStartTime(groupId, time)
  }

  override def onGroupCompleted(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group completed: $groupId, time: $time")
    println(s"Group completed: $groupId, time: $time")
    // update flow group state to COMPLETED
    DataBaseUtil.updateGroupFinishedTime(groupId, time)
    DataBaseUtil.updateGroupState(groupId, GroupState.COMPLETED)

  }

  override def onGroupStoped(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group stoped: $groupId, time: $time")
    println(s"Group stoped: $groupId, time: $time")
    // update flow group state to COMPLETED
    DataBaseUtil.updateGroupFinishedTime(groupId, time)
    DataBaseUtil.updateGroupState(groupId, GroupState.KILLED)

  }

  override def onGroupFailed(ctx: GroupContext[StreamingContext, DataType, DStream]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val time = new Date().toString
    logger.debug(s"Group failed: $groupId, time: $time")
    println(s"Group failed: $groupId, time: $time")
    // update flow group state to FAILED
    DataBaseUtil.updateGroupFinishedTime(groupId, time)
    DataBaseUtil.updateGroupState(groupId, GroupState.FAILED)

  }

}
