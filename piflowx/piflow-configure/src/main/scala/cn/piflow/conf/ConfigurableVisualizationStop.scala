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

import cn.piflow.{Constants, VisualizationStop}
import cn.piflow.util.ConfigureUtil

abstract class ConfigurableVisualizationStop[DataType]
  extends ConfigurableStop[DataType]
  with VisualizationStop[DataType] {

  override var visualizationPath: String = _
  override var processId: String = _
  override var stopName: String = _

  override def init(stopName: String): Unit = {
    this.stopName = stopName
  }

  override def getVisualizationPath(processId: String): String = {
    visualizationPath = ConfigureUtil
      .getVisualizationPath()
      .stripSuffix(
        Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + processId + Constants.SINGLE_SLASH + stopName
    visualizationPath
  }

}
