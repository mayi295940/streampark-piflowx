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

package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.common.Eunm.ScheduleState;
import org.apache.streampark.console.flow.component.system.entity.SysSchedule;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysScheduleMapperProvider;
import org.apache.streampark.console.flow.component.system.vo.SysScheduleVo;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysScheduleMapper {

    @InsertProvider(type = SysScheduleMapperProvider.class, method = "insert")
    int insert(SysSchedule sysSchedule);

    @InsertProvider(type = SysScheduleMapperProvider.class, method = "update")
    int update(SysSchedule sysSchedule);

    @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
    SysSchedule getSysScheduleById(boolean isAdmin, String id);

    /**
     * getSysScheduleListByStatus
     *
     * @param isAdmin isAdmin
     * @param status status
     */
    @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleListByStatus")
    List<SysSchedule> getSysScheduleListByStatus(boolean isAdmin, ScheduleState status);

    /**
     * getSysScheduleList
     *
     * @param param param
     */
    @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleList")
    List<SysScheduleVo> getSysScheduleList(boolean isAdmin, String param);

    @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
    SysScheduleVo getSysScheduleVoById(boolean isAdmin, String id);
}
