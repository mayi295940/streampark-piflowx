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

import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.mapper.provider.StopsMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.StopsVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopVo;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;
import java.util.Map;

/** Stop component table */
@Mapper
public interface StopsMapper {

    /**
     * Add a single stops
     *
     * @param stops stops
     */
    @InsertProvider(type = StopsMapperProvider.class, method = "addStops")
    int addStops(Stops stops);

    /**
     * Insert list<Stops> Note that the method of spelling sql must use Map to connect Param content
     * to key value.
     *
     * @param stopsList stopsList
     */
    @InsertProvider(type = StopsMapperProvider.class, method = "addStopsList")
    int addStopsList(List<Stops> stopsList);

    /**
     * update stops
     *
     * @param stops stops
     */
    @UpdateProvider(type = StopsMapperProvider.class, method = "updateStops")
    int updateStops(Stops stops);

    /** Query all stops data */
    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsList")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "is_checkpoint", property = "isCheckpoint"),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(property = "dataSource", column = "fk_data_source_id", many = @Many(select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.getDataSourceById", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "customizedPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyListByStopsId", fetchType = FetchType.LAZY))
    })
    List<Stops> getStopsList();

    /**
     * Query StopsList based on flowId
     *
     * @param flowId flowId
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsListByFlowId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "is_checkpoint", property = "isCheckpoint"),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(property = "dataSource", column = "fk_data_source_id", many = @Many(select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.adminGetDataSourceById", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "customizedPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyListByStopsId", fetchType = FetchType.LAZY))
    })
    List<Stops> getStopsListByFlowId(String flowId);

    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsListByFlowIdAndPageIds")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(property = "dataSource", column = "fk_data_source_id", many = @Many(select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.getDataSourceById", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "customizedPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyListByStopsId", fetchType = FetchType.LAZY))
    })
    List<Stops> getStopsListByFlowIdAndPageIds(
                                               @Param("flowId") String flowId, @Param("pageIds") String[] pageIds);

    @UpdateProvider(type = StopsMapperProvider.class, method = "updateStopEnableFlagByFlowId")
    int updateStopEnableFlagByFlowId(String username, String id);

    /**
     * Query stop and attribute information based on stopsId
     *
     * @param Id Id
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsById")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "fk_flow_id", property = "flow", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowById", fetchType = FetchType.LAZY))
    })
    Stops getStopsById(String Id);

    @Select("select * from flow_stops fs where fs.fk_flow_id = #{flowId} and fs.page_id = #{stopPageId} and fs.enable_flag = 1 ")
    Stops getStopByFlowIdAndStopPageId(
                                       @Param("flowId") String flowId, @Param("stopPageId") String stopPageId);

    @UpdateProvider(type = StopsMapperProvider.class, method = "updateStopsByFlowIdAndName")
    int updateStopsByFlowIdAndName(ThirdFlowInfoStopVo stopVo);

    /**
     * Verify that stop name is duplicated
     *
     * @param flowId flowId
     * @param stopName stop name
     */
    @Select("select id from flow_stops where name = #{stopName} and fk_flow_id =#{flowId} and enable_flag = 1 ")
    String getStopByNameAndFlowId(@Param("flowId") String flowId, @Param("stopName") String stopName);

    @Select("select MAX(page_id+0) from flow_stops where enable_flag = 1 and fk_flow_id = #{flowId} ")
    Integer getMaxStopPageIdByFlowId(@Param("flowId") String flowId);

    @Select("SELECT fs.name from flow_stops fs WHERE fs.enable_flag=1 and fs.fk_flow_id = #{flowId}")
    String[] getStopNamesByFlowId(@Param("flowId") String flowId);

    @Select("select * from flow_stops "
        + "where enable_flag=1 "
        + "and fk_flow_id = #{fid} "
        + "and page_id = #{stopPageId} "
        + "order by crt_dttm asc "
        + "limit 1")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "oldProperties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getOldPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "customizedPropertyList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "fk_flow_id", property = "flow", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowById", fetchType = FetchType.LAZY)),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(column = "fk_data_source_id", property = "dataSource", many = @Many(select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.getDataSourceById", fetchType = FetchType.LAZY))
    })
    Stops getStopsByPageId(@Param("fid") String fid, @Param("stopPageId") String stopPageId);

    @Select("SELECT name FROM flow_stops WHERE enable_flag = 1 and fk_data_source_id = #{datasourceId}")
    List<String> getStopsNamesByDatasourceId(@Param("datasourceId") String datasourceId);

    /** Query all stops data by datasource id */
    @Select("SELECT * FROM flow_stops WHERE enable_flag=1 and fk_data_source_id=#{datasourceId}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "is_data_source", property = "isDataSource")
    })
    List<Stops> getStopsListByDatasourceId(@Param("datasourceId") String datasourceId);

    /** Get StopsDisabledPages List By FlowId */
    @Select("SELECT page_id FROM flow_stops "
        + "WHERE enable_flag = 1 "
        + "and is_disabled = 1 "
        + "and fk_flow_id = #{flowId}")
    List<String> getStopsDisabledPagesListByFlowId(@Param("flowId") String flowId);

    /**
     * Query stop and attribute information based on stopsId
     *
     * @param Id ID
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsById")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "propertiesVo", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyVoListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "oldPropertiesVo", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getOldPropertyVoListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "stopsCustomizedPropertyVoList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyVoListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "fk_flow_id", property = "flowVo", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowVoById", fetchType = FetchType.LAZY)),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(column = "fk_data_source_id", property = "dataSourceVo", many = @Many(select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.getDataSourceVoById", fetchType = FetchType.LAZY))
    })
    StopsVo getStopsVoById(String Id);

    @Select("SELECT fs.id, fs.name FROM flow_stops fs\n"
        + "LEFT JOIN flow f ON f.id=fs.fk_flow_id\n"
        + "WHERE fs.enable_flag=1 "
        + "AND f.is_example <> 1 "
        + "AND (fs.is_disabled=0 or fs.is_disabled is null) "
        + "AND fs.fk_flow_id = #{flowId}")
    List<Map<String, String>> getStopsIdAndNameListByFlowId(@Param("flowId") String flowId);

    /**
     * Query stop and attribute information based on stopsId
     *
     * @param Ids Ids
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getDisabledStopsNameListByIds")
    List<String> getDisabledStopsNameListByIds(List<String> Ids);

    /**
     * Query cannot published Stops name list by ids
     *
     * @param Ids Ids
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getCannotPublishedStopsNameByIds")
    List<String> getCannotPublishedStopsNameByIds(List<String> Ids);

    /**
     * Query stop and attribute information based on stopsId
     *
     * @param Ids Ids
     */
    @SelectProvider(type = StopsMapperProvider.class, method = "getStopsBindDatasourceByIds")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "is_data_source", property = "isDataSource"),
            @Result(column = "id", property = "properties", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyListByStopsId", fetchType = FetchType.LAZY)),
            @Result(column = "fk_flow_id", property = "flow", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowById", fetchType = FetchType.LAZY))
    })
    List<Stops> getStopsBindDatasourceByIds(List<String> Ids);

    /**
     * Query stop counts by flowId
     *
     * @param flowId flowId
     */
    @Select("SELECT COUNT(fs.id) AS num FROM flow_stops fs "
        + "WHERE fs.enable_flag = 1 "
        + "AND fs.fk_flow_id = #{flowId};")
    int getStopsCountsByFlowId(@Param("flowId") String flowId);
}
