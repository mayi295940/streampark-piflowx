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

import org.apache.streampark.console.flow.component.process.entity.ProcessStopProperty;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessStopPropertyMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface ProcessStopPropertyMapper {

    @InsertProvider(type = ProcessStopPropertyMapperProvider.class, method = "addProcessStopProperty")
    int addProcessStopProperty(ProcessStopProperty processStopProperty);

    @InsertProvider(type = ProcessStopPropertyMapperProvider.class, method = "addProcessStopProperties")
    int addProcessStopProperties(List<ProcessStopProperty> processStopPropertyList);

    /**
     * Query processStop attribute based on processStopId
     *
     * @param processStopId processStopId
     */
    @SelectProvider(type = ProcessStopPropertyMapperProvider.class, method = "getStopPropertyByProcessStopId")
    @Results({
            @Result(column = "custom_value", property = "customValue"),
            @Result(column = "allowable_values", property = "allowableValues"),
            @Result(column = "property_required", property = "required"),
            @Result(column = "property_sensitive", property = "sensitive")
    })
    ProcessStopProperty getStopPropertyByProcessStopId(String processStopId);

    @UpdateProvider(type = ProcessStopPropertyMapperProvider.class, method = "updateProcessStopProperty")
    int updateProcessStopProperty(ProcessStopProperty processStopProperty);

    @UpdateProvider(type = ProcessStopPropertyMapperProvider.class, method = "updateEnableFlagByProcessStopId")
    int updateEnableFlagByProcessStopId(String processStopId, String username);
}
