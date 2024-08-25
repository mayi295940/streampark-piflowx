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

package org.apache.streampark.console.flow.third.service;

import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdProgressVo;

import java.util.Map;

public interface IFlow {

    Map<String, Object> startFlow(Process process, String checkpoint, RunModeType runModeType);

    String getProcessJson(Process process, String checkpoint, RunModeType runModeType);

    String stopFlow(String appId);

    ThirdProgressVo getFlowProgress(String appId);

    String getFlowLog(String appId);

    String getCheckpoints(String appID);

    String getDebugData(String appID, String stopName, String portName);

    String getVisualizationData(String appID, String stopName, String visualizationType);

    ThirdFlowInfoVo getFlowInfo(String appid);

    void getProcessInfoAndSave(String appid) throws Exception;

    void processInfoAndSaveSync() throws Exception;

    String getTestDataPathUrl();
}
