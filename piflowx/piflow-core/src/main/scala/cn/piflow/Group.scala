package cn.piflow

import cn.piflow.util._

import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable.{ArrayBuffer, Map => MMap}
import scala.util.{Failure, Success, Try}

/** Created by xjzhu@cnic.cn on 4/25/19 */

trait Group[DataType] extends GroupEntry[DataType] {

  def addGroupEntry(
      name: String,
      flowOrGroup: GroupEntry[DataType],
      con: Condition[GroupExecution] = Condition.AlwaysTrue[GroupExecution]()): Unit

  def mapFlowWithConditions(): Map[String, (GroupEntry[DataType], Condition[GroupExecution])]

  def getGroupName: String

  def setGroupName(groupName: String): Unit

  def getParentGroupId: String

  def setParentGroupId(groupId: String): Unit

}

class GroupImpl[DataType] extends Group[DataType] {
  var name = ""
  var uuid = ""
  var parentId = ""

  private val _mapFlowWithConditions =
    MMap[String, (GroupEntry[DataType], Condition[GroupExecution])]()

  def addGroupEntry(
      name: String,
      flowOrGroup: GroupEntry[DataType],
      con: Condition[GroupExecution] = Condition.AlwaysTrue[GroupExecution]()): Unit = {

    _mapFlowWithConditions(name) = flowOrGroup -> con
  }

  def mapFlowWithConditions(): Map[String, (GroupEntry[DataType], Condition[GroupExecution])] =
    _mapFlowWithConditions.toMap

  override def getGroupName: String = {
    this.name
  }

  override def setGroupName(groupName: String): Unit = {
    this.name = groupName
  }

  override def getParentGroupId: String = {
    this.parentId
  }

  override def setParentGroupId(groupId: String): Unit = {
    this.parentId = groupId
  }

}

trait GroupExecution extends Execution {

  def stop(): Unit

  def awaitTermination(): Unit

  def awaitTermination(timeout: Long, unit: TimeUnit): Unit

  def getGroupId: String

  def getChildCount: Int
}

class GroupExecutionImpl[DataType](
    group: Group[DataType],
    runnerContext: Context[DataType],
    runner: Runner[DataType])
  extends GroupExecution {

  private val groupContext = createContext(runnerContext)
  val groupExecution: GroupExecutionImpl[DataType] = this

  val id: String = "group_" + IdGenerator.uuid

  private val mapGroupEntryWithConditions
      : Map[String, (GroupEntry[DataType], Condition[GroupExecution])] =
    group.mapFlowWithConditions()

  private val completedGroupEntry = MMap[String, Boolean]()
  completedGroupEntry ++= mapGroupEntryWithConditions.map(x => (x._1, false))

  private val numWaitingGroupEntry = new AtomicInteger(mapGroupEntryWithConditions.size)

  // todo
  //  val startedProcesses = MMap[String, SparkAppHandle]();
  private val startedProcesses = MMap[String, Any]()
  private val startedGroup = MMap[String, GroupExecution]()

  private val execution = this
  private val POLLING_INTERVAL = 1000
  val latch = new CountDownLatch(1)
  var running = true

  val listener: RunnerListener[DataType] = new RunnerListener[DataType] {

    override def onProcessStarted(ctx: ProcessContext[DataType]): Unit = {}

    override def onProcessFailed(ctx: ProcessContext[DataType]): Unit = {
      // TODO: retry?
    }

    override def onProcessCompleted(ctx: ProcessContext[DataType]): Unit = {}

    override def onJobStarted(ctx: JobContext[DataType]): Unit = {}

    override def onJobCompleted(ctx: JobContext[DataType]): Unit = {}

    override def onJobInitialized(ctx: JobContext[DataType]): Unit = {}

    override def onProcessForked(
        ctx: ProcessContext[DataType],
        child: ProcessContext[DataType]): Unit = {}

    override def onJobFailed(ctx: JobContext[DataType]): Unit = {}

    override def onProcessAborted(ctx: ProcessContext[DataType]): Unit = {}

    override def monitorJobCompleted(
        ctx: JobContext[DataType],
        outputs: JobOutputStream[DataType]): Unit = {}

    override def onGroupStarted(ctx: GroupContext[DataType]): Unit = {}

    override def onGroupCompleted(ctx: GroupContext[DataType]): Unit = {
      startedGroup.filter(_._2 == ctx.getGroupExecution).foreach {
        x =>
          completedGroupEntry(x._1) = true
          numWaitingGroupEntry.decrementAndGet()
      }
    }

    override def onGroupStoped(ctx: GroupContext[DataType]): Unit = {}

    override def onGroupFailed(ctx: GroupContext[DataType]): Unit = {}
  }

  runner.addListener(listener)
  val runnerListener: RunnerListener[DataType] = runner.getListener

  def isEntryCompleted(name: String): Boolean = {
    completedGroupEntry(name)
  }

  private def startProcess(name: String, flow: Flow[DataType], groupId: String = ""): Unit = {

    println(flow.getFlowJson)

    var flowJson = flow.getFlowJson

    var appId: String = ""
    val countDownLatch = new CountDownLatch(1)

    //    val handle = FlowLauncher.launch(flow).startApplication(new SparkAppHandle.Listener {
    //      override def stateChanged(handle: SparkAppHandle): Unit = {
    //        appId = handle.getAppId
    //        val sparkAppState = handle.getState
    //        if (appId != null) {
    //          println("Spark job with app id: " + appId + ",\t State changed to: " + sparkAppState)
    //        } else {
    //          println("Spark job's state changed to: " + sparkAppState)
    //        }
    //        if (!H2Util.getFlowProcessId(appId).equals("")) {
    //          //H2Util.updateFlowGroupId(appId,groupId)
    //        }
    //
    //        if (H2Util.getFlowState(appId).equals(FlowState.COMPLETED)) {
    //          completedGroupEntry(flow.getFlowName()) = true;
    //          numWaitingGroupEntry.decrementAndGet();
    //        }
    //
    //        if (handle.getState().isFinal) {
    //          countDownLatch.countDown()
    //          println("Task is finished!")
    //        }
    //      }
    //
    //      override def infoChanged(handle: SparkAppHandle): Unit = {
    //
    //      }
    //    }
    //    )
    //
    //
    //    while (handle.getAppId == null) {
    //
    //      Thread.sleep(100)
    //    }
    //    appId = handle.getAppId
    //
    //    //wait flow process started
    //    while (H2Util.getFlowProcessId(appId).equals("")) {
    //      Thread.sleep(1000)
    //    }
    //
    //    if (groupId != "") {
    //      H2Util.updateFlowGroupId(appId, groupId)
    //    }
    //
    //    startedProcesses(name) = handle
    //    startedProcessesAppID(name) = appId
  }

  private def startGroup(name: String, group: Group[DataType], parentId: String): Unit = {
    val groupExecution = runner.start(group)
    startedGroup(name) = groupExecution
    val groupId = groupExecution.getGroupId
    while (H2Util.getGroupState(groupId).equals("")) {
      Thread.sleep(1000)
    }
    if (parentId != "") {
      H2Util.updateGroupParent(groupId, parentId)
    }
  }

  @volatile
  private var maybeException: Option[Throwable] = None

  private val pollingThread = new Thread(new Runnable() {
    override def run(): Unit = {

      runnerListener.onGroupStarted(groupContext)

      try {
        while (numWaitingGroupEntry.get() > 0) {

          val (todosFlow, todosGroup) = {
            getTodos
          }

          if (
            todosFlow.isEmpty && todosGroup.isEmpty
            && H2Util.isGroupChildError(id)
            && !H2Util.isGroupChildRunning(id)
          ) {

            val (todosFlow, todosGroup) = getTodos
            if (todosFlow.isEmpty && todosGroup.isEmpty)
              throw new GroupException("Group Failed!")
          }

          startedProcesses.synchronized {
            todosFlow.foreach(
              en => {
                startProcess(en._1, en._2, id)
              })
          }
          startedGroup.synchronized {
            todosGroup.foreach(
              en => {
                startGroup(en._1, en._2, id)
              })
          }

          Thread.sleep(POLLING_INTERVAL)
        }

        runnerListener.onGroupCompleted(groupContext)

      } catch {
        case e: Throwable =>
          runnerListener.onGroupFailed(groupContext)
          println(e)
          if (e.isInstanceOf[GroupException])
            throw e
      } finally {
        latch.countDown()
        finalizeExecution(true)
      }
    }
  })

  private val doit = Try {
    pollingThread.setUncaughtExceptionHandler(
      (thread: Thread, throwable: Throwable) => {
        maybeException = Some(throwable)
      })
    pollingThread.start()
    // pollingThread.join()
  }

  doit match {
    case Success(_) =>
      println("Did not capture error!")
    case Failure(_) =>
      println("Capture error!")
      runnerListener.onGroupFailed(groupContext)
  }

  override def awaitTermination(): Unit = {
    latch.await()
    finalizeExecution(true)
  }

  override def stop(): Unit = {
    finalizeExecution(false)
    // runnerListener.onProjectStoped(projectContext)
  }

  override def awaitTermination(timeout: Long, unit: TimeUnit): Unit = {
    if (!latch.await(timeout, unit))
      finalizeExecution(false)
  }

  private def finalizeExecution(completed: Boolean): Unit = {
    if (running) {
      if (!completed) {
        // pollingThread.interrupt();
        // startedProcesses.filter(x => !isEntryCompleted(x._1)).map(_._2).foreach(_.stop());

        // todo
        //        startedProcesses.synchronized {
        //          startedProcesses.filter(x => !isEntryCompleted(x._1)).foreach(x => {
        //
        //            x._2.stop()
        //            val appID: String = startedProcessesAppID.getOrElse(x._1, "")
        //            if (!appID.equals("")) {
        //              println("Stop Flow " + appID + " by FlowLauncher!")
        //              FlowLauncher.stop(appID)
        //            }
        //
        //          });
        //        }
        startedGroup.synchronized {
          startedGroup.filter(x => !isEntryCompleted(x._1)).values.foreach(_.stop())
        }

        pollingThread.interrupt()

      }

      runner.removeListener(listener)
      running = false
    }
  }

  private def createContext(runnerContext: Context[DataType]): GroupContext[DataType] = {
    new CascadeContext[DataType](runnerContext) with GroupContext[DataType] {
      override def getGroup: Group[DataType] = group

      override def getGroupExecution: GroupExecution = groupExecution
    }
  }

  private def getTodos
      : (ArrayBuffer[(String, Flow[DataType])], ArrayBuffer[(String, Group[DataType])]) = {

    val todosFlow = ArrayBuffer[(String, Flow[DataType])]()
    val todosGroup = ArrayBuffer[(String, Group[DataType])]()

    mapGroupEntryWithConditions.foreach {
      en =>
        en._2._1 match {
          case flow: Flow[DataType] =>
            if (!startedProcesses.contains(en._1) && en._2._2.matches(execution)) {
              todosFlow += (en._1 -> flow)
            }
          case group1: Group[DataType] =>
            if (!startedGroup.contains(en._1) && en._2._2.matches(execution)) {
              todosGroup += (en._1 -> group1)
            }
          case _ =>
        }

    }
    (todosFlow, todosGroup)
  }

  override def getGroupId: String = id

  override def getChildCount: Int = {
    mapGroupEntryWithConditions.size
  }

}
