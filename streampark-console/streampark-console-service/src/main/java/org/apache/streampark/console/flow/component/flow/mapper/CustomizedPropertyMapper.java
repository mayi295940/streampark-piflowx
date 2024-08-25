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

package org.apache.streampark.console.flow.component.flow.mapper;

import org.apache.streampark.console.flow.component.flow.entity.CustomizedProperty;
import org.apache.streampark.console.flow.component.flow.mapper.provider.CustomizedPropertyMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.StopsCustomizedPropertyVo;

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
public interface CustomizedPropertyMapper {

    /**
     * Insert "list<CustomizedProperty>" Note that the method of spelling "sql" must use "map" to
     * connect the "Param" content to the key value.
     *
     * @param customizedPropertyList (Content: "customizedPropertyList" with a value of
     *     "List<CustomizedProperty>")
     */
    @InsertProvider(type = CustomizedPropertyMapperProvider.class, method = "addCustomizedPropertyList")
    int addCustomizedPropertyList(List<CustomizedProperty> customizedPropertyList);

    @InsertProvider(type = CustomizedPropertyMapperProvider.class, method = "addCustomizedProperty")
    int addCustomizedProperty(CustomizedProperty customizedProperty);

    @Select("select * from flow_stops_customized_property where id = #{id} and enable_flag = 1 ")
    @Results({
            @Result(column = "fk_stops_id", property = "stops", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsById", fetchType = FetchType.LAZY))
    })
    CustomizedProperty getCustomizedPropertyById(@Param("id") String id);

    @Select("select * from flow_stops_customized_property where fk_stops_id = #{stopsId} and enable_flag = 1 ")
    List<CustomizedProperty> getCustomizedPropertyListByStopsId(@Param("stopsId") String stopsId);

    @Select("select * from flow_stops_customized_property where fk_stops_id = #{stopsId} and name = #{name} and enable_flag = 1 ")
    List<CustomizedProperty> getCustomizedPropertyListByStopsIdAndName(String stopsId, String name);

    @UpdateProvider(type = CustomizedPropertyMapperProvider.class, method = "updateStopsCustomizedProperty")
    int updateStopsCustomizedProperty(CustomizedProperty customizedProperty);

    @UpdateProvider(type = CustomizedPropertyMapperProvider.class, method = "updateEnableFlagByStopId")
    int updateEnableFlagByStopId(String username, String id);

    @UpdateProvider(type = CustomizedPropertyMapperProvider.class, method = "updateCustomizedPropertyCustomValue")
    int updateCustomizedPropertyCustomValue(String username, String content, String id);

    @Select("select * from flow_stops_customized_property "
        + "where fk_stops_id = #{stopsId} "
        + "and enable_flag = 1 ")
    List<StopsCustomizedPropertyVo> getCustomizedPropertyVoListByStopsId(
                                                                         @Param("stopsId") String stopsId);
}