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

package org.apache.streampark.console.flow.component.process.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.ProcessParentType;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxGraphModelUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroupPath;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupPathVo;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;
import org.apache.streampark.console.flow.component.process.vo.ProcessVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessGroupUtils {

    public static ProcessGroup processGroupNewNoId(String username) {

        ProcessGroup processGroup = new ProcessGroup();
        // basic properties (required when creating)
        processGroup.setCrtDttm(new Date());
        processGroup.setCrtUser(username);
        // basic properties
        processGroup.setEnableFlag(true);
        processGroup.setLastUpdateUser(username);
        processGroup.setLastUpdateDttm(new Date());
        processGroup.setVersion(0L);
        return processGroup;
    }

    public static ProcessGroup initProcessGroupBasicPropertiesNoId(
                                                                   ProcessGroup processGroup, String username) {
        if (null == processGroup) {
            return processGroupNewNoId(username);
        }
        // basic properties (required when creating)
        processGroup.setId(null);
        processGroup.setCrtDttm(new Date());
        processGroup.setCrtUser(username);
        // basic properties
        processGroup.setEnableFlag(true);
        processGroup.setLastUpdateUser(username);
        processGroup.setLastUpdateDttm(new Date());
        processGroup.setVersion(0L);
        return processGroup;
    }

    public static ProcessGroup flowGroupToProcessGroup(
                                                       FlowGroup flowGroup, String username, RunModeType runModeType,
                                                       boolean isAddId) {
        if (null == flowGroup) {
            return null;
        }
        ProcessGroup processGroupNew = new ProcessGroup();
        // copy FlowGroup to ProcessGroup
        BeanUtils.copyProperties(flowGroup, processGroupNew);
        processGroupNew = initProcessGroupBasicPropertiesNoId(processGroupNew, username);
        if (isAddId) {
            processGroupNew.setId(UUIDUtils.getUUID32());
        }

        // Take out the sketchpad information of 'flowGroup'
        MxGraphModel flowGroupMxGraphModel = flowGroup.getMxGraphModel();
        MxGraphModel mxGraphModelProcessGroup =
            MxGraphModelUtils.copyMxGraphModelAndNewNoIdAndUnlink(
                username, flowGroupMxGraphModel, isAddId, null);
        mxGraphModelProcessGroup =
            MxGraphModelUtils.initMxGraphModelBasicPropertiesNoId(
                mxGraphModelProcessGroup, username, isAddId);
        // add link
        mxGraphModelProcessGroup.setProcessGroup(processGroupNew);
        processGroupNew.setMxGraphModel(mxGraphModelProcessGroup);
        processGroupNew.setFlowId(flowGroup.getId());
        processGroupNew.setId(UUIDUtils.getUUID32());
        processGroupNew.setRunModeType(runModeType);
        processGroupNew.setProcessParentType(ProcessParentType.GROUP);

        // Get the paths information of flow
        List<FlowGroupPaths> flowGroupPathsList = flowGroup.getFlowGroupPathsList();
        // isEmpty
        if (null != flowGroupPathsList && flowGroupPathsList.size() > 0) {
            List<ProcessGroupPath> processGroupPathList = new ArrayList<>();
            // Loop paths information
            for (FlowGroupPaths flowGroupPaths : flowGroupPathsList) {
                // isEmpty
                if (null != flowGroupPaths) {
                    ProcessGroupPath processGroupPath = new ProcessGroupPath();
                    // Copy flowGroupPaths information into processGroupPath
                    BeanUtils.copyProperties(flowGroupPaths, processGroupPath);
                    // Set basic information
                    processGroupPath.setId(null);
                    processGroupPath.setCrtDttm(new Date());
                    processGroupPath.setCrtUser(username);
                    processGroupPath.setLastUpdateDttm(new Date());
                    processGroupPath.setLastUpdateUser(username);
                    processGroupPath.setEnableFlag(true);
                    // Associated foreign key
                    processGroupPath.setProcessGroup(processGroupNew);
                    processGroupPathList.add(processGroupPath);
                }
            }
            processGroupNew.setProcessGroupPathList(processGroupPathList);
        }

        // flow to remove flowGroup
        List<Flow> flowList = flowGroup.getFlowList();
        // flowList isEmpty
        if (null != flowList && flowList.size() > 0) {
            // List of stop of process
            List<Process> processList = new ArrayList<>();
            // Loop flowList
            for (Flow flow : flowList) {
                // isEmpty
                if (null == flow) {
                    continue;
                }
                Process processNew = ProcessUtils.flowToProcess(flow, username, isAddId);
                if (null == processNew) {
                    continue;
                }
                processNew.setProcessGroup(processGroupNew);
                processList.add(processNew);
            }
            processGroupNew.setProcessList(processList);
        }

        List<FlowGroup> flowGroupList = flowGroup.getFlowGroupList();
        if (null != flowGroupList && flowGroupList.size() > 0) {
            // List of stop of process
            List<ProcessGroup> processGroupList = new ArrayList<>();
            // Loop flowGroupList
            for (FlowGroup flowGroupList_i : flowGroupList) {
                ProcessGroup processGroupChildNew =
                    flowGroupToProcessGroup(flowGroupList_i, username, runModeType, isAddId);
                processGroupChildNew.setProcessGroup(processGroupNew);
                processGroupList.add(processGroupChildNew);
            }
            processGroupNew.setProcessGroupList(processGroupList);
        }

        return processGroupNew;
    }

    public static List<ProcessGroup> copyProcessGroupList(
                                                          List<ProcessGroup> processGroupList,
                                                          ProcessGroup processGroup,
                                                          String username,
                                                          RunModeType runModeType,
                                                          boolean isAddId) {
        List<ProcessGroup> copyProcessGroupList = null;
        if (null != processGroupList && processGroupList.size() > 0) {
            copyProcessGroupList = new ArrayList<>();
            for (ProcessGroup processGroup_new : processGroupList) {
                ProcessGroup copyProcessGroup =
                    copyProcessGroup(processGroup_new, username, runModeType, isAddId);
                if (null != copyProcessGroup) {
                    copyProcessGroup.setProcessGroup(processGroup);
                    copyProcessGroupList.add(copyProcessGroup);
                }
            }
        }
        return copyProcessGroupList;
    }

    public static ProcessGroup copyProcessGroup(
                                                ProcessGroup processGroup, String username, RunModeType runModeType,
                                                boolean isAddId) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        if (null == processGroup) {
            return null;
        }
        ProcessGroup copyProcessGroup = new ProcessGroup();
        BeanUtils.copyProperties(processGroup, copyProcessGroup);
        copyProcessGroup =
            ProcessGroupUtils.initProcessGroupBasicPropertiesNoId(copyProcessGroup, username);
        if (isAddId) {
            copyProcessGroup.setId(UUIDUtils.getUUID32());
        } else {
            copyProcessGroup.setId(null);
        }
        copyProcessGroup.setParentProcessId(
            StringUtils.isNotBlank(processGroup.getParentProcessId())
                ? processGroup.getParentProcessId()
                : processGroup.getProcessId());
        copyProcessGroup.setState(ProcessState.INIT);
        copyProcessGroup.setRunModeType(null != runModeType ? runModeType : RunModeType.RUN);
        copyProcessGroup.setProcessParentType(ProcessParentType.GROUP);
        copyProcessGroup.setStartTime(null);
        copyProcessGroup.setEndTime(null);
        copyProcessGroup.setProgress("0.00");
        copyProcessGroup.setAppId(null);

        // copyMxGraphModel remove Id
        MxGraphModel copyMxGraphModel = copyProcessGroup.getMxGraphModel();
        if (null != copyMxGraphModel) {
            copyMxGraphModel =
                MxGraphModelUtils.copyMxGraphModelAndNewNoIdAndUnlink(
                    username, copyMxGraphModel, isAddId, null);
            copyMxGraphModel =
                MxGraphModelUtils.initMxGraphModelBasicPropertiesNoId(
                    copyMxGraphModel, username, isAddId);
            // add link
            copyMxGraphModel.setProcessGroup(copyProcessGroup);
            copyProcessGroup.setMxGraphModel(copyMxGraphModel);
        }

        // processGroupPathList
        List<ProcessGroupPath> processGroupPathList = processGroup.getProcessGroupPathList();
        copyProcessGroup.setProcessGroupPathList(
            copyProcessGroupPathList(processGroupPathList, copyProcessGroup, username, isAddId));
        // processList
        List<Process> processList = processGroup.getProcessList();
        List<Process> copyProcessList =
            ProcessUtils.copyProcessList(processList, username, runModeType, copyProcessGroup, isAddId);
        copyProcessGroup.setProcessList(copyProcessList);
        // processGroupList
        List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
        List<ProcessGroup> copyProcessGroupList =
            copyProcessGroupList(processGroupList, copyProcessGroup, username, runModeType, isAddId);
        copyProcessGroup.setProcessGroupList(copyProcessGroupList);
        return copyProcessGroup;
    }

    public static List<ProcessGroupPath> copyProcessGroupPathList(
                                                                  List<ProcessGroupPath> processGroupPathList,
                                                                  ProcessGroup copyProcessGroup,
                                                                  String username,
                                                                  boolean isAddId) {
        List<ProcessGroupPath> copyProcessGroupPathList = null;
        if (null != processGroupPathList && processGroupPathList.size() > 0) {
            copyProcessGroupPathList = new ArrayList<>();
            for (ProcessGroupPath processGroupPath : processGroupPathList) {
                ProcessGroupPath copyProcessGroupPath =
                    copyProcessGroupPath(processGroupPath, username, isAddId);
                if (null != copyProcessGroupPath) {
                    copyProcessGroupPath.setProcessGroup(copyProcessGroup);
                    copyProcessGroupPathList.add(copyProcessGroupPath);
                }
            }
            // copyProcessGroup.setProcessGroupPathList(copyProcessGroupPathList);
        }
        return copyProcessGroupPathList;
    }

    public static ProcessGroupPath copyProcessGroupPath(
                                                        ProcessGroupPath processGroupPath, String username,
                                                        boolean isAddId) {
        if (null == processGroupPath) {
            return null;
        }
        ProcessGroupPath copyProcessGroupPath = new ProcessGroupPath();
        if (isAddId) {
            copyProcessGroupPath.setId(UUIDUtils.getUUID32());
        } else {
            copyProcessGroupPath.setId(null);
        }
        copyProcessGroupPath.setCrtDttm(new Date());
        copyProcessGroupPath.setCrtUser(username);
        copyProcessGroupPath.setLastUpdateDttm(new Date());
        copyProcessGroupPath.setLastUpdateUser(username);
        copyProcessGroupPath.setEnableFlag(true);
        copyProcessGroupPath.setFrom(processGroupPath.getFrom());
        copyProcessGroupPath.setTo(processGroupPath.getTo());
        copyProcessGroupPath.setInport(processGroupPath.getInport());
        copyProcessGroupPath.setOutport(processGroupPath.getOutport());
        copyProcessGroupPath.setPageId(processGroupPath.getPageId());
        return copyProcessGroupPath;
    }

    public static ProcessGroupVo processGroupPoToVo(ProcessGroup processGroup) {
        if (null == processGroup) {
            return null;
        }
        ProcessGroupVo processGroupVo = new ProcessGroupVo();

        BeanUtils.copyProperties(processGroup, processGroupVo);
        processGroupVo.setProgress(
            StringUtils.isNotBlank(processGroup.getProgress()) ? processGroup.getProgress() : "0.00");

        // Parents ProcessGroup Copy
        ProcessGroup parentsProcessGroup = processGroup.getProcessGroup();
        if (null != parentsProcessGroup) {
            ProcessGroupVo parentsProcessGroupVo = new ProcessGroupVo();
            BeanUtils.copyProperties(parentsProcessGroup, parentsProcessGroupVo);
            processGroupVo.setProcessGroupVo(parentsProcessGroupVo);
        }

        // MxGraphModel Copy
        MxGraphModel mxGraphModel = processGroup.getMxGraphModel();
        MxGraphModelVo mxGraphModelVo = MxGraphModelUtils.mxGraphModelPoToVo(mxGraphModel);
        processGroupVo.setMxGraphModelVo(mxGraphModelVo);

        // Process List Copy
        List<Process> processList = processGroup.getProcessList();
        if (null != processList && processList.size() > 0) {
            List<ProcessVo> processVoList = new ArrayList<>();
            for (Process process : processList) {
                ProcessVo processVo = ProcessUtils.processPoToVo(process);
                if (null == processVo) {
                    continue;
                }
                processVo.setState(process.getState());
                processVoList.add(processVo);
            }
            processGroupVo.setProcessVoList(processVoList);
        }

        // ProcessGroupPath List Copy
        List<ProcessGroupPath> processGroupPathList = processGroup.getProcessGroupPathList();
        if (null != processGroupPathList && processGroupPathList.size() > 0) {
            List<ProcessGroupPathVo> processGroupPathVoList = new ArrayList<>();
            ProcessGroupPathVo processGroupPathVo;
            for (ProcessGroupPath processGroupPath : processGroupPathList) {
                if (null == processGroupPath) {
                    continue;
                }
                processGroupPathVo = new ProcessGroupPathVo();
                BeanUtils.copyProperties(processGroupPath, processGroupPathVo);
                processGroupPathVoList.add(processGroupPathVo);
            }
            processGroupVo.setProcessGroupPathVoList(processGroupPathVoList);
        }

        // ProcessGroup List Copy
        List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
        if (null != processGroupList && processGroupList.size() > 0) {
            List<ProcessGroupVo> processGroupVoList = new ArrayList<>();
            ProcessGroupVo copy_ProcessGroupVo_I;
            for (ProcessGroup processGroup_i : processGroupList) {
                copy_ProcessGroupVo_I = processGroupPoToVo(processGroup_i);
                if (null == copy_ProcessGroupVo_I) {
                    continue;
                }
                processGroupVoList.add(copy_ProcessGroupVo_I);
            }
            processGroupVo.setProcessGroupVoList(processGroupVoList);
        }
        return processGroupVo;
    }

    public static ProcessGroupVo processGroupBasePoToVo(ProcessGroup processGroup) {
        if (null == processGroup) {
            return null;
        }
        ProcessGroupVo processGroupVo = new ProcessGroupVo();

        BeanUtils.copyProperties(processGroup, processGroupVo);
        processGroupVo.setProgress(
            StringUtils.isNotBlank(processGroup.getProgress()) ? processGroup.getProgress() : "0.00");

        // Process List Copy
        List<Process> processList = processGroup.getProcessList();
        if (null != processList && processList.size() > 0) {
            List<ProcessVo> processVoList = new ArrayList<>();
            ProcessVo processVo;
            for (Process process : processList) {
                if (null == process) {
                    continue;
                }
                processVo = new ProcessVo();
                processVo.setState(process.getState());
                processVo.setStartTime(process.getStartTime());
                processVo.setEndTime(process.getEndTime());
                processVo.setPageId(process.getPageId());
                processVoList.add(processVo);
            }
            processGroupVo.setProcessVoList(processVoList);
        }

        // ProcessGroup List Copy
        List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
        if (null != processGroupList && processGroupList.size() > 0) {
            List<ProcessGroupVo> processGroupVoList = new ArrayList<>();
            ProcessGroupVo processGroupVo_I;
            for (ProcessGroup processGroup_i : processGroupList) {
                if (null == processGroup_i) {
                    continue;
                }
                processGroupVo_I = new ProcessGroupVo();
                processGroupVo_I.setState(processGroup_i.getState());
                processGroupVo_I.setStartTime(processGroup_i.getStartTime());
                processGroupVo_I.setEndTime(processGroup_i.getEndTime());
                processGroupVo_I.setPageId(processGroup_i.getPageId());
                processGroupVoList.add(processGroupVo_I);
            }
            processGroupVo.setProcessGroupVoList(processGroupVoList);
        }
        return processGroupVo;
    }
}
