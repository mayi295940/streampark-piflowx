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

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.service.IProcessPathService;
import org.apache.streampark.console.flow.component.process.service.IProcessService;
import org.apache.streampark.console.flow.component.process.service.IProcessStopService;
import org.apache.streampark.console.flow.component.process.vo.DebugDataRequest;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Api(value = "process api", tags = "process api")
@Controller
@RequestMapping("/process")
public class ProcessCtrl {

    private final IProcessStopService processStopServiceImpl;
    private final IProcessPathService processPathServiceImpl;
    private final IProcessService processServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public ProcessCtrl(
                       IProcessStopService processStopServiceImpl,
                       IProcessPathService processPathServiceImpl,
                       IProcessService processServiceImpl,
                       ILogHelperService logHelperServiceImpl) {
        this.processStopServiceImpl = processStopServiceImpl;
        this.processPathServiceImpl = processPathServiceImpl;
        this.processServiceImpl = processServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping(value = "/processListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "processListPage", notes = "Process list page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "limit", required = true, paramType = "query"),
            @ApiImplicitParam(name = "param", value = "param", paramType = "query")
    })
    public String processAndProcessGroupListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processServiceImpl.getProcessVoListPage(username, isAdmin, page, limit, param);
    }

    /** Enter the front page of the drawing board */
    @RequestMapping(value = "/drawingBoardData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "drawingBoardData", notes = "drawingBoard data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loadId", value = "loadId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "parentAccessPath", value = "parentAccessPath", paramType = "query")
    })
    public String drawingBoardData(String loadId, String parentAccessPath) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processServiceImpl.drawingBoardData(currentUsername, isAdmin, loadId, parentAccessPath);
    }

    /** Query Process basic information */
    @RequestMapping(value = "/queryProcessData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "queryProcessData", notes = "query Process data")
    @ApiImplicitParam(name = "processId", value = "processId", required = true, paramType = "query")
    public String queryProcessData(String processId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processServiceImpl.getProcessVoById(username, isAdmin, processId);
    }

    /** Query ProcessStop basic information */
    @RequestMapping(value = "/queryProcessStopData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "queryProcessStopData", notes = "query ProcessStop data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "processId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageId", value = "pageId", required = true, paramType = "query")
    })
    public String queryProcessStopData(String processId, String pageId) {
        return processStopServiceImpl.getProcessStopVoByPageId(processId, pageId);
    }

    /** Query ProcessPath basic information */
    @RequestMapping(value = "/queryProcessPathData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "queryProcessPathData", notes = "query ProcessPath data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "processId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageId", value = "pageId", required = true, paramType = "query")
    })
    public String queryProcessPathData(String processId, String pageId) {
        return processPathServiceImpl.getProcessPathVoByPageId(processId, pageId);
    }

    @RequestMapping(value = "/runProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "runProcess", notes = "Run Process")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "checkpointStr", value = "checkpointStr", paramType = "query"),
            @ApiImplicitParam(name = "runMode", value = "runMode", paramType = "query")
    })
    public String runProcess(String id, String checkpointStr, String runMode) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("runProcess" + id, username);
        return processServiceImpl.startProcess(isAdmin, username, id, checkpointStr, runMode);
    }

    @RequestMapping(value = "/stopProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "stopProcess", notes = "stop Process")
    @ApiImplicitParam(name = "processId", value = "processId", required = true, paramType = "query")
    public String stopProcess(String processId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("stopProcess" + processId, username);
        return processServiceImpl.stopProcess(username, isAdmin, processId);
    }

    @RequestMapping(value = "/delProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delProcess", notes = "delete Process")
    @ApiImplicitParam(name = "processID", value = "processID", required = true, paramType = "query")
    public String delProcess(String processID) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("delProcess" + processID, username);
        return processServiceImpl.delProcess(isAdmin, username, processID);
    }

    /** Get the address of the log of the flow */
    @RequestMapping(value = "/getLogUrl", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getLogUrl", notes = "get log data")
    @ApiImplicitParam(name = "appId", value = "appId", required = true, paramType = "query")
    public String getLogUrl(String appId) {
        return processServiceImpl.getLogUrl(appId);
    }

    /** Climb to the log by the address of the flow log */
    @RequestMapping(value = "/getLog", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getLog", notes = "get log data")
    @ApiImplicitParam(name = "url", value = "url", required = true, paramType = "query")
    public String getLog(String url) {
        if (StringUtils.isBlank(url)) {
            return "urlStr is null";
        }
        if (MessageConfig.INTERFACE_CALL_ERROR_MSG().equals(url)) {
            return MessageConfig.INTERFACE_CALL_ERROR_MSG();
        }
        return HttpUtils.getHtml(url);
    }

    /** Monitoring query app info */
    @RequestMapping(value = "/getAppInfo", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getAppInfo", notes = "get app info")
    @ApiImplicitParam(name = "appid", value = "appid", required = true, paramType = "query")
    public String getAppInfo(String appid) {
        return processServiceImpl.getAppInfoByAppId(appid);
    }

    /** Monitoring query app info List */
    @RequestMapping(value = "/getAppInfoList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getAppInfoList", notes = "get app info list")
    @ApiImplicitParam(name = "arrayObj", value = "arrayObj", required = true, paramType = "query", allowMultiple = true)
    public String getAppInfoList(String[] arrayObj) {
        return processServiceImpl.getProgressByAppIds(arrayObj);
    }

    /** Call the interface to return Checkpoint for the user to choose */
    @RequestMapping(value = "/getCheckpointData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getCheckpointData", notes = "get checkpoint data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pID", value = "pID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "parentProcessId", value = "parentProcessId", required = true, paramType = "query")
    })
    public String getCheckpoint(String pID, String parentProcessId) {
        return processServiceImpl.getCheckpoints(parentProcessId, pID);
    }

    @RequestMapping(value = "/getDebugData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDebugData", notes = "get debug data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "appId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stopName", value = "stopName", required = true, paramType = "query"),
            @ApiImplicitParam(name = "portName", value = "portName", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startFileName", value = "startFileName", paramType = "query"),
            @ApiImplicitParam(name = "startLine", value = "startLine", example = "0", dataType = "long", required = true, paramType = "query")
    })
    public String getDebugData(
                               String appId, String stopName, String portName, String startFileName, int startLine) {
        if ("Default".equals(portName)) {
            portName = portName.toLowerCase();
        }
        return processServiceImpl.getDebugData(
            new DebugDataRequest(appId, stopName, portName, startFileName, startLine));
    }

    @RequestMapping(value = "/getVisualizationData", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getVisualizationData", notes = "get visualization data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "appId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stopName", value = "stopName", required = true, paramType = "query"),
            @ApiImplicitParam(name = "visualizationType", value = "visualizationType", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isSoft", value = "isSoft", required = true, example = "false", paramType = "query")
    })
    public String getVisualizationData(
                                       String appId, String stopName, String visualizationType, boolean isSoft) {
        return processServiceImpl.getVisualizationData(appId, stopName, visualizationType, isSoft);
    }

    /**
     * Get the `list' running under `flow'
     *
     * @param flowId flowId
     */
    @RequestMapping(value = "/getRunningProcessList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getRunningProcessList", notes = "get running Process list")
    @ApiImplicitParam(name = "flowId", value = "flowId", required = true, paramType = "query")
    public String getRunningProcessList(String flowId) {
        return processServiceImpl.getRunningProcessVoList(flowId);
    }

    @RequestMapping(value = "/showViewStopData/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "showViewStopData", notes = "show view Stop data")
    public void showViewData(HttpServletResponse response, @PathVariable String id) throws Exception {
        processStopServiceImpl.showViewData(response, id);
    }
}
