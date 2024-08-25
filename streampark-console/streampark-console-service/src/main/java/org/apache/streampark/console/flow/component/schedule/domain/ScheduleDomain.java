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

package org.apache.streampark.console.flow.component.schedule.domain;

import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.component.schedule.mapper.ScheduleMapper;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class ScheduleDomain {

    private final ScheduleMapper scheduleMapper;

    @Autowired
    public ScheduleDomain(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    public int insert(Schedule schedule) {
        return scheduleMapper.insert(schedule);
    }

    public int update(Schedule schedule) {
        return scheduleMapper.update(schedule);
    }

    public List<ScheduleVo> getScheduleVoList(boolean isAdmin, String username, String param) {
        return scheduleMapper.getScheduleVoList(isAdmin, username, param);
    }

    public ScheduleVo getScheduleVoById(boolean isAdmin, String username, String id) {
        return scheduleMapper.getScheduleVoById(isAdmin, username, id);
    }

    public Schedule getScheduleById(boolean isAdmin, String username, String id) {
        return scheduleMapper.getScheduleById(isAdmin, username, id);
    }

    public int delScheduleById(boolean isAdmin, String username, String id) {
        return scheduleMapper.delScheduleById(isAdmin, username, id);
    }

    public List<ScheduleVo> getScheduleIdListByStateRunning(boolean isAdmin, String username) {
        return scheduleMapper.getScheduleIdListByStateRunning(isAdmin, username);
    }

    public int getScheduleIdListByScheduleRunTemplateId(
                                                        boolean isAdmin, String username,
                                                        String scheduleRunTemplateId) {
        return scheduleMapper.getScheduleIdListByScheduleRunTemplateId(
            isAdmin, username, scheduleRunTemplateId);
    }
}
