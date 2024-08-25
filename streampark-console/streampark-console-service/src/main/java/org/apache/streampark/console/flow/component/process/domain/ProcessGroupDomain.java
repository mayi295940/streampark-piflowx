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

package org.apache.streampark.console.flow.component.process.domain;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxGraphModelDomain;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroupPath;
import org.apache.streampark.console.flow.component.process.mapper.ProcessAndProcessGroupMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper;
import org.apache.streampark.console.flow.component.process.vo.ProcessAndProcessGroupVo;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class ProcessGroupDomain {

    private final Logger logger = LoggerUtil.getLogger();

    private final ProcessAndProcessGroupMapper processAndProcessGroupMapper;
    private final ProcessGroupPathMapper processGroupPathMapper;
    private final ProcessGroupMapper processGroupMapper;
    private final MxGraphModelDomain mxGraphModelDomain;
    private final ProcessDomain processDomain;

    @Autowired
    public ProcessGroupDomain(
                              ProcessAndProcessGroupMapper processAndProcessGroupMapper,
                              ProcessGroupPathMapper processGroupPathMapper,
                              ProcessGroupMapper processGroupMapper,
                              MxGraphModelDomain mxGraphModelDomain,
                              ProcessDomain processDomain) {
        this.processAndProcessGroupMapper = processAndProcessGroupMapper;
        this.processGroupPathMapper = processGroupPathMapper;
        this.processGroupMapper = processGroupMapper;
        this.mxGraphModelDomain = mxGraphModelDomain;
        this.processDomain = processDomain;
    }

    /**
     * Add process of things
     *
     * @param processGroup processGroup
     * @return affected rows
     */
    public int addProcessGroup(ProcessGroup processGroup) throws Exception {
        if (null == processGroup) {
            throw new Exception("save failed, processGroup is null");
        }
        String id = processGroup.getId();
        if (StringUtils.isBlank(id)) {
            processGroup.setId(UUIDUtils.getUUID32());
        }
        int affectedRows = processGroupMapper.addProcessGroup(processGroup);
        if (affectedRows <= 0) {
            throw new Exception("save failed");
        }
        // save path
        List<ProcessGroupPath> processGroupPathList = processGroup.getProcessGroupPathList();
        if (null != processGroupPathList && processGroupPathList.size() > 0) {
            for (ProcessGroupPath processGroupPath : processGroupPathList) {
                if (null == processGroupPath) {
                    continue;
                }
                processGroupPath.setProcessGroup(processGroup);
                affectedRows += addProcessGroupPath(processGroupPath);
            }
        }
        // Save Process
        List<Process> processList = processGroup.getProcessList();
        if (null != processList && processList.size() > 0) {
            for (Process process : processList) {
                process.setProcessGroup(processGroup);
                affectedRows += processDomain.addProcess(process);
            }
        }
        // Save Process
        List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
        if (null != processGroupList && processGroupList.size() > 0) {
            for (ProcessGroup processGroup_i : processGroupList) {
                processGroup_i.setProcessGroup(processGroup);
                affectedRows += this.addProcessGroup(processGroup_i);
            }
        }
        MxGraphModel mxGraphModel = processGroup.getMxGraphModel();
        if (null != mxGraphModel) {
            affectedRows += mxGraphModelDomain.addMxGraphModel(mxGraphModel);
        }
        return affectedRows;
    }

    public int addProcessGroupPath(ProcessGroupPath processGroupPath) throws Exception {
        if (null == processGroupPath) {
            throw new Exception("save failed, processGroupPath is null");
        }
        int affectedRows = processGroupPathMapper.addProcessGroupPath(processGroupPath);
        if (affectedRows <= 0) {
            throw new Exception("save failed");
        }
        return affectedRows;
    }

    /**
     * update processGroup of things
     *
     * @param processGroup processGroup
     * @return affected rows
     */
    public int updateProcessGroup(ProcessGroup processGroup) throws Exception {
        if (null == processGroup) {
            return 0;
        }

        int affectedRows = processGroupMapper.updateProcessGroup(processGroup);
        if (affectedRows <= 0) {
            throw new Exception("save failed");
        }
        // save path
        List<ProcessGroupPath> processGroupPathList = processGroup.getProcessGroupPathList();
        if (null != processGroupPathList && processGroupPathList.size() > 0) {
            for (ProcessGroupPath processGroupPath : processGroupPathList) {
                if (null == processGroupPath) {
                    continue;
                }
                processGroupPath.setProcessGroup(processGroup);
                if (StringUtils.isBlank(processGroupPath.getId())) {
                    affectedRows += addProcessGroupPath(processGroupPath);
                } else {
                    affectedRows += updateProcessGroupPath(processGroupPath);
                }
            }
        }
        // Save Process
        List<Process> processList = processGroup.getProcessList();
        if (null != processList && processList.size() > 0) {
            for (Process process : processList) {
                if (null == process) {
                    continue;
                }
                process.setProcessGroup(processGroup);
                if (StringUtils.isBlank(process.getId())) {
                    affectedRows += processDomain.addProcess(process);
                } else {
                    affectedRows += processDomain.updateProcess(process);
                }
            }
        }
        // Save Process
        List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
        if (null != processGroupList && processGroupList.size() > 0) {
            for (ProcessGroup processGroup_i : processGroupList) {
                if (null == processGroup_i) {
                    continue;
                }
                processGroup_i.setProcessGroup(processGroup);
                if (StringUtils.isBlank(processGroup_i.getId())) {
                    affectedRows += this.addProcessGroup(processGroup_i);
                } else {
                    affectedRows += this.updateProcessGroup(processGroup_i);
                }
            }
        }
        MxGraphModel mxGraphModel = processGroup.getMxGraphModel();
        if (null != mxGraphModel) {
            mxGraphModel.setProcessGroup(processGroup);
            if (StringUtils.isBlank(mxGraphModel.getId())) {
                affectedRows += mxGraphModelDomain.addMxGraphModel(mxGraphModel);
            } else {
                affectedRows += mxGraphModelDomain.updateMxGraphModel(mxGraphModel);
            }
        }
        return affectedRows;
    }

    /**
     * update processGroupPath of things
     *
     * @param processGroupPath processGroupPath
     * @return affected rows
     */
    public int updateProcessGroupPath(ProcessGroupPath processGroupPath) {
        if (null == processGroupPath) {
            return 0;
        }
        return processGroupPathMapper.updateProcessGroupPath(processGroupPath);
    }

    public List<String> getProcessGroupIdByAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            logger.warn("process id is null");
            return null;
        }
        return processGroupMapper.getProcessGroupIdByAppId(appId);
    }

    public ProcessGroup getProcessGroupById(String username, boolean isAdmin, String processGroupId) {
        if (StringUtils.isBlank(processGroupId) || StringUtils.isBlank(username)) {
            logger.warn("process id is null");
            return null;
        }
        return processGroupMapper.getProcessGroupByIdAndUser(username, isAdmin, processGroupId);
    }

    public ProcessGroupPath getProcessGroupPathByPageId(String fid, String pageId) {
        return processGroupPathMapper.getProcessGroupPathByPageId(fid, pageId);
    }

    public ProcessGroup getProcessGroupByAppId(String appId) {
        return processGroupMapper.getProcessGroupByAppId(appId);
    }

    public int saveOrUpdateSyncTask(ProcessGroup processGroup) throws Exception {
        processGroup.setLastUpdateUser("syncTask");
        return this.updateProcessGroup(processGroup);
    }

    public List<Map<String, Object>> getProcessGroupNamesAndPageIdsByPageIds(
                                                                             String fid, List<String> pageIds) {
        return processGroupMapper.getProcessGroupNamesAndPageIdsByPageIds(fid, pageIds);
    }

    public String getProcessGroupIdByPageId(String fid, String pageId) {
        return processGroupMapper.getProcessGroupIdByPageId(fid, pageId);
    }

    public List<ProcessGroup> getProcessGroupListByAppIDs(String[] appIDs) {
        return processGroupMapper.getProcessGroupListByAppIDs(appIDs);
    }

    public ProcessGroup getProcessGroupByIdAndUser(String username, boolean isAdmin, String id) {
        return processGroupMapper.getProcessGroupByIdAndUser(username, isAdmin, id);
    }

    public int updateEnableFlagById(String id, String username) {
        return processGroupMapper.updateEnableFlagById(id, username);
    }

    /** Paging query */
    public List<ProcessGroupVo> getProcessGroupListPageByParam(
                                                               String username, boolean isAdmin, String param) {
        return processGroupMapper.getProcessGroupListByParam(username, isAdmin, param);
    }

    public ProcessGroup getProcessGroupByPageId(String fid, String pageId) {
        return processGroupMapper.getProcessGroupByPageId(fid, pageId);
    }

    public List<Process> getProcessListByAppIDs(String[] appIDs) {
        return processDomain.getProcessListByAppIDs(appIDs);
    }

    public List<ProcessAndProcessGroupVo> getProcessAndProcessGroupList(String param) {
        return processAndProcessGroupMapper.getProcessAndProcessGroupList(param);
    }

    public List<ProcessAndProcessGroupVo> getProcessAndProcessGroupListByUser(
                                                                              String param, String username) {
        return processAndProcessGroupMapper.getProcessAndProcessGroupListByUser(param, username);
    }
}
