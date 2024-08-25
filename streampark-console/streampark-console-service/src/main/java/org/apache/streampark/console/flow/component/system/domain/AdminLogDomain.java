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

package org.apache.streampark.console.flow.component.system.domain;

import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.mapper.AdminLogMapper;
import org.apache.streampark.console.flow.component.system.vo.SysLogVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class AdminLogDomain {

    private final AdminLogMapper adminLogMapper;

    @Autowired
    public AdminLogDomain(AdminLogMapper adminLogMapper) {
        this.adminLogMapper = adminLogMapper;
    }

    public List<SysLogVo> getLogList(boolean isAdmin, String username, String param) {
        return adminLogMapper.getLogList(isAdmin, username, param);
    }

    public int insertSelective(SysLog record) {
        return adminLogMapper.insertSelective(record);
    }
}
