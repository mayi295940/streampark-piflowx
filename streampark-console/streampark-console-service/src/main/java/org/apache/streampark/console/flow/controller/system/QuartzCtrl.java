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

package org.apache.streampark.console.flow.controller.system;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.component.system.service.ISysScheduleService;
import org.apache.streampark.console.flow.component.system.vo.SysScheduleVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sysSchedule")
public class QuartzCtrl {

    private final ISysScheduleService sysScheduleServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public QuartzCtrl(
                      ISysScheduleService sysScheduleServiceImpl, ILogHelperService logHelperServiceImpl) {
        this.sysScheduleServiceImpl = sysScheduleServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping("/getScheduleListPage")
    @ResponseBody
    public String getScheduleListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return sysScheduleServiceImpl.getScheduleListPage(username, isAdmin, page, limit, param);
    }

    @RequestMapping(value = "/getScheduleById", method = RequestMethod.GET)
    @ResponseBody
    public String getScheduleById(String scheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return sysScheduleServiceImpl.getScheduleById(username, isAdmin, scheduleId);
    }

    @RequestMapping(value = "/createTask", method = RequestMethod.GET)
    @ResponseBody
    public String createTask(SysScheduleVo sysScheduleVo) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("create task", username);
        return sysScheduleServiceImpl.createJob(username, isAdmin, sysScheduleVo);
    }

    @RequestMapping(value = "/runOnceTask", method = RequestMethod.POST)
    @ResponseBody
    public String runOnceTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("run task " + sysScheduleId, username);
        return sysScheduleServiceImpl.runOnce(username, isAdmin, sysScheduleId);
    }

    @RequestMapping(value = "/startTask", method = RequestMethod.POST)
    @ResponseBody
    public String startTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("start task " + sysScheduleId, username);
        return sysScheduleServiceImpl.startJob(username, isAdmin, sysScheduleId);
    }

    @RequestMapping(value = "/stopTask", method = RequestMethod.POST)
    @ResponseBody
    public String stopTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("stop task " + sysScheduleId, username);
        return sysScheduleServiceImpl.stopJob(username, isAdmin, sysScheduleId);
    }

    @RequestMapping(value = "/pauseTask", method = RequestMethod.POST)
    @ResponseBody
    public String pauseTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("pause task " + sysScheduleId, username);
        return sysScheduleServiceImpl.pauseJob(username, isAdmin, sysScheduleId);
    }

    @RequestMapping(value = "/resumeTask", method = RequestMethod.POST)
    @ResponseBody
    public String resumeTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return sysScheduleServiceImpl.resume(username, isAdmin, sysScheduleId);
    }

    @RequestMapping(value = "/updateTask", method = RequestMethod.GET)
    @ResponseBody
    public String updateTask(SysScheduleVo sysScheduleVo) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return sysScheduleServiceImpl.update(username, isAdmin, sysScheduleVo);
    }

    @RequestMapping(value = "/deleteTask", method = RequestMethod.GET)
    @ResponseBody
    public String deleteTask(String sysScheduleId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return sysScheduleServiceImpl.deleteTask(username, isAdmin, sysScheduleId);
    }
}
