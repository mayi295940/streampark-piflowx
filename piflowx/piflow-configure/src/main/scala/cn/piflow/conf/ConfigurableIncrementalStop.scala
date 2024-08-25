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

package cn.piflow.conf

import cn.piflow.{Constants, IncrementalStop, JobContext}
import cn.piflow.util.{ConfigureUtil, HdfsUtil, PropertyUtil}

/** Created by xjzhu@cnic.cn on 7/15/19 */
abstract class ConfigurableIncrementalStop[DataType]
  extends ConfigurableStop[DataType]
  with IncrementalStop[DataType] {

  override var incrementalPath: String = _

  override def init(flowName: String, stopName: String): Unit = {
    incrementalPath = ConfigureUtil.getIncrementPath().stripSuffix(Constants.SINGLE_SLASH) +
      Constants.SINGLE_SLASH + flowName + Constants.SINGLE_SLASH + stopName
  }

  override def readIncrementalStart(): String = {
    if (!HdfsUtil.exists(incrementalPath))
      HdfsUtil.createFile(incrementalPath)
    val value: String = HdfsUtil.getLine(incrementalPath)
    value
  }

  override def saveIncrementalStart(value: String): Unit = {
    HdfsUtil.saveLine(incrementalPath, value)
  }

}
