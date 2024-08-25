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

import org.apache.streampark.console.flow.component.process.entity.ProcessStopCustomizedProperty;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessStopCustomizedPropertyMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface ProcessStopCustomizedPropertyMapper {

    /**
     * Insert "list<ProcessStopCustomizedProperty>" Note that the method of spelling "sql" must use
     * "map" to connect the "Param" content to the key value.
     *
     * @param processStopCustomizedPropertyList (Content: "processStopCustomizedPropertyList" with a
     *     value of "List<ProcessStopCustomizedProperty>")
     */
    @InsertProvider(type = ProcessStopCustomizedPropertyMapperProvider.class, method = "addProcessStopCustomizedPropertyList")
    int addProcessStopCustomizedPropertyList(
                                             List<ProcessStopCustomizedProperty> processStopCustomizedPropertyList);

    @InsertProvider(type = ProcessStopCustomizedPropertyMapperProvider.class, method = "addProcessStopCustomizedProperty")
    int addProcessStopCustomizedProperty(ProcessStopCustomizedProperty processStopCustomizedProperty);

    @Select("select * from process_stops_customized_property "
        + "where id = #{id} "
        + "and enable_flag = 1 ")
    @Results({
            @Result(column = "fk_flow_process_stop_id", property = "stops", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsById", fetchType = FetchType.LAZY))
    })
    ProcessStopCustomizedProperty getProcessStopCustomizedPropertyById(@Param("id") String id);

    @Select("select * from process_stops_customized_property "
        + "where fk_flow_process_stop_id = #{processStopsId} "
        + "and enable_flag = 1 ")
    List<ProcessStopCustomizedProperty> getProcessStopCustomizedPropertyListByProcessStopsId(
                                                                                             @Param("processStopsId") String processStopsId);

    @Select("select * from process_stops_customized_property "
        + "where fk_flow_process_stop_id = #{processStopsId} "
        + "and name = #{name} "
        + "and enable_flag = 1 ")
    List<ProcessStopCustomizedProperty> getProcessStopCustomizedPropertyListByProcessStopsIdAndName(
                                                                                                    @Param("processStopsId") String processStopsId,
                                                                                                    @Param("name") String name);

    @UpdateProvider(type = ProcessStopCustomizedPropertyMapperProvider.class, method = "updateProcessStopCustomizedProperty")
    int updateProcessStopCustomizedProperty(
                                            ProcessStopCustomizedProperty processStopCustomizedProperty);

    @UpdateProvider(type = ProcessStopCustomizedPropertyMapperProvider.class, method = "updateEnableFlagByProcessStopId")
    int updateEnableFlagByProcessStopId(String username, String id);

    @UpdateProvider(type = ProcessStopCustomizedPropertyMapperProvider.class, method = "updateProcessStopCustomizedPropertyCustomValue")
    int updateProcessStopCustomizedPropertyCustomValue(String username, String content, String id);
}
