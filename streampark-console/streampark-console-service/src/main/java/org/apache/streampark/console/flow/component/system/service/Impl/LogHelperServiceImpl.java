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

package org.apache.streampark.console.flow.component.system.service.Impl;

import org.apache.streampark.console.flow.base.utils.IpUtil;
import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.service.AdminLogService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

@Component
public class LogHelperServiceImpl implements ILogHelperService {

    public static final Integer LOG_TYPE_AUTH = 1;

    private final AdminLogService logService;

    @Autowired
    public LogHelperServiceImpl(AdminLogService logService) {
        this.logService = logService;
    }

    @Override
    public void logAuthSucceed(String action, String result) {
        logAdmin(LOG_TYPE_AUTH, action, true, result, "");
    }

    @Override
    public void logAdmin(
                         Integer type, String action, Boolean succeed, String result, String comment) {
        SysLog log = new SysLog();
        log.setUsername(result);
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.setLastLoginIp(IpUtil.getIpAddr(request));
        log.setAction(action);
        log.setStatus(succeed);
        log.setResult(result);
        log.setComment(comment);
        log.setLastUpdateDttm(new Date());
        logService.add(log);
    }
}
