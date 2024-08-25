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

package org.apache.streampark.console.flow.component.process.service.Impl;

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessPath;
import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.component.process.mapper.ProcessMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper;
import org.apache.streampark.console.flow.component.process.service.IProcessPathService;
import org.apache.streampark.console.flow.component.process.vo.ProcessPathVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProcessPathServiceImpl implements IProcessPathService {

    private final ProcessMapper processMapper;
    private final ProcessPathMapper processPathMapper;
    private final ProcessStopMapper processStopMapper;

    @Autowired
    public ProcessPathServiceImpl(
                                  ProcessMapper processMapper,
                                  ProcessPathMapper processPathMapper,
                                  ProcessStopMapper processStopMapper) {
        this.processMapper = processMapper;
        this.processPathMapper = processPathMapper;
        this.processStopMapper = processStopMapper;
    }

    /** Query processPath based on processId and pageId */
    @Override
    public String getProcessPathVoByPageId(String processId, String pageId) {
        if (StringUtils.isAnyEmpty(processId, pageId)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("Parameter passed in incorrectly");
        }
        // Find ProcessPath
        ProcessPath processPathByPageId =
            processPathMapper.getProcessPathByPageIdAndPid(processId, pageId);
        if (null == processPathByPageId) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        // get from PageId and to PageId
        String[] pageIds = new String[2];
        String pathTo = processPathByPageId.getTo();
        String pathFrom = processPathByPageId.getFrom();
        if (StringUtils.isNotBlank(pathFrom)) {
            pageIds[0] = pathFrom;
        }
        if (StringUtils.isNotBlank(pathTo)) {
            pageIds[1] = pathTo;
        }
        if (StringUtils.isBlank(processId) || null == pageIds || pageIds.length <= 0) {
            return null;
        }
        // Find from ProcessStop and to ProcessStop
        List<ProcessStop> processStopByPageIds =
            processStopMapper.getProcessStopByPageIdAndPageIds(processId, pageIds);
        if (null == processStopByPageIds || processStopByPageIds.size() == 0) {
            return null;
        }
        ProcessPathVo processPathVo = new ProcessPathVo();
        pathTo = (null == pathTo ? "" : pathTo);
        pathFrom = (null == pathTo ? "" : pathFrom);
        for (ProcessStop processStop : processStopByPageIds) {
            if (null != processStop) {
                if (pathTo.equals(processStop.getPageId())) {
                    processPathVo.setTo(processStop.getName());
                } else if (pathFrom.equals(processStop.getPageId())) {
                    processPathVo.setFrom(processStop.getName());
                }
            }
        }
        processPathVo.setInport(
            StringUtils.isNotBlank(processPathByPageId.getInport())
                ? processPathByPageId.getInport()
                : PortType.DEFAULT.getText());
        processPathVo.setOutport(
            StringUtils.isNotBlank(processPathByPageId.getOutport())
                ? processPathByPageId.getOutport()
                : PortType.DEFAULT.getText());

        // Find Process RunModeType
        RunModeType runModeType = processMapper.getProcessRunModeTypeById(processId);
        Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
        if (null != runModeType) {
            rtnMap.put("runModeType", runModeType);
        }
        return ReturnMapUtils.appendValuesToJson(rtnMap, "processPathVo", processPathVo);
    }

    /** Query processGroupPath based on processId and pageId */
    @Override
    public ProcessPathVo getProcessGroupPathVoByPageId(String processGroupId, String pageId) {
        ProcessPathVo processStopVo = null;
        ProcessPath processPathByPageIdAndProcessGroupId =
            processPathMapper.getProcessPathByPageIdAndProcessGroupId(processGroupId, pageId);
        if (null != processPathByPageIdAndProcessGroupId) {
            String[] pageIds = new String[2];
            String pathTo = processPathByPageIdAndProcessGroupId.getTo();
            String pathFrom = processPathByPageIdAndProcessGroupId.getFrom();
            if (StringUtils.isNotBlank(pathFrom)) {
                pageIds[0] = pathFrom;
            }
            if (StringUtils.isNotBlank(pathTo)) {
                pageIds[1] = pathTo;
            }
            List<Process> processByPageIds = processMapper.getProcessByPageIds(processGroupId, pageIds);
            if (null != processByPageIds && processByPageIds.size() > 0) {
                processStopVo = new ProcessPathVo();
                pathTo = (null == pathTo ? "" : pathTo);
                pathFrom = (null == pathTo ? "" : pathFrom);
                for (Process process : processByPageIds) {
                    if (null != process) {
                        if (pathTo.equals(process.getPageId())) {
                            processStopVo.setTo(process.getName());
                        } else if (pathFrom.equals(process.getPageId())) {
                            processStopVo.setFrom(process.getName());
                        }
                    }
                }
                processStopVo.setInport(
                    StringUtils.isNotBlank(processPathByPageIdAndProcessGroupId.getInport())
                        ? processPathByPageIdAndProcessGroupId.getInport()
                        : PortType.DEFAULT.getText());
                processStopVo.setOutport(
                    StringUtils.isNotBlank(processPathByPageIdAndProcessGroupId.getOutport())
                        ? processPathByPageIdAndProcessGroupId.getOutport()
                        : PortType.DEFAULT.getText());
            }
        }
        return processStopVo;
    }
}
