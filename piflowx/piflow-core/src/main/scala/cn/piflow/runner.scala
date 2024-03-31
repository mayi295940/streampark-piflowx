package cn.piflow

import cn.piflow.util._

import java.util.Date

import scala.collection.mutable.ArrayBuffer

trait Runner[DataType] {

  def bind(key: String, value: Any): Runner[DataType]

  def start(flow: Flow[DataType]): Process[DataType]

  def start(group: Group[DataType]): GroupExecution

  def addListener(listener: RunnerListener[DataType]): Unit

  def removeListener(listener: RunnerListener[DataType]): Unit

  def getListener: RunnerListener[DataType]
}

object Runner {

  def create[DataType](): Runner[DataType] = new Runner[DataType]() {

    val listeners: ArrayBuffer[RunnerListener[DataType]] =
      ArrayBuffer[RunnerListener[DataType]](new RunnerLogger())

    val compositeListener: RunnerListener[DataType] = new RunnerListener[DataType]() {
      override def onProcessStarted(ctx: ProcessContext[DataType]): Unit = {
        listeners.foreach(_.onProcessStarted(ctx))
      }

      override def onProcessFailed(ctx: ProcessContext[DataType]): Unit = {
        listeners.foreach(_.onProcessFailed(ctx))
      }

      override def onProcessCompleted(ctx: ProcessContext[DataType]): Unit = {
        listeners.foreach(_.onProcessCompleted(ctx))
      }

      override def onJobStarted(ctx: JobContext[DataType]): Unit = {
        listeners.foreach(_.onJobStarted(ctx))
      }

      override def onJobCompleted(ctx: JobContext[DataType]): Unit = {
        listeners.foreach(_.onJobCompleted(ctx))
      }

      override def onJobInitialized(ctx: JobContext[DataType]): Unit = {
        listeners.foreach(_.onJobInitialized(ctx))
      }

      override def onProcessForked(
          ctx: ProcessContext[DataType],
          child: ProcessContext[DataType]): Unit = {
        listeners.foreach(_.onProcessForked(ctx, child))
      }

      override def onJobFailed(ctx: JobContext[DataType]): Unit = {
        listeners.foreach(_.onJobFailed(ctx))
      }

      override def onProcessAborted(ctx: ProcessContext[DataType]): Unit = {
        listeners.foreach(_.onProcessAborted(ctx))
      }

      override def monitorJobCompleted(
          ctx: JobContext[DataType],
          outputs: JobOutputStream[DataType]): Unit = {
        // TODO:
        listeners.foreach(_.monitorJobCompleted(ctx, outputs))
      }

      override def onGroupStarted(ctx: GroupContext[DataType]): Unit = {
        listeners.foreach(_.onGroupStarted(ctx))
      }

      override def onGroupCompleted(ctx: GroupContext[DataType]): Unit = {
        listeners.foreach(_.onGroupCompleted(ctx))
      }

      override def onGroupFailed(ctx: GroupContext[DataType]): Unit = {
        listeners.foreach(_.onGroupFailed(ctx))
      }

      override def onGroupStoped(ctx: GroupContext[DataType]): Unit = {
        // TODO
      }
    }

    override def addListener(listener: RunnerListener[DataType]): Unit = {
      listeners += listener
    }

    override def getListener: RunnerListener[DataType] = compositeListener

    val ctx = new CascadeContext[DataType]()

    override def bind(key: String, value: Any): this.type = {
      ctx.put(key, value)
      this
    }

    override def start(flow: Flow[DataType]): Process[DataType] = {
      new ProcessImpl[DataType](flow, ctx, this)
    }

    override def start(group: Group[DataType]): GroupExecution = {
      new GroupExecutionImpl(group, ctx, this)
    }

    override def removeListener(listener: RunnerListener[DataType]): Unit = {
      listeners -= listener
    }
  }
}

trait RunnerListener[DataType] {
  def onProcessStarted(ctx: ProcessContext[DataType]): Unit

  def onProcessForked(ctx: ProcessContext[DataType], child: ProcessContext[DataType]): Unit

  def onProcessCompleted(ctx: ProcessContext[DataType]): Unit

  def onProcessFailed(ctx: ProcessContext[DataType]): Unit

  def onProcessAborted(ctx: ProcessContext[DataType]): Unit

  def onJobInitialized(ctx: JobContext[DataType]): Unit

  def onJobStarted(ctx: JobContext[DataType]): Unit

  def onJobCompleted(ctx: JobContext[DataType]): Unit

  def onJobFailed(ctx: JobContext[DataType]): Unit

  def monitorJobCompleted(ctx: JobContext[DataType], outputs: JobOutputStream[DataType]): Unit

  def onGroupStarted(ctx: GroupContext[DataType]): Unit

  def onGroupCompleted(ctx: GroupContext[DataType]): Unit

  def onGroupFailed(ctx: GroupContext[DataType]): Unit

  def onGroupStoped(ctx: GroupContext[DataType]): Unit

}

class RunnerLogger[DataType] extends RunnerListener[DataType] with Logging {
  // TODO: add GroupID or ProjectID
  override def onProcessStarted(ctx: ProcessContext[DataType]): Unit = {
    val pid = ctx.getProcess.pid()
    val flowName = ctx.getFlow.toString
    val time = new Date().toString
    logger.debug(s"process started: $pid, flow: $flowName, time: $time")
    println(s"process started: $pid, flow: $flowName, time: $time")
    // update flow state to STARTED
    val appId = getAppId(ctx)
    H2Util.addFlow(appId, pid, ctx.getFlow.getFlowName)
    H2Util.updateFlowState(appId, FlowState.STARTED)
    H2Util.updateFlowStartTime(appId, time)
  }

  override def onJobStarted(ctx: JobContext[DataType]): Unit = {
    val jid = ctx.getStopJob.jid()
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job started: $jid, stop: $stopName, time: $time")
    println(s"job started: $jid, stop: $stopName, time: $time")
    // update stop state to STARTED
    H2Util.updateStopState(getAppId(ctx), stopName, StopState.STARTED)
    H2Util.updateStopStartTime(getAppId(ctx), stopName, time)
  }

  override def onJobFailed(ctx: JobContext[DataType]): Unit = {
    ctx.getProcessContext
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job failed: $stopName, time: $time")
    println(s"job failed: $stopName, time: $time")
    // update stop state to FAILED
    H2Util.updateStopFinishedTime(getAppId(ctx), stopName, time)
    H2Util.updateStopState(getAppId(ctx), stopName, StopState.FAILED)

  }

  override def onJobInitialized(ctx: JobContext[DataType]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job initialized: $stopName, time: $time")
    println(s"job initialized: $stopName, time: $time")
    // add stop into h2 db and update stop state to INIT
    val appId = getAppId(ctx)
    H2Util.addStop(appId, stopName)
    H2Util.updateStopState(appId, stopName, StopState.INIT)
  }

  override def onProcessCompleted(ctx: ProcessContext[DataType]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process completed: $pid, time: $time")
    println(s"process completed: $pid, time: $time")
    // update flow state to COMPLETED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(appId, FlowState.COMPLETED)

  }

  override def onJobCompleted(ctx: JobContext[DataType]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job completed: $stopName, time: $time")
    println(s"job completed: $stopName, time: $time")
    // update stop state to COMPLETED
    val appId = getAppId(ctx)
    H2Util.updateStopFinishedTime(appId, stopName, time)
    H2Util.updateStopState(appId, stopName, StopState.COMPLETED)

  }

  override def onProcessFailed(ctx: ProcessContext[DataType]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process failed: $pid, time: $time")
    println(s"process failed: $pid, time: $time")
    // update flow state to FAILED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(getAppId(ctx), FlowState.FAILED)

  }

  override def onProcessAborted(ctx: ProcessContext[DataType]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process aborted: $pid, time: $time")
    println(s"process aborted: $pid, time: $time")
    // update flow state to ABORTED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(appId, FlowState.ABORTED)

  }

  override def onProcessForked(
      ctx: ProcessContext[DataType],
      child: ProcessContext[DataType]): Unit = {
    val pid = ctx.getProcess.pid()
    val cid = child.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process forked: $pid, child flow execution: $cid, time: $time")
    println(s"process forked: $pid, child flow execution: $cid, time: $time")
    // update flow state to FORK
    H2Util.updateFlowState(getAppId(ctx), FlowState.FORK)
  }

  private def getAppId(ctx: Context[DataType]): String = {
    // todo
    //    val sparkSession = ctx.get(classOf[SparkSession].getName).asInstanceOf[SparkSession]
    //    sparkSession.sparkContext.applicationId
    ""
  }

  override def monitorJobCompleted(
      ctx: JobContext[DataType],
      outputs: JobOutputStream[DataType]): Unit = {
    val appId = getAppId(ctx)
    val stopName = ctx.getStopJob.getStopName
    logger.debug(s"job completed: monitor $stopName")
    println(s"job completed: monitor $stopName")

  }

  override def onGroupStarted(ctx: GroupContext[DataType]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val childCount = ctx.getGroupExecution.getChildCount
    val time = new Date().toString
    // val flowCount = ctx.getGroupExecution().getFlowCount()
    logger.debug(s"Group started: $groupId, group: $flowGroupName, time: $time")
    println(s"Group started: $groupId, group: $flowGroupName, time: $time")
    // update flow group state to STARTED
    H2Util.addGroup(groupId, flowGroupName, childCount)
    H2Util.updateGroupState(groupId, GroupState.STARTED)
    H2Util.updateGroupStartTime(groupId, time)
  }

  override def onGroupCompleted(ctx: GroupContext[DataType]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group completed: $groupId, time: $time")
    println(s"Group completed: $groupId, time: $time")
    // update flow group state to COMPLETED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.COMPLETED)

  }

  override def onGroupStoped(ctx: GroupContext[DataType]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group stoped: $groupId, time: $time")
    println(s"Group stoped: $groupId, time: $time")
    // update flow group state to COMPLETED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.KILLED)

  }

  override def onGroupFailed(ctx: GroupContext[DataType]): Unit = {
    // TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val time = new Date().toString
    logger.debug(s"Group failed: $groupId, time: $time")
    println(s"Group failed: $groupId, time: $time")
    // update flow group state to FAILED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.FAILED)

  }

}
