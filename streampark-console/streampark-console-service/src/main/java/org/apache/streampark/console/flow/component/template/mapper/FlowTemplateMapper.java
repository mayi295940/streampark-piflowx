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

package org.apache.streampark.console.flow.component.template.mapper;

import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;
import org.apache.streampark.console.flow.component.template.mapper.provider.FlowTemplateMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface FlowTemplateMapper {

    @InsertProvider(type = FlowTemplateMapperProvider.class, method = "insertFlowTemplate")
    int insertFlowTemplate(FlowTemplate flowTemplate);

    @UpdateProvider(type = FlowTemplateMapperProvider.class, method = "updateEnableFlagById")
    int updateEnableFlagById(String id, boolean enableFlag);

    @Select("select ft.* from flow_template ft where enable_flag and ft.id=#{id}")
    FlowTemplate getFlowTemplateById(@Param("id") String id);

    @SelectProvider(type = FlowTemplateMapperProvider.class, method = "getFlowTemplateList")
    List<FlowTemplate> getFlowTemplateList(String username, boolean isAdmin);

    @SelectProvider(type = FlowTemplateMapperProvider.class, method = "getFlowTemplateListByParam")
    List<FlowTemplate> getFlowTemplateListByParam(String username, boolean isAdmin, String param);
}
