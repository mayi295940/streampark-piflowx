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

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.system.service.ISysInitRecordsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/bootPage")
public class BootPageCtrl {

    private final ISysInitRecordsService sysInitRecordsServiceImpl;

    @Autowired
    public BootPageCtrl(ISysInitRecordsService sysInitRecordsServiceImpl) {
        this.sysInitRecordsServiceImpl = sysInitRecordsServiceImpl;
    }

    @RequestMapping(value = "/isInBootPage", method = RequestMethod.GET)
    @ResponseBody
    public String isInBootPage() {
        boolean inBootPage = sysInitRecordsServiceImpl.isInBootPage();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("isIn", inBootPage);
    }

    @RequestMapping(value = "/initComponents", method = RequestMethod.GET)
    @ResponseBody
    public String initComponents() {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        return sysInitRecordsServiceImpl.initComponents(currentUsername);
    }

    @RequestMapping(value = "/threadMonitoring", method = RequestMethod.GET)
    @ResponseBody
    public String threadMonitoring() {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        return sysInitRecordsServiceImpl.threadMonitoring(currentUsername);
    }
}
