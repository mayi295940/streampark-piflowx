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
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.third.vo.flowGroup.ThirdFlowGroupInfoResponse;

import java.util.List;
import java.util.Map;

public interface IGroup {

    /**
     * startFlowGroup
     *
     * @param processGroup
     * @return
     */
    public Map<String, Object> startFlowGroup(ProcessGroup processGroup, RunModeType runModeType);

    /**
     * stopFlowGroup
     *
     * @param processGroupId
     * @return
     */
    public String stopFlowGroup(String processGroupId);

    /**
     * getFlowGroupInfoStr
     *
     * @param groupId
     * @return
     */
    public String getFlowGroupInfoStr(String groupId);

    /**
     * getFlowGroupInfo
     *
     * @param groupId
     * @return
     */
    public ThirdFlowGroupInfoResponse getFlowGroupInfo(String groupId);

    /**
     * getFlowGroupProgress
     *
     * @param groupId
     * @return
     */
    public Double getFlowGroupProgress(String groupId);

    /**
     * update FlowGroup By Interface
     *
     * @param groupId
     * @throws Exception
     */
    public void updateFlowGroupByInterface(String groupId) throws Exception;

    /**
     * update FlowGroups By Interface
     *
     * @param groupIds
     * @throws Exception
     */
    public void updateFlowGroupsByInterface(List<String> groupIds) throws Exception;
}
