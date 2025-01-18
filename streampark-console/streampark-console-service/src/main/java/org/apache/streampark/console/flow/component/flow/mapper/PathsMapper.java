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

import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.mapper.provider.PathsMapperProvider;

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
public interface PathsMapper {

    /**
     * Insert paths
     *
     * @param paths paths
     */
    @InsertProvider(type = PathsMapperProvider.class, method = "addPaths")
    int addPaths(Paths paths);

    /**
     * Insert "list<Paths>" Note that the method of spelling SQL must use "map" to connect the "Param"
     * content to the key value.
     *
     * @param pathsList pathsList
     */
    @InsertProvider(type = PathsMapperProvider.class, method = "addPathsList")
    int addPathsList(List<Paths> pathsList);

    /**
     * update paths
     *
     * @param paths paths
     */
    @UpdateProvider(type = PathsMapperProvider.class, method = "updatePaths")
    int updatePaths(Paths paths);

    /**
     * Query according to "flowId"
     *
     * @param flowId flowId
     */
    @SelectProvider(type = PathsMapperProvider.class, method = "getPathsListByFlowId")
    @Results({
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_to", property = "to"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport")
    })
    List<Paths> getPathsListByFlowId(String flowId);

    /**
     * Query connection information
     *
     * @param flowId flow Id
     * @param pageId path pageID
     * @param from path from
     * @param to path to
     */
    @SelectProvider(type = PathsMapperProvider.class, method = "getPaths")
    @Results({
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_to", property = "to"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport"),
            @Result(column = "line_port", property = "port"),
            @Result(column = "fk_flow_id", property = "flow", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowById", fetchType = FetchType.LAZY))
    })
    List<Paths> getPaths(String flowId, String pageId, String from, String to);

    /**
     * Query connection information
     *
     * @param flowId flow Id
     * @param pageId path pageID
     * @param from path from
     * @param to path to
     */
    @SelectProvider(type = PathsMapperProvider.class, method = "getPaths")
    @Results({
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_to", property = "to"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport"),
            @Result(column = "line_port", property = "port"),
            @Result(column = "fk_flow_id", property = "flow", many = @Many(select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowById", fetchType = FetchType.LAZY))
    })
    List<Paths> getPathsByFlowIdAndStopPageId(String flowId, String pageId, String from, String to);

    /**
     * Query the number of connections
     *
     * @param flowId flow Id
     * @param pageId path pageID
     * @param from path from
     * @param to path to
     */
    @SelectProvider(type = PathsMapperProvider.class, method = "getPathsCounts")
    Integer getPathsCounts(String flowId, String pageId, String from, String to);

    /**
     * Query paths by id
     *
     * @param id id
     */
    @SelectProvider(type = PathsMapperProvider.class, method = "getPathsById")
    Paths getPathsById(String id);

    /**
     * Logically delete flowInfo according to flowId
     *
     * @param username username
     * @param flowId flowId
     */
    @UpdateProvider(type = PathsMapperProvider.class, method = "updateEnableFlagByFlowId")
    int updateEnableFlagByFlowId(String username, String flowId);
}