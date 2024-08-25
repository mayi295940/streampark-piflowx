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

import org.apache.streampark.console.flow.component.system.entity.SysSchedule;
import org.apache.streampark.console.flow.component.system.mapper.SysScheduleMapper;
import org.apache.streampark.console.flow.component.system.vo.SysScheduleVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class SysScheduleDomain {

    private final SysScheduleMapper sysScheduleMapper;

    @Autowired
    public SysScheduleDomain(SysScheduleMapper sysScheduleMapper) {
        this.sysScheduleMapper = sysScheduleMapper;
    }

    /**
     * getSysScheduleList
     *
     * @param param
     * @return
     */
    public List<SysScheduleVo> getSysScheduleList(boolean isAdmin, String param) {
        return sysScheduleMapper.getSysScheduleList(isAdmin, param);
    }

    /**
     * getSysScheduleById
     *
     * @param isAdmin
     * @param id
     * @return
     */
    public SysSchedule getSysScheduleById(boolean isAdmin, String id) {
        return sysScheduleMapper.getSysScheduleById(isAdmin, id);
    }

    /**
     * getSysScheduleVoById
     *
     * @param isAdmin
     * @param id
     * @return
     */
    public SysScheduleVo getSysScheduleVoById(boolean isAdmin, String id) {
        return sysScheduleMapper.getSysScheduleVoById(isAdmin, id);
    }

    public int insertSysSchedule(SysSchedule sysSchedule) {
        if (null == sysSchedule) {
            return 0;
        }
        return sysScheduleMapper.insert(sysSchedule);
    }

    public int updateSysSchedule(SysSchedule sysSchedule) {
        if (null == sysSchedule) {
            return 0;
        }
        return sysScheduleMapper.update(sysSchedule);
    }
}
