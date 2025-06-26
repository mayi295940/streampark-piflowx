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

trait JobInputStream[StreamingContext, DataType, DStream] {
  def isEmpty: Boolean

  def read(): DataType

  def ports(): Seq[String]

  def read(inport: String): DataType

  def readProperties(): MMap[String, String]

  def readProperties(inport: String): MMap[String, String]
}

trait JobOutputStream[StreamingContext, DataType, DStream] {

  def makeCheckPoint(pec: JobContext[StreamingContext, DataType, DStream]): Unit;

  def loadCheckPoint(pec: JobContext[StreamingContext, DataType, DStream], path: String): Unit;

  def write(data: DataType): Unit

  def write(bundle: String, data: DataType): Unit

  protected def writeProperties(properties: MMap[String, String]): Unit

  def writeProperties(bundle: String, properties: MMap[String, String]): Unit

  protected def sendError(): Unit

  def getDataCount(): MMap[String, Long];

  def getIncrementalValue(pec: JobContext[StreamingContext, DataType, DStream], incrementalField: String): String;
}

trait StopJob[StreamingContext, DataType, DStream] {
  def jid(): String

  def getStopName: String

  def getStop: Stop[StreamingContext, DataType, DStream]
}

trait JobContext[StreamingContext, DataType, DStream] extends Context[StreamingContext, DataType, DStream] {
  def getStopJob: StopJob[StreamingContext, DataType, DStream]

  def getInputStream: JobInputStream[StreamingContext, DataType, DStream]

  def getOutputStream: JobOutputStream[StreamingContext, DataType, DStream]

  def getProcessContext: ProcessContext[StreamingContext, DataType, DStream]
}

trait Stop[StreamingContext, DataType, DStream] extends Serializable {
  def initialize(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit

  def perform(
      in: JobInputStream[StreamingContext, DataType, DStream],
      out: JobOutputStream[StreamingContext, DataType, DStream],
      pec: JobContext[StreamingContext, DataType, DStream]): Unit
}

trait StreamingStop[StreamingContext, DataType, DStream] extends Stop[StreamingContext, DataType, DStream] {
  var batchDuration: Int

  def getDStream(ssc: StreamingContext): DStream
}

trait IncrementalStop[StreamingContext, DataType, DStream] extends Stop[StreamingContext, DataType, DStream] {

  var incrementalField: String
  var incrementalStart: String
  var incrementalPath: String

  def init(flowName: String, stopName: String): Unit

  def readIncrementalStart(): String

  def saveIncrementalStart(value: String): Unit

}

trait VisualizationStop[StreamingContext, DataType, DStream] extends Stop[StreamingContext, DataType, DStream] {

  var processId: String
  var stopName: String
  var visualizationPath: String
  var visualizationType: String

  def init(stopName: String): Unit

  def getVisualizationPath(): String

}

trait GroupEntry[StreamingContext, DataType, DStream] {}

trait Flow[StreamingContext, DataType, DStream] extends GroupEntry[StreamingContext, DataType, DStream] {
  def getStopNames(): Seq[String]

  def hasCheckPoint(processName: String): Boolean

  def getStop(name: String): Stop[StreamingContext, DataType, DStream]

  def analyze(): AnalyzedFlowGraph[StreamingContext, DataType, DStream]

  def show(): Unit

  def getFlowName: String

  def setFlowName(flowName: String): Unit

  def getCheckpointParentProcessId: String

  def setCheckpointParentProcessId(checkpointParentProcessId: String): Unit

  def getRunMode: String

  def setRunMode(runMode: String): Unit

  def hasStreamingStop(): Boolean;

  def getStreamingStop(): (String, StreamingStop[StreamingContext, DataType, DStream]);

  def hasIncrementalStop(): Boolean;

  def getIncrementalStop(): (String, IncrementalStop[StreamingContext, DataType, DStream]);

  // Flow Json String API
  def setFlowJson(flowJson: String): Unit

  def getFlowJson: String

  def setUUID(uuid: String): Unit

  def getUUID: String

  def setEnvironment(env: Map[String, Any]): Unit

  def getEnvironment: Map[String, Any]
}

class FlowImpl[StreamingContext, DataType, DStream] extends Flow[StreamingContext, DataType, DStream] {

  var name = ""
  var uuid = ""

  val edges: ArrayBuffer[Edge] = ArrayBuffer[Edge]()
  val stops: MMap[String, Stop[StreamingContext, DataType, DStream]] = MMap[String, Stop[StreamingContext, DataType, DStream]]()

  private val checkpoints = ArrayBuffer[String]()
  var checkpointParentProcessId = ""
  var runMode = ""
  var flowJson = ""

  var environment: Map[String, Any] = Map[String, Any]()

  def addStop(name: String, process: Stop[StreamingContext, DataType, DStream]): FlowImpl[StreamingContext, DataType, DStream] = {
    stops(name) = process
    this
  }

  override def show(): Unit = {
    edges.foreach { arrow =>
      println(arrow.toString());
    }
  }

  def addCheckPoint(processName: String): Unit = {
    checkpoints += processName
  }

  override def hasCheckPoint(processName: String): Boolean = {
    checkpoints.contains(processName)
  }

  override def getStop(name: String): Stop[StreamingContext, DataType, DStream] = stops(name)

  override def getStopNames: Seq[String] = stops.keys.toSeq

  def addPath(path: Path): Flow[StreamingContext, DataType, DStream] = {
    edges ++= path.toEdges()
    this
  }

  override def analyze(): AnalyzedFlowGraph[StreamingContext, DataType, DStream] =
    new AnalyzedFlowGraph[StreamingContext, DataType, DStream]() {
      val incomingEdges: MMap[String, ArrayBuffer[Edge]] = MMap[String, ArrayBuffer[Edge]]()
      val outgoingEdges: MMap[String, ArrayBuffer[Edge]] = MMap[String, ArrayBuffer[Edge]]()

      edges.foreach {
        edge =>
          incomingEdges.getOrElseUpdate(edge.stopTo, ArrayBuffer[Edge]()) += edge
          outgoingEdges.getOrElseUpdate(edge.stopFrom, ArrayBuffer[Edge]()) += edge
      }

      private def _visitProcess[T](
          flow: Flow[StreamingContext, DataType, DStream],
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

      override def visit[T](flow: Flow[StreamingContext, DataType, DStream], op: (String, Map[Edge, T]) => T): Unit = {
        val ends = stops.keys.filterNot(outgoingEdges.contains)
        val visited = MMap[String, T]()
        ends.foreach {
          _visitProcess(flow, _, op, visited)
        }
      }

      override def visitStreaming[T](
          flow: Flow[StreamingContext, DataType, DStream],
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

  override def getEnvironment: Map[String, Any] = {
    this.environment
  }

  override def setEnvironment(env: Map[String, Any]): Unit = {
    this.environment = env
  }

  override def hasStreamingStop(): Boolean = {
    stops.keys.foreach { stopName =>
      {
        if (stops(stopName).isInstanceOf[StreamingStop[StreamingContext, DataType, DStream]]) {
          return true
        }
      }
    }
    false
  }

  override def getStreamingStop(): (String, StreamingStop[StreamingContext, DataType, DStream]) = {
    stops.keys.foreach { stopName =>
      {
        stops(stopName) match {
          case value: StreamingStop[StreamingContext, DataType, DStream] =>
            return (stopName, value)
          case _ =>
        }
      }
    }
    null
  }

  override def hasIncrementalStop(): Boolean = {
    stops.keys.foreach { stopName =>
      {
        if (stops(stopName).isInstanceOf[IncrementalStop[StreamingContext, DataType, DStream]]) {
          return true
        }
      }
    }
    false
  }

  override def getIncrementalStop(): (String, IncrementalStop[StreamingContext, DataType, DStream]) = {
    stops.keys.foreach { stopName =>
      {
        if (stops(stopName).isInstanceOf[StreamingStop[StreamingContext, DataType, DStream]]) {
          return (stopName, stops(stopName).asInstanceOf[IncrementalStop[StreamingContext, DataType, DStream]])
        }
      }
    }
    null
  }
}

trait AnalyzedFlowGraph[StreamingContext, DataType, DStream] {
  def visit[T](flow: Flow[StreamingContext, DataType, DStream], op: (String, Map[Edge, T]) => T): Unit

  def visitStreaming[T](
      flow: Flow[StreamingContext, DataType, DStream],
      streamingStop: String,
      streamingData: T,
      op: (String, Map[Edge, T]) => T): Unit
}

trait Process[StreamingContext, DataType, DStream] {

  def pid(): String

  def awaitTermination(): Unit

  def awaitTermination(timeout: Long, unit: TimeUnit): Unit

  def getFlow: Flow[StreamingContext, DataType, DStream]

  def fork(child: Flow[StreamingContext, DataType, DStream]): Process[StreamingContext, DataType, DStream]

  def stop(): Unit
}

trait ProcessContext[StreamingContext, DataType, DStream] extends Context[StreamingContext, DataType, DStream] {
  def getFlow: Flow[StreamingContext, DataType, DStream]

  def getProcess: Process[StreamingContext, DataType, DStream]
}

trait GroupContext[StreamingContext, DataType, DStream] extends Context[StreamingContext, DataType, DStream] {

  def getGroup: Group[StreamingContext, DataType, DStream]

  def getGroupExecution: GroupExecution

}

class JobInputStreamImpl[StreamingContext, DataType, DStream]() extends JobInputStream[StreamingContext, DataType, DStream] {

  // only returns DataFrame on calling read()
  private val inputs = MMap[String, DataType]()
  val inputsProperties: MMap[String, () => MMap[String, String]] =
    MMap[String, () => MMap[String, String]]()

  override def isEmpty: Boolean = inputs.isEmpty

  def attach(inputs: Map[Edge, JobOutputStreamImpl[StreamingContext, DataType, DStream]]): inputsProperties.type = {
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

  override def read(inPort: String): DataType = {
    inputs(inPort)
  }

  override def readProperties(): MMap[String, String] = {
    readProperties("")
  }

  override def readProperties(inPort: String): MMap[String, String] = {
    inputsProperties(inPort)()
  }
}

class JobOutputStreamImpl[StreamingContext, DataType, DStream]() extends JobOutputStream[StreamingContext, DataType, DStream] with Logging {

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

  def showData(count: Int): Unit = {
    //    mapDataFrame.foreach(en => {
    //      val portName = if (en._1.equals("")) "default" else en._1
    //      println(portName + " port: ")
    //      case en._2.getClass match {
    //        case df: org.apache.spark.sql.DataFrame =>
    //          println("DataFrame: ")
    //          df.show(count)
    //        case _ =>
    //      }
    //      //en._2.apply().show(count)
    //    })
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

  override def makeCheckPoint(pec: JobContext[StreamingContext, DataType, DStream]): Unit = ???

  override def loadCheckPoint(pec: JobContext[StreamingContext, DataType, DStream], path: String): Unit = ???

  override def getDataCount(): MMap[String, Long] = ???

  override def getIncrementalValue(pec: JobContext[StreamingContext, DataType, DStream], incrementalField: String): String = ???
}

class ProcessImpl[StreamingContext, DataType, DStream](
    flow: Flow[StreamingContext, DataType, DStream],
    runnerContext: Context[StreamingContext, DataType, DStream],
    runner: Runner[StreamingContext, DataType, DStream],
    parentProcess: Option[Process[StreamingContext, DataType, DStream]] = None)
  extends Process[StreamingContext, DataType, DStream]
  with Logging {

  val id: String = "process_" + IdGenerator.uuid + "_" + IdGenerator.nextId[Process[StreamingContext, DataType, DStream]]
  private val executionString = id + parentProcess.map("(parent=" + _.toString + ")").getOrElse("")

  runnerContext.put("processId", id)
  runnerContext.put("jobId", flow.getEnvironment.getOrElse("jobId", "").asInstanceOf[String])

  logger.debug(s"create process: $this, flow: $flow")
  flow.show()

  val process: ProcessImpl[StreamingContext, DataType, DStream] = this
  val runnerListener: RunnerListener[StreamingContext, DataType, DStream] = runner.getListener
  private val processContext = createContext(runnerContext)
  val latch = new CountDownLatch(1)
  var running = false

  private val jobs = MMap[String, StopJobImpl[StreamingContext, DataType, DStream]]()
  flow.getStopNames.foreach {
    stopName =>
      val stop = flow.getStop(stopName)
      stop.initialize(processContext)
      val pe = new StopJobImpl(stopName, stop, processContext)
      jobs(stopName) = pe
      runnerListener.onJobInitialized(pe.getContext())
  }

  private val analyzed = flow.analyze()
  val checkpointParentProcessId: String = flow.getCheckpointParentProcessId

  analyzed.visit[JobOutputStreamImpl[StreamingContext, DataType, DStream]](flow, performStopByCheckpoint)

  // perform stop use checkpoint
  def performStopByCheckpoint(stopName: String, inputs: Map[Edge, JobOutputStreamImpl[StreamingContext, DataType, DStream]]) = {
    val pe = jobs(stopName)

    var outputs: JobOutputStreamImpl[StreamingContext, DataType, DStream] = null

    try {
      runnerListener.onJobStarted(pe.getContext())

      println("Visit process " + stopName + "!!!!!!!!!!!!!")
      outputs = pe.perform(inputs)

      // show data in log
      //      val showDataCount = PropertyUtil.getPropertyValue("data.show").toInt
      //      if (showDataCount > 0) {
      //        outputs.showData(showDataCount)
      //      }

      // save data in debug mode
      //      if (flow.getRunMode() == FlowRunMode.DEBUG) {
      //        outputs.saveData(debugPath)
      //      }

      runnerListener.onJobCompleted(pe.getContext())
    } catch {
      case e: Throwable =>
        runnerListener.onJobFailed(pe.getContext())
        println("---------------performStopByCheckpoint--------------update flow state failed!!!----------------")
        runnerListener.onProcessFailed(processContext);
        throw e;
    }

    outputs
  }

  val workerThread = new Thread(new Runnable() {
    def perform() {

      // val env = processContext.get[StreamExecutionEnvironment]()
      // val env = StreamExecutionEnvironment.getExecutionEnvironment
      // println("StreamExecutionEnvironment in worderThread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

      val jobs = MMap[String, StopJobImpl[StreamingContext, DataType, DStream]]()
      flow.getStopNames().foreach { stopName =>
        val stop = flow.getStop(stopName)
        stop.initialize(processContext)

        val pe = new StopJobImpl(stopName, stop, processContext)
        jobs(stopName) = pe
        runnerListener.onJobInitialized(pe.getContext())
      }

      val analyzed = flow.analyze()
      val checkpointParentProcessId = flow.getCheckpointParentProcessId

      // TODO: change number by property configuration
      if (flow.hasStreamingStop()) {
        //        val (streamingStopName, streamingStop) = flow.getStreamingStop()
        //        val pec = jobs(streamingStopName).getContext()
        //        val spark = pec.get[SparkSession]();
        //        val ssc = new StreamingContext(spark.sparkContext, Seconds(streamingStop.batchDuration))
        //        val lines = streamingStop.getDStream(ssc)
        //        lines.foreachRDD {
        //          rdd => {
        //            //println(rdd.count())
        //            val spark = pec.get[SparkSession]()
        //            val df = rdd.toDF("value")
        //
        //            //show data in log
        //            val showDataCount = PropertyUtil.getPropertyValue("data.show").toInt
        //            if (showDataCount > 0) {
        //              df.show(showDataCount)
        //            }
        //            val streamingData = new JobOutputStreamImpl()
        //            streamingData.write(df)
        //
        //            analyzed.visitStreaming[JobOutputStreamImpl](flow, streamingStopName, streamingData, performStreamingStop)
        //          }
        //        }
        //        ssc.start()
        //        ssc.awaitTermination()
      } else {
        analyzed.visit[JobOutputStreamImpl[StreamingContext, DataType, DStream]](flow, performStopByCheckpoint)
      }

      def performStreamingStop(stopName: String, inputs: Map[Edge, JobOutputStreamImpl[StreamingContext, DataType, DStream]]) = {
        val pe = jobs(stopName);
        var outputs: JobOutputStreamImpl[StreamingContext, DataType, DStream] = null;
        try {
          runnerListener.onJobStarted(pe.getContext());
          outputs = pe.perform(inputs);
          runnerListener.onJobCompleted(pe.getContext());

          // show data in log
          val showDataCount = PropertyUtil.getPropertyValue("data.show").toInt
          if (showDataCount > 0) {
            outputs.showData(showDataCount)
          }
        } catch {
          case e: Throwable =>
            runnerListener.onJobFailed(pe.getContext());
            println("---------------performStreamingStop----update flow state failed!!!----------------")
            runnerListener.onProcessFailed(processContext);
            throw e;
        }

        outputs;
      }

      // perform stop use checkpoint
      def performStopByCheckpoint(stopName: String, inputs: Map[Edge, JobOutputStreamImpl[StreamingContext, DataType, DStream]]) = {
        val pe = jobs(stopName)

        var outputs: JobOutputStreamImpl[StreamingContext, DataType, DStream] = null
        try {
          runnerListener.onJobStarted(pe.getContext())

          println("Visit process " + stopName + "!!!!!!!!!!!!!")
          outputs = pe.perform(inputs)

          runnerListener.onJobCompleted(pe.getContext())

        } catch {
          case e: Throwable =>
            runnerListener.onJobFailed(pe.getContext())
            throw e
        }

        outputs
      }

    }

    override def run(): Unit = {
      running = true

      // onFlowStarted
      runnerListener.onProcessStarted(processContext)
      try {
        perform()
        // onFlowCompleted
        runnerListener.onProcessCompleted(processContext)
      }
      // onFlowFailed
      catch {
        case e: Throwable =>
          runnerListener.onProcessFailed(processContext)
          throw e
      } finally {
        latch.countDown()
        running = false
      }
    }
  })

  // IMPORTANT: start thread
  workerThread.start()

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

  override def getFlow: Flow[StreamingContext, DataType, DStream] = flow

  private def createContext(runnerContext: Context[StreamingContext, DataType, DStream]): ProcessContext[StreamingContext, DataType, DStream] = {

    new CascadeContext[StreamingContext, DataType, DStream](runnerContext) with ProcessContext[StreamingContext, DataType, DStream] {
      override def getFlow: Flow[StreamingContext, DataType, DStream] = flow

      override def getProcess: Process[StreamingContext, DataType, DStream] = process
    }
  }

  override def fork(child: Flow[StreamingContext, DataType, DStream]): Process[StreamingContext, DataType, DStream] = {
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

class JobContextImpl[StreamingContext, DataType, DStream](
    job: StopJob[StreamingContext, DataType, DStream],
    processContext: ProcessContext[StreamingContext, DataType, DStream])
  extends CascadeContext(processContext)
  with JobContext[StreamingContext, DataType, DStream]
  with Logging {

  val is: JobInputStreamImpl[StreamingContext, DataType, DStream] = new JobInputStreamImpl[StreamingContext, DataType, DStream]()

  val os = new JobOutputStreamImpl[StreamingContext, DataType, DStream]()

  def getStopJob: StopJob[StreamingContext, DataType, DStream] = job

  def getInputStream: JobInputStream[StreamingContext, DataType, DStream] = is

  def getOutputStream: JobOutputStream[StreamingContext, DataType, DStream] = os

  override def getProcessContext: ProcessContext[StreamingContext, DataType, DStream] = processContext
}

class StopJobImpl[StreamingContext, DataType, DStream](
    stopName: String,
    stop: Stop[StreamingContext, DataType, DStream],
    processContext: ProcessContext[StreamingContext, DataType, DStream])
  extends StopJob[StreamingContext, DataType, DStream]
  with Logging {

  val id: String = "job_" + IdGenerator.nextId[StopJob[StreamingContext, DataType, DStream]]
  val pec = new JobContextImpl(this, processContext)

  override def jid(): String = id

  def getContext(): JobContextImpl[StreamingContext, DataType, DStream] = pec

  def perform(inputs: Map[Edge, JobOutputStreamImpl[StreamingContext, DataType, DStream]]): JobOutputStreamImpl[StreamingContext, DataType, DStream] = {
    pec.getInputStream.asInstanceOf[JobInputStreamImpl[StreamingContext, DataType, DStream]].attach(inputs)
    stop.perform(pec.getInputStream, pec.getOutputStream, pec)
    pec.getOutputStream.asInstanceOf[JobOutputStreamImpl[StreamingContext, DataType, DStream]]
  }

  override def getStopName: String = stopName

  override def getStop(): Stop[StreamingContext, DataType, DStream] = stop
}

trait Context[StreamingContext, DataType, DStream] {
  def get(key: String): Any

  def get(key: String, defaultValue: Any): Any

  def get[T]()(implicit m: Manifest[T]): T = {
    get(m.runtimeClass.getName).asInstanceOf[T]
  }

  def put(key: String, value: Any): this.type

  def put[T](value: T)(implicit m: Manifest[T]): this.type =
    put(m.runtimeClass.getName, value)
}

class CascadeContext[StreamingContext, DataType, DStream](parent: Context[StreamingContext, DataType, DStream] = null)
  extends Context[StreamingContext, DataType, DStream]
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
class FlowAsStop[StreamingContext, DataType, DStream](flow: Flow[StreamingContext, DataType, DStream]) extends Stop[StreamingContext, DataType, DStream] {
  override def initialize(ctx: ProcessContext[StreamingContext, DataType, DStream]): Unit = {}

  override def perform(
      in: JobInputStream[StreamingContext, DataType, DStream],
      out: JobOutputStream[StreamingContext, DataType, DStream],
      pec: JobContext[StreamingContext, DataType, DStream]): Unit = {

    pec.getProcessContext.getProcess.fork(flow).awaitTermination()
  }
}

class ProcessNotRunningException[StreamingContext, DataType, DStream](process: Process[StreamingContext, DataType, DStream]) extends FlowException() {}

class InvalidPathException(head: Any) extends FlowException() {}
