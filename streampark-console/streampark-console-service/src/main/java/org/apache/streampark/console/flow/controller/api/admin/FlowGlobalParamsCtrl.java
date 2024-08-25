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

package org.apache.streampark.console.flow.controller.api.admin;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.service.IFlowGlobalParamsService;
import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequestAdd;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "FlowGlobalParams api", tags = "FlowGlobalParams api")
@RestController
@RequestMapping("/flowGlobalParams")
public class FlowGlobalParamsCtrl {

    private final IFlowGlobalParamsService flowGlobalParamsServiceImpl;

    @Autowired
    public FlowGlobalParamsCtrl(IFlowGlobalParamsService flowGlobalParamsServiceImpl) {
        this.flowGlobalParamsServiceImpl = flowGlobalParamsServiceImpl;
    }

    /** Query and enter the GlobalParams list */
    @RequestMapping(value = "/globalParamsListPage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "globalParamsListPage", notes = "global params list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "limit", required = true, paramType = "query"),
            @ApiImplicitParam(name = "param", value = "param", paramType = "query")
    })
    public String globalParamsListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowGlobalParamsServiceImpl.getFlowGlobalParamsListPage(
            username, isAdmin, page, limit, param);
    }

    /** Query and enter the GlobalParams list */
    @RequestMapping(value = "/globalParamsList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "globalParamsList", notes = "global params list")
    @ApiImplicitParam(name = "param", value = "param", paramType = "query")
    public String globalParamsList(String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowGlobalParamsServiceImpl.getFlowGlobalParamsList(username, isAdmin, param);
    }

    /** add GlobalParams */
    @RequestMapping(value = "/addGlobalParams", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "addGlobalParams", notes = "add global params")
    public String addGlobalParams(FlowGlobalParamsVoRequestAdd globalParamsVo) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        return flowGlobalParamsServiceImpl.addFlowGlobalParams(username, globalParamsVo);
    }

    /** update GlobalParams */
    @RequestMapping(value = "/updateGlobalParams", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "updateGlobalParams", notes = "update global params")
    public String updateGlobalParams(FlowGlobalParamsVoRequest globalParamsVo) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowGlobalParamsServiceImpl.updateFlowGlobalParams(username, isAdmin, globalParamsVo);
    }

    /** get GlobalParams by id */
    @RequestMapping(value = "/getGlobalParamsById", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getGlobalParamsById", notes = "get global params by id")
    @ApiImplicitParam(name = "id", value = "id", required = true)
    public String getGlobalParamsById(String id) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowGlobalParamsServiceImpl.getFlowGlobalParamsById(username, isAdmin, id);
    }

    @RequestMapping(value = "/delGlobalParams", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delGlobalParams", notes = "delete global params by id")
    public String delGlobalParams(String id) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return flowGlobalParamsServiceImpl.deleteFlowGlobalParamsById(username, isAdmin, id);
    }
}
