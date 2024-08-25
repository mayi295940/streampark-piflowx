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

import java.util.concurrent.{CountDownLatch, TimeUnit}

import scala.collection.mutable.{ArrayBuffer, Map => MMap}

trait JobInputStream[DataType] {
  def isEmpty: Boolean

  def read(): DataType

  def ports(): Seq[String]

  def read(inport: String): DataType

  def readProperties(): MMap[String, String]

  def readProperties(inport: String): MMap[String, String]
}

trait JobOutputStream[DataType] {

  def write(data: DataType): Unit

  def write(bundle: String, data: DataType): Unit

  protected def writeProperties(properties: MMap[String, String]): Unit

  def writeProperties(bundle: String, properties: MMap[String, String]): Unit

  protected def sendError(): Unit
}

trait StopJob[DataType] {
  def jid(): String

  def getStopName: String

  def getStop: Stop[DataType]
}

trait JobContext[DataType] extends Context[DataType] {
  def getStopJob: StopJob[DataType]

  def getInputStream: JobInputStream[DataType]

  def getOutputStream: JobOutputStream[DataType]

  def getProcessContext: ProcessContext[DataType]
}

trait Stop[DataType] extends Serializable {
  def initialize(ctx: ProcessContext[DataType]): Unit

  def perform(
      in: JobInputStream[DataType],
      out: JobOutputStream[DataType],
      pec: JobContext[DataType]): Unit
}

trait StreamingStop[StreamingContext, DataType, DStream] extends Stop[DataType] {
  var batchDuration: Int
  def getDStream(ssc: StreamingContext): DStream
}

trait IncrementalStop[DataType] extends Stop[DataType] {

  var incrementalField: String
  var incrementalStart: String
  var incrementalPath: String

  def init(flowName: String, stopName: String): Unit

  def readIncrementalStart(): String

  def saveIncrementalStart(value: String): Unit

}

trait VisualizationStop[DataType] extends Stop[DataType] {

  var processId: String
  var stopName: String
  var visualizationPath: String
  var visualizationType: String

  def init(stopName: String): Unit

  def getVisualizationPath(processId: String): String

}

trait GroupEntry[DataType] {}

trait Flow[DataType] extends GroupEntry[DataType] {
  def getStopNames: Seq[String]

  def hasCheckPoint(processName: String): Boolean

  def getStop(name: String): Stop[DataType]

  def analyze(): AnalyzedFlowGraph[DataType]

  def show(): Unit

  def getFlowName: String

  def setFlowName(flowName: String): Unit

  def getCheckpointParentProcessId: String

  def setCheckpointParentProcessId(checkpointParentProcessId: String): Unit

  def getRunMode: String

  def setRunMode(runMode: String): Unit

  // Flow Json String API
  def setFlowJson(flowJson: String): Unit

  def getFlowJson: String

  def setUUID(uuid: String): Unit

  def getUUID: String

  def setEnvironment(env: Map[String, Any]): Unit

  def getEnvironment: Map[String, Any]
}

class FlowImpl[DataType] extends Flow[DataType] {

  var name = ""
  var uuid = ""

  val edges: ArrayBuffer[Edge] = ArrayBuffer[Edge]()
  val stops: MMap[String, Stop[DataType]] = MMap[String, Stop[DataType]]()

  private val checkpoints = ArrayBuffer[String]()
  var checkpointParentProcessId = ""
  var runMode = ""
  var flowJson = ""

  var environment: Map[String, Any] = Map[String, Any]()

  def addStop(name: String, process: Stop[DataType]): FlowImpl[DataType] = {
    stops(name) = process
    this
  }

  def addCheckPoint(processName: String): Unit = {
    checkpoints += processName
  }

  override def hasCheckPoint(processName: String): Boolean = {
    checkpoints.contains(processName)
  }

  override def getStop(name: String): Stop[DataType] = stops(name)

  override def getStopNames: Seq[String] = stops.keys.toSeq

  def addPath(path: Path): Flow[DataType] = {
    edges ++= path.toEdges()
    this
  }

  override def analyze(): AnalyzedFlowGraph[DataType] =
    new AnalyzedFlowGraph[DataType]() {
      val incomingEdges: MMap[String, ArrayBuffer[Edge]] = MMap[String, ArrayBuffer[Edge]]()
      val outgoingEdges: MMap[String, ArrayBuffer[Edge]] = MMap[String, ArrayBuffer[Edge]]()

      edges.foreach {
        edge =>
          incomingEdges.getOrElseUpdate(edge.stopTo, ArrayBuffer[Edge]()) += edge
          outgoingEdges.getOrElseUpdate(edge.stopFrom, ArrayBuffer[Edge]()) += edge
      }

      private def _visitProcess[T](
          flow: Flow[DataType],
          processName: String,
          op: (String, Map[Edge, T]) => T,
          visited: MMap[String, T]): T = {

        if (!visited.contains(processName)) {
          // TODO: need to check whether the checkpoint's data exist!!!!
          if (flow.hasCheckPoint(processName) && !flow.getCheckpointParentProcessId.equals("")) {
            val ret = op(processName, null)
            visited(processName) = ret
            return ret
          }
          // executes dependent processes
          val inputs =
            if (incomingEdges.contains(processName)) {
              // all incoming edges
              val edges = incomingEdges(processName)
              edges.map {
                edge =>
                  edge ->
                    _visitProcess(flow, edge.stopFrom, op, visited)
              }.toMap
            } else {
              Map[Edge, T]()
            }

          val ret = op(processName, inputs)
          visited(processName) = ret
          ret
        } else {
          visited(processName)
        }
      }

      override def visit[T](flow: Flow[DataType], op: (String, Map[Edge, T]) => T): Unit = {
        val ends = stops.keys.filterNot(outgoingEdges.contains)
        val visited = MMap[String, T]()
        ends.foreach {
          _visitProcess(flow, _, op, visited)
        }
      }

      override def visitStreaming[T](
          flow: Flow[DataType],
          streamingStop: String,
          streamingData: T,
          op: (String, Map[Edge, T]) => T): Unit = {

        val visited = MMap[String, T]()
        visited(streamingStop) = streamingData

        val ends = stops.keys.filterNot(outgoingEdges.contains)
        ends.foreach {
          _visitProcess(flow, _, op, visited)
        }
      }
    }

  override def getFlowName: String = {
    this.name
  }

  override def setFlowName(flowName: String): Unit = {
    this.name = flowName
  }

  // get the processId
  override def getCheckpointParentProcessId: String = {
    this.checkpointParentProcessId
  }

  override def setCheckpointParentProcessId(checkpointParentProcessId: String): Unit = {
    this.checkpointParentProcessId = checkpointParentProcessId
  }

  override def getRunMode: String = {
    this.runMode
  }

  override def setRunMode(runMode: String): Unit = {
    this.runMode = runMode
  }

  override def setFlowJson(flowJson: String): Unit = {
    this.flowJson = flowJson
  }

  override def getFlowJson: String = {
    flowJson
  }

  override def setUUID(uuid: String): Unit = {
    this.uuid = uuid
  }

  override def getUUID: String = {
    this.uuid
  }

  override def show(): Unit = {}

  override def getEnvironment: Map[String, Any] = {
    this.environment
  }

  override def setEnvironment(env: Map[String, Any]): Unit = {
    this.environment = env
  }
}

trait AnalyzedFlowGraph[DataType] {
  def visit[T](flow: Flow[DataType], op: (String, Map[Edge, T]) => T): Unit

  def visitStreaming[T](
      flow: Flow[DataType],
      streamingStop: String,
      streamingData: T,
      op: (String, Map[Edge, T]) => T): Unit
}

trait Process[DataType] {

  def pid(): String

  def awaitTermination(): Unit

  def awaitTermination(timeout: Long, unit: TimeUnit): Unit

  def getFlow: Flow[DataType]

  def fork(child: Flow[DataType]): Process[DataType]

  def stop(): Unit
}

trait ProcessContext[DataType] extends Context[DataType] {
  def getFlow: Flow[DataType]

  def getProcess: Process[DataType]
}

trait GroupContext[DataType] extends Context[DataType] {

  def getGroup: Group[DataType]

  def getGroupExecution: GroupExecution

}

class JobInputStreamImpl[DataType]() extends JobInputStream[DataType] {

  // only returns DataFrame on calling read()
  private val inputs = MMap[String, DataType]()
  val inputsProperties: MMap[String, () => MMap[String, String]] =
    MMap[String, () => MMap[String, String]]()

  override def isEmpty: Boolean = inputs.isEmpty

  def attach(inputs: Map[Edge, JobOutputStreamImpl[DataType]]): inputsProperties.type = {
    this.inputs ++= inputs
      .filter(x => x._2.contains(x._1.outport))
      .map(x => (x._1.inport, x._2.getDataFrame(x._1.outport)))

    this.inputsProperties ++= inputs
      .filter(x => x._2.contains(x._1.outport))
      .map(x => (x._1.inport, x._2.getDataFrameProperties(x._1.outport)))
  }

  override def ports(): Seq[String] = {
    inputs.keySet.toSeq
  }

  override def read(): DataType = {
    if (inputs.isEmpty)
      throw new NoInputAvailableException()

    read(inputs.head._1)
  }

  override def read(inport: String): DataType = {
    inputs(inport)
  }

  override def readProperties(): MMap[String, String] = {
    readProperties("")
  }

  override def readProperties(inport: String): MMap[String, String] = {
    inputsProperties(inport)()
  }
}

class JobOutputStreamImpl[DataType]() extends JobOutputStream[DataType] with Logging {

  private val defaultPort = "default"

  private val mapDataFrame = MMap[String, DataType]()

  private val mapDataFrameProperties = MMap[String, () => MMap[String, String]]()

  override def write(data: DataType): Unit = write("", data)

  override def sendError(): Unit = ???

  override def write(outport: String, data: DataType): Unit = {
    mapDataFrame(outport) = data
  }

  def contains(port: String): Boolean = mapDataFrame.contains(port)

  def getDataFrame(port: String): DataType = mapDataFrame(port)

  def showDataDataType(count: Int): Unit = {

    mapDataFrame.foreach(en => {
      val portName = if (en._1.equals("")) "default" else en._1
      println(portName + " port: ")
      // en._2.asInstanceOf[DataType].print()
    })
  }

  override def writeProperties(properties: MMap[String, String]): Unit = {
    writeProperties("", properties)
  }

  override def writeProperties(outport: String, properties: MMap[String, String]): Unit = {
    mapDataFrameProperties(outport) = () => properties
  }

  def getDataFrameProperties(port: String): () => MMap[String, String] = {
    if (!mapDataFrameProperties.contains(port)) {
      mapDataFrameProperties(port) = () => MMap[String, String]()
    }
    mapDataFrameProperties(port)
  }
}

class ProcessImpl[DataType](
    flow: Flow[DataType],
    runnerContext: Context[DataType],
    runner: Runner[DataType],
    parentProcess: Option[Process[DataType]] = None)
  extends Process[DataType]
  with Logging {

  val id: String = "process_" + IdGenerator.uuid + "_" + IdGenerator.nextId[Process[DataType]]
  private val executionString =
    "" + id + parentProcess.map("(parent=" + _.toString + ")").getOrElse("")

  logger.debug(s"create process: $this, flow: $flow")
  flow.show()

  val process: ProcessImpl[DataType] = this
  val runnerListener: RunnerListener[DataType] = runner.getListener
  private val processContext = createContext(runnerContext)
  val latch = new CountDownLatch(1)
  var running = false

  // val env = StreamExecutionEnvironment.getExecutionEnvironment

  private val jobs = MMap[String, StopJobImpl[DataType]]()
  flow.getStopNames.foreach {
    stopName =>
      val stop = flow.getStop(stopName)
      stop.initialize(processContext)
      val pe = new StopJobImpl(stopName, stop, processContext)
      jobs(stopName) = pe
      // runnerListener.onJobInitialized(pe.getContext())
  }

  private val analyzed = flow.analyze()
  val checkpointParentProcessId: String = flow.getCheckpointParentProcessId

  analyzed.visit[JobOutputStreamImpl[DataType]](flow, performStopByCheckpoint)

  // perform stop use checkpoint
  private def performStopByCheckpoint(
      stopName: String,
      inputs: Map[Edge, JobOutputStreamImpl[DataType]]) = {
    val pe = jobs(stopName)

    var outputs: JobOutputStreamImpl[DataType] = null

    try {
      // runnerListener.onJobStarted(pe.getContext())

      println("Visit process " + stopName + "!!!!!!!!!!!!!")
      outputs = pe.perform(inputs)

      // outputs.showData(10)

      // runnerListener.onJobCompleted(pe.getContext())

    } catch {
      case e: Throwable =>
        // runnerListener.onJobFailed(pe.getContext())
        throw e
    }

    outputs
  }

  // env.execute(flow.getFlowName())

  /*val workerThread = new Thread(new Runnable() {
    def perform() {

      //val env = processContext.get[StreamExecutionEnvironment]()
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      println("StreamExecutionEnvironment in worderThread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

      val jobs = MMap[String, StopJobImpl]()
      flow.getStopNames().foreach { stopName =>
        val stop = flow.getStop(stopName)
        stop.initialize(processContext)

        val pe = new StopJobImpl(stopName, stop, processContext)
        jobs(stopName) = pe
        //runnerListener.onJobInitialized(pe.getContext())
      }

      val analyzed = flow.analyze()
      val checkpointParentProcessId = flow.getCheckpointParentProcessId()


      analyzed.visit[JobOutputStreamImpl](flow,performStopByCheckpoint)


      //perform stop use checkpoint
      def performStopByCheckpoint(stopName: String, inputs: Map[Edge, JobOutputStreamImpl]) = {
        val pe = jobs(stopName)

        var outputs : JobOutputStreamImpl = null
        try {
          //runnerListener.onJobStarted(pe.getContext())

          println("Visit process " + stopName + "!!!!!!!!!!!!!")
          outputs = pe.perform(inputs)

          //runnerListener.onJobCompleted(pe.getContext())

        }
        catch {
          case e: Throwable =>
            //runnerListener.onJobFailed(pe.getContext())
            throw e
        }

        outputs
      }


      //env.execute(flow.getFlowName())

    }

    override def run(): Unit = {
      running = true

      //onFlowStarted
      //runnerListener.onProcessStarted(processContext)
      try {
        perform()
        //onFlowCompleted
        //runnerListener.onProcessCompleted(processContext)
      }
      //onFlowFailed
      catch {
        case e: Throwable =>
          //runnerListener.onProcessFailed(processContext)
          throw e
      }
      finally {
        latch.countDown()
        running = false
      }
    }
  })*/

  // IMPORTANT: start thread
  // workerThread.start()

  override def toString: String = executionString

  override def awaitTermination(): Unit = {
    latch.await()
  }

  override def awaitTermination(timeout: Long, unit: TimeUnit): Unit = {
    latch.await(timeout, unit)
    if (running)
      stop()
  }

  override def pid(): String = id

  override def getFlow: Flow[DataType] = flow

  private def createContext(runnerContext: Context[DataType]): ProcessContext[DataType] = {

    new CascadeContext[DataType](runnerContext) with ProcessContext[DataType] {
      override def getFlow: Flow[DataType] = flow

      override def getProcess: Process[DataType] = process
    }
  }

  override def fork(child: Flow[DataType]): Process[DataType] = {
    // add flow process stack
    val process = new ProcessImpl(child, runnerContext, runner, Some(this))
    runnerListener.onProcessForked(processContext, process.processContext)
    process
  }

  // TODO: stopSparkJob()
  override def stop(): Unit = {
    /*if (!running)
      throw new ProcessNotRunningException(this)

    workerThread.interrupt()
    runnerListener.onProcessAborted(processContext)
    latch.countDown()*/
  }
}

class JobContextImpl[DataType](job: StopJob[DataType], processContext: ProcessContext[DataType])
  extends CascadeContext(processContext)
  with JobContext[DataType]
  with Logging {

  val is: JobInputStreamImpl[DataType] = new JobInputStreamImpl[DataType]()

  val os = new JobOutputStreamImpl[DataType]()

  def getStopJob: StopJob[DataType] = job

  def getInputStream: JobInputStream[DataType] = is

  def getOutputStream: JobOutputStream[DataType] = os

  override def getProcessContext: ProcessContext[DataType] = processContext
}

class StopJobImpl[DataType](
    stopName: String,
    stop: Stop[DataType],
    processContext: ProcessContext[DataType])
  extends StopJob[DataType]
  with Logging {

  val id: String = "job_" + IdGenerator.nextId[StopJob[DataType]]
  val pec = new JobContextImpl(this, processContext)

  override def jid(): String = id

  def getContext: JobContextImpl[DataType] = pec

  def perform(inputs: Map[Edge, JobOutputStreamImpl[DataType]]): JobOutputStreamImpl[DataType] = {
    pec.getInputStream.asInstanceOf[JobInputStreamImpl[DataType]].attach(inputs)
    stop.perform(pec.getInputStream, pec.getOutputStream, pec)
    pec.getOutputStream.asInstanceOf[JobOutputStreamImpl[DataType]]
  }

  override def getStopName: String = stopName

  override def getStop: Stop[DataType] = stop
}

trait Context[DataType] {
  def get(key: String): Any

  def get(key: String, defaultValue: Any): Any

  def get[T]()(implicit m: Manifest[T]): T = {
    get(m.runtimeClass.getName).asInstanceOf[T]
  }

  def put(key: String, value: Any): this.type

  def put[T](value: T)(implicit m: Manifest[T]): this.type =
    put(m.runtimeClass.getName, value)
}

class CascadeContext[DataType](parent: Context[DataType] = null)
  extends Context[DataType]
  with Logging {

  val map: MMap[String, Any] = MMap[String, Any]()

  override def get(key: String): Any =
    internalGet(key, () => throw new ParameterNotSetException(key))

  override def get(key: String, defaultValue: Any): Any = internalGet(
    key,
    () => {
      logger.warn(s"value of '$key' not set, using default: $defaultValue")
      defaultValue
    })

  private def internalGet(key: String, op: () => Unit): Any = {
    if (map.contains(key)) {
      map(key)
    } else {
      if (parent != null)
        parent.get(key)
      else
        op()
    }
  }

  override def put(key: String, value: Any): this.type = {
    map(key) = value
    this
  }
}

class FlowException(msg: String = null, cause: Throwable = null)
  extends RuntimeException(msg, cause) {}

class NoInputAvailableException extends FlowException() {}

class ParameterNotSetException(key: String) extends FlowException(s"parameter not set: $key") {}

//sub flow
class FlowAsStop[DataType](flow: Flow[DataType]) extends Stop[DataType] {
  override def initialize(ctx: ProcessContext[DataType]): Unit = {}

  override def perform(
      in: JobInputStream[DataType],
      out: JobOutputStream[DataType],
      pec: JobContext[DataType]): Unit = {

    pec.getProcessContext.getProcess.fork(flow).awaitTermination()
  }
}

class ProcessNotRunningException[DataType](process: Process[DataType]) extends FlowException() {}

class InvalidPathException(head: Any) extends FlowException() {}
