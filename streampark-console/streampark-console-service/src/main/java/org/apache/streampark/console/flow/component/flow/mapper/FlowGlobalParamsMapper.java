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

import org.apache.streampark.console.flow.component.flow.entity.FlowGlobalParams;
import org.apache.streampark.console.flow.component.flow.mapper.provider.FlowGlobalParamsMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface FlowGlobalParamsMapper {

    @InsertProvider(type = FlowGlobalParamsMapperProvider.class, method = "addGlobalParams")
    int addGlobalParams(FlowGlobalParams globalParams);

    @UpdateProvider(type = FlowGlobalParamsMapperProvider.class, method = "updateGlobalParams")
    int updateGlobalParams(FlowGlobalParams globalParams);

    @UpdateProvider(type = FlowGlobalParamsMapperProvider.class, method = "updateEnableFlagById")
    int updateEnableFlagById(String username, String id, boolean enableFlag);

    @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getGlobalParamsListParam")
    List<FlowGlobalParams> getGlobalParamsListParam(String username, boolean isAdmin, String param);

    /**
     * Query FlowGlobalParams based on FlowGroup Id
     *
     * @param id FlowGroup Id
     */
    @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getGlobalParamsById")
    FlowGlobalParams getGlobalParamsById(String username, boolean isAdmin, String id);

    @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getFlowGlobalParamsByIds")
    List<FlowGlobalParams> getFlowGlobalParamsByIds(String[] ids);

    @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getFlowGlobalParamsByFlowId")
    List<FlowGlobalParams> getFlowGlobalParamsByFlowId(String flowId);

    @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getFlowGlobalParamsByProcessId")
    List<FlowGlobalParams> getFlowGlobalParamsByProcessId(String processId);
}
