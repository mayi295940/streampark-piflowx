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

import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.mapper.provider.FlowMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.FlowVo;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface FlowMapper {

    /**
     * add flow
     *
     * @param flow flow
     */
    @InsertProvider(type = FlowMapperProvider.class, method = "addFlow")
    int addFlow(Flow flow);

    /**
     * update flow
     *
     * @param flow flow
     */
    @UpdateProvider(type = FlowMapperProvider.class, method = "updateFlow")
    int updateFlow(Flow flow);

    /** Query all workflows */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowList")
    List<Flow> getFlowList();

    /**
     * Query all workflow paging queries
     *
     * @param param param
     */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowListParam")
    List<FlowVo> getFlowListParam(String username, boolean isAdmin, String param);

    /** Query all sample workflows */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowExampleList")
    List<Flow> getFlowExampleList();

    /**
     * Query workflow based on workflow Id
     *
     * @param id workflow Id
     */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowById")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "fk_flow_group_id", property = "flowGroup", one = @One(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper.getFlowGroupById", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "mxGraphModel", one = @One(select = "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "stopsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsListByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "pathsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PathsMapper.getPathsListByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "flowGlobalParamsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowGlobalParamsMapper.getFlowGlobalParamsByFlowId", fetchType = FetchType.LAZY))
    })
    Flow getFlowById(String id);

    /** Query workflow based on workflow Id */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowByPageId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "fk_flow_group_id", property = "flowGroup", one = @One(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper.getFlowGroupById", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "mxGraphModel", one = @One(select = "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "stopsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsListByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "pathsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PathsMapper.getPathsListByFlowId", fetchType = FetchType.LAZY))
    })
    Flow getFlowByPageId(String fid, String pageId);

    @UpdateProvider(type = FlowMapperProvider.class, method = "updateEnableFlagById")
    int updateEnableFlagById(String username, String id);

    /**
     * According to the flow query PageId maximum
     *
     * @param flowId flowId
     */
    @Select("select MAX(page_id+0) from flow_stops where fk_flow_id = #{flowId} and enable_flag = 1 ")
    Integer getMaxStopPageId(@Param("flowId") String flowId);

    /**
     * According to the flow query stopName
     *
     * @param flowId flowId
     */
    @Select("SELECT fs.name from flow_stops fs WHERE fs.enable_flag=1 and fs.fk_flow_id = #{flowId}")
    String[] getStopNamesByFlowId(@Param("flowId") String flowId);

    /**
     * According to the flow query PageId maximum
     *
     * @param flowGroupId flowGroupId
     */
    @Select("select MAX(page_id + 0) from flow where enable_flag = 1 and fk_flow_group_id = #{flowGroupId} ")
    Integer getMaxFlowPageIdByFlowGroupId(@Param("flowGroupId") String flowGroupId);

    @Select("SELECT f.name from flow f WHERE f.enable_flag=1 and f.fk_flow_group_id=#{flowGroupId} and f.name=#{flowName} ")
    List<String> getFlowNamesByFlowGroupId(
                                           @Param("flowGroupId") String flowGroupId,
                                           @Param("flowName") String flowName);

    @Select("select name from flow s where s.enable_flag = 1 and s.fk_flow_group_id = #{fid} and s.page_id = #{pageId}")
    String getFlowNameByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

    @Select("select s.id from flow s where s.enable_flag = 1 and s.fk_flow_group_id = #{fid} and s.page_id = #{pageId}")
    String getFlowIdByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

    @Select("select s.id from flow s where s.enable_flag=1 and s.fk_flow_group_id=#{fid} and s.name=#{flowName}")
    String getFlowIdByNameAndFlowGroupId(
                                         @Param("fid") String fid, @Param("flowName") String flowName);

    /**
     * Query flow by flowGroupId
     *
     * @param flowGroupId flowGroupId
     */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowListGroupId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "id", property = "mxGraphModel", one = @One(select = "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "stopsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsListByFlowId", fetchType = FetchType.LAZY)),
            @Result(column = "id", property = "pathsList", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.PathsMapper.getPathsListByFlowId", fetchType = FetchType.LAZY))
    })
    List<Flow> getFlowListGroupId(String flowGroupId);

    @Select("select name from ( "
        + "select f.name from flow f WHERE f.enable_flag=1 and f.fk_flow_group_id=#{flowGroupId} "
        + "UNION ALL "
        + "select fg.name from flow_group fg where fg.enable_flag and fg.fk_flow_group_id=#{flowGroupId} "
        + ") as re ")
    String[] getFlowAndGroupNamesByFlowGroupId(@Param("flowGroupId") String flowGroupId);

    /**
     * query flow name by flow name
     *
     * @param flowName flowName
     */
    @Select("SELECT name FROM flow "
        + "WHERE enable_flag=1 "
        + "AND fk_flow_group_id IS NULL "
        + "AND is_example=0 "
        + "AND name = #{flowName} ")
    String getFlowName(@Param("flowName") String flowName);

    /**
     * get globalParams ids by flow id
     *
     * @param flowId flowId
     */
    @SelectProvider(type = FlowMapperProvider.class, method = "getGlobalParamsIdsByFlowId")
    String[] getGlobalParamsIdsByFlowId(String flowId);

    /**
     * link GlobalParams
     *
     * @param globalParamsIds globalParamsIds
     */
    @InsertProvider(type = FlowMapperProvider.class, method = "linkGlobalParams")
    int linkGlobalParams(String flowId, String[] globalParamsIds);

    /** unlink GlobalParams */
    @DeleteProvider(type = FlowMapperProvider.class, method = "unlinkGlobalParams")
    int unlinkGlobalParams(String flowId, String[] globalParamsIds);

    /**
     * Query workflow based on workflow Id
     *
     * @param id workflow Id
     */
    @SelectProvider(type = FlowMapperProvider.class, method = "getFlowById")
    FlowVo getFlowVoById(String id);
}
