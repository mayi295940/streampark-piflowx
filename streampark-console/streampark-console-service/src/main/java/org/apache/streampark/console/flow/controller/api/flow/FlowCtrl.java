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

package org.apache.streampark.console.flow.controller.api.flow;

import org.apache.streampark.console.base.domain.RestRequest;
import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.service.IFlowService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestAdd;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestUpdate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Api(value = "flow api", tags = "flow api")
@RestController
@RequestMapping("/flow")
public class FlowCtrl {

    private final IFlowService flowServiceImpl;

    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public FlowCtrl(IFlowService flowServiceImpl, ILogHelperService logHelperServiceImpl) {
        this.flowServiceImpl = flowServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @ApiOperation(value = "getFlowListPage", notes = "get Flow list page")
    @PostMapping("getFlowListPage")
    public RestResponse getFlowListPage(RestRequest restRequest, String param) {
        boolean isAdmin = SessionUserUtil.isAdmin();
        String username = SessionUserUtil.getCurrentUsername();
        return RestResponse.success(
            flowServiceImpl.getFlowListPage(
                username, isAdmin, restRequest.getPageNum(), restRequest.getPageSize(), param));
    }

    /**
     * Enter the front page of the drawing board
     */
    @GetMapping(value = "/drawingBoardData")
    @ApiOperation(value = "drawingBoardData", notes = "drawingBoard data")
    public String drawingBoardData(String load, String parentAccessPath) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowServiceImpl.drawingBoardData(username, isAdmin, load, parentAccessPath);
    }

    @PostMapping(value = "/runFlow")
    @ApiOperation(value = "runFlow", notes = "run Flow")
    public String runFlow(String flowId, String runMode) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("run flow", username);
        return flowServiceImpl.runFlow(username, isAdmin, flowId, runMode);
    }

    @PostMapping(value = "/runFlowByPublishingId")
    @ApiOperation(value = "runFlowByPublishingId", notes = "run Flow")
    public String runFlowByPublishingId(String publishingId, String runMode) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("run flow", username);
        return flowServiceImpl.runFlowByPublishingId(username, isAdmin, publishingId, runMode);
    }

    @PostMapping(value = "/queryFlowData")
    @ApiOperation(value = "queryFlowData", notes = "query Flow data")
    public String queryFlowData(String load) {
        return flowServiceImpl.getFlowVoById(load);
    }

    @PostMapping(value = "/saveFlowInfo")
    @ApiOperation(value = "saveFlowInfo", notes = "save Flow info")
    public RestResponse saveFlowInfo(FlowInfoVoRequestAdd flowVo) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("save flow", username);
        return RestResponse.success(flowServiceImpl.addFlow(username, flowVo));
    }

    /**
     * Delete flow association information according to flowId
     */
    @DeleteMapping(value = "/deleteFlow")
    @ApiOperation(value = "deleteFlow", notes = "delete Flow")
    public RestResponse deleteFlow(@NotBlank(message = "{id}") String id) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return RestResponse.success(flowServiceImpl.deleteFLowInfo(username, isAdmin, id));
    }

    @PostMapping(value = "/updateFlowBaseInfo")
    @ApiOperation(value = "updateFlowBaseInfo", notes = "update Flow base info")
    @ApiImplicitParam(name = "fId", value = "fId")
    public String updateFlowBaseInfo(String fId, FlowInfoVoRequestUpdate flowVo) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("update flow base " + flowVo.getName(), username);
        return flowServiceImpl.updateFlowBaseInfo(username, fId, flowVo);
    }
}
