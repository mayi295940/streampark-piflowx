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

package org.apache.streampark.console.flow.component.process.service;

import org.apache.streampark.console.flow.component.process.vo.DebugDataRequest;
import org.apache.streampark.console.flow.component.process.vo.DebugDataResponse;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;

public interface IProcessGroupService {

    /**
     * Query processVo based on id (query contains its child table)
     *
     * @param username
     * @param isAdmin
     * @param processGroupId ProcessGroup Id
     * @return ProcessGroupVo (query contains its child table)
     */
    public ProcessGroupVo getProcessGroupVoAllById(
                                                   String username, boolean isAdmin, String processGroupId);

    /**
     * Query processGroupVo based on id (only query process table)
     *
     * @param username
     * @param isAdmin
     * @param processGroupId ProcessGroup Id
     * @return String json string
     */
    public String getProcessGroupVoById(String username, boolean isAdmin, String processGroupId);

    /**
     * Query appInfo according to appID
     *
     * @param appID appId
     * @return ProcessGroupVo
     */
    public String getAppInfoByAppId(String appID);

    /**
     * Query appInfo according to appID
     *
     * @param appIDs AppId array
     * @return string
     */
    public String getAppInfoByAppIds(String[] appIDs);

    /**
     * Query processGroupVoList (parameter space-time non-paging)
     *
     * @param username username
     * @param isAdmin isAdmin
     * @param offset Number of pages
     * @param limit Number each page
     * @param param Search content
     * @return json
     */
    public String getProcessGroupVoListPage(
                                            String username, boolean isAdmin, Integer offset, Integer limit,
                                            String param);

    /**
     * Start processesGroup
     *
     * @param username currentUser
     * @param processGroupId Run ProcessGroup Id
     * @param checkpoint checkpoint
     * @return json
     * @throws Exception
     */
    public String startProcessGroup(
                                    boolean isAdmin, String username, String processGroupId, String checkpoint,
                                    String runMode) throws Exception;

    /**
     * Stop running processGroup
     *
     * @param processGroupId ProcessGroup Id
     * @return json
     */
    public String stopProcessGroup(String username, boolean isAdmin, String processGroupId);

    /**
     * get debug data
     *
     * @param debugDataRequest DebugDataRequest
     * @return DebugDataResponse
     */
    public DebugDataResponse getDebugData(DebugDataRequest debugDataRequest);

    /**
     * delProcessGroup
     *
     * @param processGroupID ProcessGroup Id
     * @return json
     */
    public String delProcessGroup(String username, boolean isAdmin, String processGroupID);

    /**
     * getGroupLogData
     *
     * @param processGroupAppID ProcessGroup AppId
     * @return json
     */
    public String getGroupLogData(String processGroupAppID);

    /**
     * getStartGroupJson
     *
     * @param processGroupId ProcessGroup Id
     * @return json
     */
    public String getStartGroupJson(String username, boolean isAdmin, String processGroupId);

    /**
     * getProcessIdByPageId
     *
     * @param fId Parents Id
     * @param pageId MxGraph PageId
     * @return json
     */
    public String getProcessIdByPageId(String fId, String pageId);

    /**
     * getProcessGroupIdByPageId
     *
     * @param fId Parents Id
     * @param pageId MxGraph PageId
     * @return json
     */
    public String getProcessGroupIdByPageId(String fId, String pageId);

    /**
     * getProcessGroupVoByPageId
     *
     * @param processGroupId ProcessGroup Id
     * @param pageId MxGraph PageId
     * @return json
     */
    public ProcessGroupVo getProcessGroupVoByPageId(String processGroupId, String pageId);

    /**
     * getProcessGroupPathVoByPageId
     *
     * @param processGroupId ProcessGroup Id
     * @param pageId MxGraph PageId
     * @return json
     */
    public String getProcessGroupPathVoByPageId(String processGroupId, String pageId);

    /**
     * drawingBoard Data
     *
     * @param username
     * @param isAdmin
     * @param loadId
     * @param parentAccessPath
     * @return
     */
    public String drawingBoardData(
                                   String username, boolean isAdmin, String loadId, String parentAccessPath);

    public String getProcessGroupNode(
                                      String username, boolean isAdmin, String processGroupId, String pageId);
}
