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

package org.apache.streampark.console.flow.controller.api.process;

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.service.IProcessGroupService;
import org.apache.streampark.console.flow.component.process.service.IProcessService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Api(value = "processGroup api", tags = "processGroup api")
@Controller
@RequestMapping("/processGroup")
public class ProcessGroupCtrl {

    private final IProcessGroupService processGroupServiceImpl;
    private final ILogHelperService logHelperServiceImpl;
    private final IProcessService processServiceImpl;

    @Autowired
    public ProcessGroupCtrl(
                            IProcessGroupService processGroupServiceImpl,
                            ILogHelperService logHelperServiceImpl,
                            IProcessService processServiceImpl) {
        this.processGroupServiceImpl = processGroupServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
        this.processServiceImpl = processServiceImpl;
    }

    /** Query and enter the process list */
    @RequestMapping(value = "/processGroupListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "processGroupListPage", notes = "get ProcessGroup list page")
    public String processGroupListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processGroupServiceImpl.getProcessGroupVoListPage(username, isAdmin, page, limit, param);
    }

    /** Query and enter the process list */
    @RequestMapping(value = "/processListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "processListPage", notes = "get Process list")
    public String processListPage(Integer start, Integer length, Integer draw, String extra_search) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processServiceImpl.getProcessGroupVoListPage(
            username, isAdmin, start / length + 1, length, extra_search);
    }

    /** Enter the front page of the drawing board */
    @RequestMapping(value = "/drawingBoardData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "drawingBoardData", notes = "drawingBoard data")
    public String drawingBoardData(String loadId, String parentAccessPath) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processGroupServiceImpl.drawingBoardData(
            currentUsername, isAdmin, loadId, parentAccessPath);
    }

    /** Query Process basic information */
    @RequestMapping(value = "/queryProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "queryProcessGroup", notes = "query ProcessGroup")
    public String queryProcessGroup(String processGroupId) {

        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processGroupServiceImpl.getProcessGroupVoById(username, isAdmin, processGroupId);
    }

    /** Query ProcessStop basic information */
    @RequestMapping(value = "/queryProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "queryProcess", notes = "query Process")
    public String queryProcess(String processGroupId, String pageId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processGroupServiceImpl.getProcessGroupNode(username, isAdmin, processGroupId, pageId);
    }

    /** Query ProcessPath basic information */
    @RequestMapping(value = "/queryProcessGroupPath", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "queryProcessGroupPath", notes = "query ProcessGroupPath")
    public String queryProcessGroupPath(String processGroupId, String pageId) {
        return processGroupServiceImpl.getProcessGroupPathVoByPageId(processGroupId, pageId);
    }

    @RequestMapping(value = "/runProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "runProcessGroup", notes = "run ProcessGroup")
    public String runProcessGroup(String id, String checkpointStr, String runMode) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("runProcessGroup " + runMode, username);
        return processGroupServiceImpl.startProcessGroup(isAdmin, username, id, checkpointStr, runMode);
    }

    /** Stop Process Group */
    @RequestMapping(value = "/stopProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "stopProcessGroup", notes = "stop ProcessGroup")
    public String stopProcessGroup(String processGroupId) {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("stopProcessGroup " + processGroupId, username);
        return processGroupServiceImpl.stopProcessGroup(
            username, SessionUserUtil.isAdmin(), processGroupId);
    }

    @RequestMapping(value = "/delProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delProcessGroup", notes = "delete ProcessGroup")
    public String delProcessGroup(String processGroupId) {
        logHelperServiceImpl.logAuthSucceed(
            "delProcessGroup " + processGroupId, SessionUserUtil.getCurrentUsername());
        return processGroupServiceImpl.delProcessGroup(
            SessionUserUtil.getCurrentUsername(), SessionUserUtil.isAdmin(), processGroupId);
    }

    @RequestMapping(value = "/getGroupLogData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getGroupLogData", notes = "get Group log data")
    public String getGroupLogData(String appId) {
        return processGroupServiceImpl.getGroupLogData(appId);
    }

    @RequestMapping(value = "/getStartGroupJson", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getStartGroupJson", notes = "get start Group json")
    public String getStartGroupJson(String processGroupId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processGroupServiceImpl.getStartGroupJson(username, isAdmin, processGroupId);
    }

    @RequestMapping(value = "/getAppInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getAppInfo", notes = "get Api info")
    public String getAppInfo(String appid) {
        return processGroupServiceImpl.getAppInfoByAppId(appid);
    }

    @RequestMapping(value = "/getAppInfoList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getAppInfoList", notes = "get Api info list")
    public String getAppInfoList(String[] arrayObj) {
        return processGroupServiceImpl.getAppInfoByAppIds(arrayObj);
    }

    @RequestMapping(value = "/getProcessIdByPageId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getProcessIdByPageId", notes = "get Process id by page id")
    public String getProcessIdByPageId(String processGroupId, String pageId) {

        if (StringUtils.isBlank(processGroupId) || StringUtils.isBlank(pageId)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        }
        String processId = processGroupServiceImpl.getProcessIdByPageId(processGroupId, pageId);
        String processGroupIdParents =
            processGroupServiceImpl.getProcessGroupIdByPageId(processGroupId, pageId);
        String nodeType;
        if (StringUtils.isNotBlank(processId)) {
            nodeType = "flow";
        } else if (StringUtils.isNotBlank(processGroupIdParents)) {
            nodeType = "flowGroup";
        } else {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        Map<String, Object> rtnMap = ReturnMapUtils.setSucceededCustomParam("nodeType", nodeType);
        rtnMap.put("processId", processId);
        rtnMap.put("processGroupId", processGroupIdParents);
        return ReturnMapUtils.toJson(rtnMap);
    }
}
