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
import org.apache.streampark.console.flow.component.system.service.AdminLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "system operation log api", tags = "system operation log api")
@Controller
@RequestMapping("/log")
public class AdminLogCtrl {

    private final AdminLogService adminLogServiceImpl;

    @Autowired
    public AdminLogCtrl(AdminLogService adminLogServiceImpl) {
        this.adminLogServiceImpl = adminLogServiceImpl;
    }

    @RequestMapping(value = "/getLogListPage", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getLogListPage", notes = "operation log list")
    public String getLogListPage(Integer page, Integer limit, String param) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return adminLogServiceImpl.getLogListPage(username, isAdmin, page, limit, param);
    }
}
