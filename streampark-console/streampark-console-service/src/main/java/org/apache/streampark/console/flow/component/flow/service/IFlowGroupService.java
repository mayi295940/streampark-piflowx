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

package org.apache.streampark.console.flow.component.flow.service;

import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupVo;
import org.apache.streampark.console.flow.controller.requestVo.FlowGroupInfoVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.FlowGroupInfoVoRequestUpDate;

public interface IFlowGroupService {

    /**
     * getFlowGroupById
     *
     * @param username
     * @param isAdmin
     * @param flowGroupId
     * @return
     */
    public FlowGroup getFlowGroupById(String username, boolean isAdmin, String flowGroupId);

    /**
     * getFlowGroupVoById
     *
     * @param flowGroupId
     * @return
     */
    public String getFlowGroupVoInfoById(String flowGroupId);

    /**
     * getFlowGroupVoAllById
     *
     * @param flowGroupId
     * @return
     */
    public String getFlowGroupVoAllById(String flowGroupId);

    /**
     * Paging query flow
     *
     * @param offset Number of pages
     * @param limit Number of pages per page
     * @param param search for the keyword
     * @return
     */
    public String getFlowGroupListPage(
                                       String username, boolean isAdmin, Integer offset, Integer limit, String param);

    /**
     * save or update flowGroupVo
     *
     * @param username
     * @param flowGroupVo
     * @return
     * @throws Exception
     */
    public String saveOrUpdate(String username, FlowGroupInfoVoRequest flowGroupVo) throws Exception;

    /**
     * run flow group
     *
     * @param flowGroupId
     * @param runMode
     * @return
     * @throws Exception
     */
    public String runFlowGroup(boolean isAdmin, String username, String flowGroupId, String runMode) throws Exception;

    /**
     * delete FLowGroup info
     *
     * @param id
     * @return
     */
    public String deleteFLowGroupInfo(boolean isAdmin, String username, String id);

    /**
     * Copy flow to group
     *
     * @param username
     * @param flowId
     * @param flowGroupId
     * @return
     * @throws Exception
     */
    public String copyFlowToGroup(String username, String flowId, String flowGroupId) throws Exception;

    /**
     * Query FlowGroupVo information based on pageId
     *
     * @param fid
     * @param pageId
     * @return
     */
    public FlowGroupVo getFlowGroupByPageId(String fid, String pageId);

    /**
     * updateFlowGroupNameById
     *
     * @param username
     * @param id
     * @param parentsId
     * @param flowGroupName
     * @param pageId
     * @return
     */
    public String updateFlowGroupNameById(
                                          String username, String id, String parentsId, String flowGroupName,
                                          String pageId) throws Exception;

    /**
     * updateFlowGroupNameById
     *
     * @param id
     * @param flowGroupName
     * @return
     * @throws Exception
     */
    public Boolean updateFlowGroupNameById(String username, String id, String flowGroupName) throws Exception;

    /**
     * updateFlowGroupBaseInfo
     *
     * @param username
     * @param fId
     * @param flowGroupVo
     * @return
     * @throws Exception
     */
    public String updateFlowGroupBaseInfo(
                                          String username, String fId,
                                          FlowGroupInfoVoRequestUpDate flowGroupVo) throws Exception;

    /**
     * Right click to run
     *
     * @param username
     * @param isAdmin
     * @param pId
     * @param nodeId
     * @param nodeType
     * @return
     * @throws Exception
     */
    public String rightRun(
                           String username, boolean isAdmin, String pId, String nodeId,
                           String nodeType) throws Exception;

    /**
     * Query FlowGroupVo or FlowVo information based on pageId
     *
     * @param fid
     * @param pageId
     * @return
     */
    public String queryIdInfo(String fid, String pageId);

    /**
     * drawingBoard Data
     *
     * @param username
     * @param isAdmin
     * @param load
     * @param parentAccessPath
     * @return
     */
    public String drawingBoardData(
                                   String username, boolean isAdmin, String load, String parentAccessPath);
}
