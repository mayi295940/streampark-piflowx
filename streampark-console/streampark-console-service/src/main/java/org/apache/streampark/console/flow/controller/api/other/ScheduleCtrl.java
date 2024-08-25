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

package org.apache.streampark.console.flow.controller.api.other;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.schedule.service.IScheduleService;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "schedule api", tags = "schedule api")
@RestController
@RequestMapping("/schedule")
public class ScheduleCtrl {

    private final IScheduleService scheduleServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public ScheduleCtrl(
                        IScheduleService scheduleServiceImpl, ILogHelperService logHelperServiceImpl) {
        this.scheduleServiceImpl = scheduleServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    /**
     * Query and enter the scheduleVo list
     *
     * @param page page number
     * @param limit page size
     * @param param search param
     * @return json
     */
    @RequestMapping(value = "/getScheduleVoListPage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getScheduleVoListPage", notes = "get ScheduleVo list")
    public String getScheduleVoListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return scheduleServiceImpl.getScheduleVoListPage(isAdmin, username, page, limit, param);
    }

    @RequestMapping(value = "/addSchedule", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "addSchedule", notes = "add Schedule")
    public String addSchedule(ScheduleVo scheduleVo) {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed(
            "addSchedule " + scheduleVo.getScheduleRunTemplateName(), username);
        return scheduleServiceImpl.addSchedule(username, scheduleVo);
    }

    @RequestMapping(value = "/getScheduleById", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getScheduleById", notes = "get Schedule by id")
    public String getScheduleById(String scheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return scheduleServiceImpl.getScheduleVoById(isAdmin, username, scheduleId);
    }

    @RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "updateSchedule", notes = "update Schedule")
    public String updateSchedule(ScheduleVo scheduleVo) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed(
            "updateSchedule " + scheduleVo.getScheduleRunTemplateName(), username);
        return scheduleServiceImpl.updateSchedule(isAdmin, username, scheduleVo);
    }

    @RequestMapping(value = "/delSchedule", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delSchedule", notes = "delete Schedule")
    public String delSchedule(String scheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("updateSchedule " + scheduleId, username);
        return scheduleServiceImpl.delSchedule(isAdmin, username, scheduleId);
    }

    @RequestMapping(value = "/startSchedule", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "startSchedule", notes = "start Schedule")
    public String startSchedule(String scheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return scheduleServiceImpl.startSchedule(isAdmin, username, scheduleId);
    }

    @RequestMapping(value = "/stopSchedule", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "stopSchedule", notes = "stop Schedule")
    public String stopSchedule(String scheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return scheduleServiceImpl.stopSchedule(isAdmin, username, scheduleId);
    }
}
