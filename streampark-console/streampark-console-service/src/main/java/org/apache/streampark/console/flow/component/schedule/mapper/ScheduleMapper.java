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

package org.apache.streampark.console.flow.component.schedule.mapper;

import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.component.schedule.mapper.provider.ScheduleMapperProvider;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    @InsertProvider(type = ScheduleMapperProvider.class, method = "insert")
    int insert(Schedule schedule);

    /**
     * update schedule
     *
     * @param schedule schedule
     */
    @UpdateProvider(type = ScheduleMapperProvider.class, method = "update")
    int update(Schedule schedule);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleList")
    List<ScheduleVo> getScheduleVoList(boolean isAdmin, String username, String param);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleById")
    ScheduleVo getScheduleVoById(boolean isAdmin, String username, String id);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleById")
    Schedule getScheduleById(boolean isAdmin, String username, String id);

    @DeleteProvider(type = ScheduleMapperProvider.class, method = "delScheduleById")
    int delScheduleById(boolean isAdmin, String username, String id);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleIdListByStateRunning")
    List<ScheduleVo> getScheduleIdListByStateRunning(boolean isAdmin, String username);

    @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleIdListByScheduleRunTemplateId")
    int getScheduleIdListByScheduleRunTemplateId(
                                                 boolean isAdmin, String username, String scheduleRunTemplateId);
}
