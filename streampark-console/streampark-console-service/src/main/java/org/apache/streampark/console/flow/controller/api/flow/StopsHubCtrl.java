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

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsHubService;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsHubInfoVo;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "stops hub api", tags = "stops hub api")
@RestController
@RequestMapping("/stops")
public class StopsHubCtrl {

    private final ILogHelperService logHelperServiceImpl;
    private final IStopsHubService stopsHubServiceImpl;

    @Autowired
    public StopsHubCtrl(
                        ILogHelperService logHelperServiceImpl, IStopsHubService stopsHubServiceImpl) {

        this.logHelperServiceImpl = logHelperServiceImpl;
        this.stopsHubServiceImpl = stopsHubServiceImpl;
    }

    /** Query and enter the process list */
    @RequestMapping(value = "/stopsHubListPage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "stopsHubListPage", notes = "stopsHub list page")
    public String stopsHubListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsHubServiceImpl.stopsHubListPage(username, isAdmin, page, limit, param);
    }

    /** Upload stopsHub jar file and save stopsHub */
    @RequestMapping(value = "/uploadStopsHubFile", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "uploadStopsHubFile", notes = "upload StopsHub file")
    public String uploadStopsHubFile(
                                     @RequestParam("file") MultipartFile file, String type, String languageVersion) {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("uploadStopsHubFile " + file.getName(), username);
        return stopsHubServiceImpl.uploadStopsHubFile(username, file, type, languageVersion);
    }

    /** Mount stopsHub */
    @RequestMapping(value = "/mountStopsHub", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "mountStopsHub", notes = "mount StopsHub")
    public String mountStopsHub(String id) {
        String username = SessionUserUtil.getCurrentUsername();
        Boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsHubServiceImpl.mountStopsHub(username, isAdmin, id);
    }

    /** unmount stopsHub */
    @RequestMapping(value = "/unmountStopsHub", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "unmountStopsHub", notes = "unmount StopsHub")
    public String unmountStopsHub(String id) {
        String username = SessionUserUtil.getCurrentUsername();
        Boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsHubServiceImpl.unmountStopsHub(username, isAdmin, id);
    }

    @RequestMapping(value = "/delStopsHub", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delStopsHub", notes = "delete StopsHub")
    public String delStopsHub(String id) {
        String username = SessionUserUtil.getCurrentUsername();
        Boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsHubServiceImpl.delStopsHub(username, isAdmin, id);
    }

    @RequestMapping(value = "/stopsHubPublishing", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "stopsHubPublishing", notes = "stopsHub publishing")
    public String stopsHubPublishing(String id) throws JsonProcessingException {
        String username = SessionUserUtil.getCurrentUsername();
        Boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsHubServiceImpl.stopsHubPublishing(username, isAdmin, id);
    }

    @RequestMapping(value = "/getStopsHubInfoByStopHubId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getStopsHubInfoByStopHubId", notes = "get stopsHubInfo")
    public String getStopsHubInfoByStopHubId(String stopsHubId) {
        if (StringUtils.isEmpty(stopsHubId)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("id"));
        } else {
            String username = SessionUserUtil.getCurrentUsername();
            Boolean isAdmin = SessionUserUtil.isAdmin();
            return stopsHubServiceImpl.getStopsHubInfoByStopHubId(username, isAdmin, stopsHubId);
        }
    }

    /** update component info when save or remove a component except scala component */
    @RequestMapping(value = "/updateComponentInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "updateComponentInfo", notes = "set python component")
    public String updateComponentInfo(
                                      StopsHubInfoVo stopsHubInfoVo,
                                      @RequestParam(name = "file", required = false) MultipartFile file) {
        if (stopsHubInfoVo == null) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        } else {
            String username = SessionUserUtil.getCurrentUsername();
            Boolean isAdmin = SessionUserUtil.isAdmin();
            return stopsHubServiceImpl.updateComponentInfo(stopsHubInfoVo, file, username, isAdmin);
        }
    }
}