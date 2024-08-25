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

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.process.service.IProcessAndProcessGroupService;
import org.apache.streampark.console.flow.component.process.service.IProcessGroupService;
import org.apache.streampark.console.flow.component.process.service.IProcessService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "processAndProcessGroup api", tags = "processAndProcessGroup api")
@Controller
@RequestMapping("/processAndProcessGroup")
public class ProcessAndProcessGroupCtrl {

    private final IProcessAndProcessGroupService processAndProcessGroupServiceImpl;
    private final IProcessGroupService processGroupServiceImpl;
    private final IProcessService processServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public ProcessAndProcessGroupCtrl(
                                      IProcessAndProcessGroupService processAndProcessGroupServiceImpl,
                                      IProcessGroupService processGroupServiceImpl,
                                      IProcessService processServiceImpl,
                                      ILogHelperService logHelperServiceImpl) {
        this.processAndProcessGroupServiceImpl = processAndProcessGroupServiceImpl;
        this.processGroupServiceImpl = processGroupServiceImpl;
        this.processServiceImpl = processServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping(value = "/processAndProcessGroupListPage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "ProcessAndProcessGroupListPage", notes = "Process and ProcessGroup list page")
    public String processAndProcessGroupListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return processAndProcessGroupServiceImpl.getProcessAndProcessGroupListPage(
            username, isAdmin, page, limit, param);
    }

    @RequestMapping(value = "/runProcessOrProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "runProcessOrProcessGroup", notes = "Run Process or ProcessGroup")
    public String runProcessOrProcessGroup(
                                           String id, String runMode, String processType,
                                           String checkpointStr) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        if ("PROCESS".equals(processType)) {
            logHelperServiceImpl.logAuthSucceed("startProcess " + runMode, username);
            return processServiceImpl.startProcess(isAdmin, username, id, checkpointStr, runMode);
        } else {
            logHelperServiceImpl.logAuthSucceed("startProcessGroup " + runMode, username);
            return processGroupServiceImpl.startProcessGroup(
                isAdmin, username, id, checkpointStr, runMode);
        }
    }

    @RequestMapping(value = "/stopProcessOrProcessGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "stopProcessOrProcessGroup", notes = "stop Process or ProcessGroup")
    public String stopProcessOrProcessGroup(String id, String processType) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        if ("PROCESS".equals(processType)) {
            logHelperServiceImpl.logAuthSucceed("stopProcess " + id, username);
            return processServiceImpl.stopProcess(username, isAdmin, id);
        } else {
            logHelperServiceImpl.logAuthSucceed("stopProcessGroup " + id, username);
            return processGroupServiceImpl.stopProcessGroup(
                SessionUserUtil.getCurrentUsername(), SessionUserUtil.isAdmin(), id);
        }
    }

    @RequestMapping(value = "/delProcessOrProcessGroup", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "delProcessOrProcessGroup", notes = "delete Process or ProcessGroup")
    public String delProcessGroup(String id, String processType) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        if ("PROCESS".equals(processType)) {
            logHelperServiceImpl.logAuthSucceed("delProcess" + processType, username);
            return processServiceImpl.delProcess(isAdmin, username, id);
        } else {
            logHelperServiceImpl.logAuthSucceed("delProcessGroup" + processType, username);
            return processGroupServiceImpl.delProcessGroup(username, isAdmin, id);
        }
    }

    @RequestMapping(value = "/getAppInfoList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getAppInfoList", notes = "get App info list")
    public String getAppInfoList(String[] taskAppIds, String[] groupAppIds) {
        return processAndProcessGroupServiceImpl.getAppInfoList(taskAppIds, groupAppIds);
    }
}
