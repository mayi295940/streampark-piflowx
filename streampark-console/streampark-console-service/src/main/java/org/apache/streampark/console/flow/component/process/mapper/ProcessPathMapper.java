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

import org.apache.streampark.console.flow.component.process.entity.ProcessPath;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessPathMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface ProcessPathMapper {

    /**
     * add processPath
     *
     * @param processPath processPath
     */
    @InsertProvider(type = ProcessPathMapperProvider.class, method = "addProcessPath")
    int addProcessPath(ProcessPath processPath);

    /**
     * add processPath
     *
     * @param processPathList processPathList
     */
    @InsertProvider(type = ProcessPathMapperProvider.class, method = "addProcessPathList")
    int addProcessPathList(List<ProcessPath> processPathList);

    /**
     * Query processPath according to process Id
     *
     * @param processId processId
     */
    @SelectProvider(type = ProcessPathMapperProvider.class, method = "getProcessPathByProcessId")
    @Results({
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport"),
            @Result(column = "line_to", property = "to")
    })
    ProcessPath getProcessPathByProcessId(String processId);

    /**
     * Query based on pid and pageId
     *
     * @param processId processId
     * @param pageId pageId
     */
    @SelectProvider(type = ProcessPathMapperProvider.class, method = "getProcessPathByPageIdAndPid")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport"),
            @Result(column = "line_to", property = "to")
    })
    ProcessPath getProcessPathByPageIdAndPid(String processId, String pageId);

    /**
     * update processPath
     *
     * @param processPath processPath
     */
    @UpdateProvider(type = ProcessPathMapperProvider.class, method = "updateProcessPath")
    int updateProcessPath(ProcessPath processPath);

    @UpdateProvider(type = ProcessPathMapperProvider.class, method = "updateEnableFlagByProcessId")
    int updateEnableFlagByProcessId(String processId, String userName);

    /**
     * Query based on processGroupId and pageId
     *
     * @param processGroupId processGroupId
     * @param pageId pageId
     */
    @SelectProvider(type = ProcessPathMapperProvider.class, method = "getProcessPathByPageIdAndProcessGroupId")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "line_from", property = "from"),
            @Result(column = "line_outport", property = "outport"),
            @Result(column = "line_inport", property = "inport"),
            @Result(column = "line_to", property = "to")
    })
    ProcessPath getProcessPathByPageIdAndProcessGroupId(String processGroupId, String pageId);
}
