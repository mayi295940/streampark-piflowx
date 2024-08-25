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

package org.apache.streampark.console.flow.component.process.mapper;

import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessStopMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface ProcessStopMapper {

    /**
     * add processStop
     *
     * @param processStop processStop
     */
    @InsertProvider(type = ProcessStopMapperProvider.class, method = "addProcessStop")
    int addProcessStop(ProcessStop processStop);

    /**
     * add processStopList
     *
     * @param processStopList processStopList
     */
    @InsertProvider(type = ProcessStopMapperProvider.class, method = "addProcessStopList")
    int addProcessStopList(List<ProcessStop> processStopList);

    /**
     * 根据process查询processStop
     *
     * @param processId processId
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessStopByProcessId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "processStopPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.process.mapper.ProcessStopPropertyMapper.getStopPropertyByProcessStopId", fetchType = FetchType.LAZY))
    })
    ProcessStop getProcessStopByProcessId(String processId);

    /**
     * Query based on pid and pageId
     *
     * @param processId processId
     * @param pageId pageId
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessStopByPageIdAndPageId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "processStopPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.process.mapper.ProcessStopPropertyMapper.getStopPropertyByProcessStopId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "processStopCustomizedPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.process.mapper.ProcessStopCustomizedPropertyMapper.getProcessStopCustomizedPropertyListByProcessStopsId", fetchType = FetchType.LAZY))
    })
    ProcessStop getProcessStopByPageIdAndPageId(String processId, String pageId);

    /**
     * Query based on pid and pageId
     *
     * @param processId processId
     * @param pageIds pageIds
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessStopByPageIdAndPageIds")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "processStopPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.process.mapper.ProcessStopPropertyMapper.getStopPropertyByProcessStopId", fetchType = FetchType.LAZY))
    })
    List<ProcessStop> getProcessStopByPageIdAndPageIds(String processId, String[] pageIds);

    /**
     * 根据pid和name查询
     *
     * @param processId processId
     * @param name name
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessStopByNameAndPid")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "processStopPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.process.mapper.ProcessStopPropertyMapper.getStopPropertyByProcessStopId", fetchType = FetchType.LAZY))
    })
    ProcessStop getProcessStopByNameAndPid(String processId, String name);

    /**
     * 根据id
     *
     * @param stopId stopId
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessAppIdByStopId")
    String getProcessAppIdByStopId(String stopId);

    /**
     * 根据id
     *
     * @param stopId stopId
     */
    @SelectProvider(type = ProcessStopMapperProvider.class, method = "getProcessStopNameByStopId")
    String getProcessStopNameByStopId(String stopId);

    /**
     * 修改ProcessStop
     *
     * @param processStop processStop
     */
    @UpdateProvider(type = ProcessStopMapperProvider.class, method = "updateProcessStop")
    int updateProcessStop(ProcessStop processStop);

    /**
     * logically delete
     *
     * @param processId processId
     */
    @UpdateProvider(type = ProcessStopMapperProvider.class, method = "updateEnableFlagByProcessId")
    int updateEnableFlagByProcessId(String processId, String username);
}
