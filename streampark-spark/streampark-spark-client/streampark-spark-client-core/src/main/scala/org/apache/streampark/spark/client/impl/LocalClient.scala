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

package org.apache.streampark.spark.client.impl

import org.apache.streampark.common.util.HttpClientUtils
import org.apache.streampark.common.util.Implicits._
import org.apache.streampark.spark.client.`trait`.SparkClientTrait
import org.apache.streampark.spark.client.bean._

import org.apache.commons.lang3.StringUtils
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

import java.util.concurrent.{ConcurrentHashMap, CountDownLatch}

import scala.util.{Failure, Success, Try}

/** Local mode submit */
object LocalClient extends SparkClientTrait {

  private val persistMetricsUrl = "http://127.0.0.1:10000/spark/app/persistMetrics"

  private lazy val sparkHandles = new ConcurrentHashMap[String, SparkAppHandle]()

  override def doCancel(cancelRequest: CancelRequest): CancelResponse = {
    val sparkAppHandle = sparkHandles.remove(cancelRequest.appId)
    if (sparkAppHandle != null) {
      Try(sparkAppHandle.stop()) match {
        case Success(_) =>
          logger.info(s"[StreamPark][Spark][LocalClient] spark job: ${cancelRequest.appId} is stopped successfully.")
          CancelResponse(null)
        case Failure(e) =>
          logger.error(s"[StreamPark][Spark][LocalClient] sparkAppHandle kill failed for appId: ${cancelRequest.appId}", e)
          CancelResponse(null)
      }
    } else {
      logger.warn(s"[StreamPark][Spark][LocalClient] spark job: ${cancelRequest.appId} is not existed.")
      CancelResponse(null)
    }
  }

  override def setConfig(submitRequest: SubmitRequest): Unit = {}

  override def doSubmit(submitRequest: SubmitRequest): SubmitResponse = {
    // 1) prepare sparkLauncher
    val launcher: SparkLauncher = prepareSparkLauncher(submitRequest)

    // 2) set spark config
    setSparkConfig(submitRequest, launcher)

    // 3) launch
    Try(launch(launcher, submitRequest)) match {
      case Success(handle: SparkAppHandle) =>
        if (handle.getError.isPresent) {
          logger.info(s"[StreamPark][Spark][LocalClient] spark job: ${submitRequest.appName} submit failed.")
          throw handle.getError.get()
        } else {
          logger.info(s"[StreamPark][Spark][LocalClient] spark job: ${submitRequest.appName} submit successfully, " +
            s"appid: ${handle.getAppId}, " +
            s"state: ${handle.getState}")
          sparkHandles += handle.getAppId -> handle
          SubmitResponse(handle.getAppId, null, submitRequest.appProperties)
        }
      case Failure(e) => throw e
    }
  }

  private def launch(sparkLauncher: SparkLauncher, submitRequest: SubmitRequest): SparkAppHandle = {
    logger.info("[StreamPark][Spark][LocalClient] The spark job start submitting")
    val submitFinished: CountDownLatch = new CountDownLatch(1)
    val sparkAppHandle = sparkLauncher.startApplication(new SparkAppHandle.Listener() {
      override def infoChanged(sparkAppHandle: SparkAppHandle): Unit = {}

      override def stateChanged(handle: SparkAppHandle): Unit = {

        if (handle.getAppId != null) {
          logger.info(s"${handle.getAppId} stateChanged : ${handle.getState.toString}")
        } else {
          logger.info("stateChanged : {}", handle.getState.toString)
        }
        if (handle.getAppId != null && submitFinished.getCount != 0) {
          // Task submission succeeded
          submitFinished.countDown()
        }
        if (handle.getState.isFinal) {
          if (StringUtils.isNotBlank(handle.getAppId) && sparkHandles.containsKey(handle.getAppId)) {
            sparkHandles.remove(handle.getAppId)
          }
          if (submitFinished.getCount != 0) {
            // Task submission failed
            submitFinished.countDown()
          }
          logger.info("Task is end, final state : {}", handle.getState.toString)
        }

        val params = new java.util.HashMap[String, AnyRef]()
        params.put("id", submitRequest.id.toString)
        params.put("appStatus", handle.getState.toString)
        val result: String = HttpClientUtils.httpPostRequest(persistMetricsUrl, params)
        logger.info(s"spark job: ${submitRequest.appName} persistMetrics result: ${result}")
      }
    })
    submitFinished.await()
    sparkAppHandle
  }

  private def prepareSparkLauncher(submitRequest: SubmitRequest) = {
    val env = new java.util.HashMap[String, String]()
    new SparkLauncher(env)
      .setSparkHome(submitRequest.sparkVersion.sparkHome)
      .setAppResource(submitRequest.userJarPath)
      .setMainClass(submitRequest.appMain)
      .setAppName(submitRequest.appName)
      .setMaster("local[*]") // Local mode with all available cores
      .setVerbose(true)
  }

  private def setSparkConfig(submitRequest: SubmitRequest, sparkLauncher: SparkLauncher): Unit = {
    logger.info("[StreamPark][Spark][LocalClient] set spark configuration.")
    // Set spark conf
    submitRequest.appProperties.foreach(prop => {
      val k = prop._1
      val v = prop._2
      logger.info(s"| $k  : $v")
      sparkLauncher.setConf(k, v)
    })

    // Set spark args
    submitRequest.appArgs.foreach(sparkLauncher.addAppArgs(_))
  }

}
