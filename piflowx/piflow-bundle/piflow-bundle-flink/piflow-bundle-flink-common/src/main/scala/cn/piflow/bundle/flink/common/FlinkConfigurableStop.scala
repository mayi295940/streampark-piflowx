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

package cn.piflow.bundle.flink.common

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import java.io.{PrintWriter, StringWriter}

trait FlinkConfigurableStop {

  def generateSql(tmpTable: String = ""): String = {
    ""
  }

  def verify(): String = {
    var env: StreamExecutionEnvironment = null
    try {
      val sql = generateSql()
      env = StreamExecutionEnvironment.getExecutionEnvironment
      val tableEnv = StreamTableEnvironment.create(env)
      tableEnv.executeSql(sql)
      "success"
    } catch {
      case e: Exception =>
        getFullStackTrace(e)
    } finally {
      try {
        if (env != null) {
          env.close()
        }
      } catch {
        case e: Exception =>
      }
    }
  }

  private def getFullStackTrace(e: Throwable): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
