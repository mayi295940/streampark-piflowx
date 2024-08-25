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

package org.apache.streampark.console.flow.third.utils;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.StopState;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopsVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoVo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdFlowInfoVoUtils {

    public static Process setProcess(Process process, ThirdFlowInfoVo thirdFlowInfoVo) {

        if (null == thirdFlowInfoVo || null == process) {
            return process;
        }
        process.setLastUpdateUser("syncTask");
        process.setLastUpdateDttm(new Date());
        process.setProgress(thirdFlowInfoVo.getProgress());
        String thirdFlowInfoVoState = thirdFlowInfoVo.getState();
        if (StringUtils.isNotBlank(thirdFlowInfoVoState)) {
            ProcessState processState;
            switch (thirdFlowInfoVoState) {
                case "NEW":
                case "NEW_SAVING":
                    processState = ProcessState.INIT;
                    break;
                case "RUNNING":
                    processState = ProcessState.SUBMITTED;
                    break;
                case "FINISHED":
                    processState = ProcessState.COMPLETED;
                    break;
                default:
                    processState = ProcessState.selectGender(thirdFlowInfoVoState);
                    break;
            }

            process.setState(processState);
        }
        // process.setName(thirdFlowInfoVo.getName());
        // process.setProcessId(thirdFlowInfoVo.getPid());
        process.setProcessId(thirdFlowInfoVo.getId());
        process.setStartTime(DateUtils.strCstToDate(thirdFlowInfoVo.getStartTime()));
        process.setEndTime(DateUtils.strCstToDate(thirdFlowInfoVo.getEndTime()));
        Map<String, ThirdFlowInfoStopVo> stopsMap = new HashMap<>();
        List<ProcessStop> processStopList = process.getProcessStopList();
        List<ThirdFlowInfoStopsVo> stops = thirdFlowInfoVo.getStops();
        if (CollectionUtils.isEmpty(stops) || CollectionUtils.isEmpty(processStopList)) {
            return process;
        }
        for (ThirdFlowInfoStopsVo thirdFlowInfoStopsVo : stops) {
            if (null != thirdFlowInfoStopsVo) {
                ThirdFlowInfoStopVo stopVo = thirdFlowInfoStopsVo.getStop();
                if (null != stopVo) {
                    stopsMap.put(stopVo.getName(), stopVo);
                }
            }
        }
        for (ProcessStop processStop : processStopList) {
            if (null != processStop) {
                ThirdFlowInfoStopVo stopVo = stopsMap.get(processStop.getName());
                if (null != stopVo) {
                    processStop.setState(StopState.selectGender(stopVo.getState()));
                    processStop.setStartTime(DateUtils.strCstToDate(stopVo.getStartTime()));
                    processStop.setEndTime(DateUtils.strCstToDate(stopVo.getEndTime()));
                }
            }
        }
        process.setProcessStopList(processStopList);
        return process;
    }
}
