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

import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessAndProcessGroupMapperProvider;
import org.apache.streampark.console.flow.component.process.vo.ProcessAndProcessGroupVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ProcessAndProcessGroupMapper {

    /** query all TemplateDataSource */
    @SelectProvider(type = ProcessAndProcessGroupMapperProvider.class, method = "getProcessAndProcessGroupList")
    List<ProcessAndProcessGroupVo> getProcessAndProcessGroupList(String param);

    @SelectProvider(type = ProcessAndProcessGroupMapperProvider.class, method = "getProcessAndProcessGroupListByUser")
    List<ProcessAndProcessGroupVo> getProcessAndProcessGroupListByUser(String param, String username);
}