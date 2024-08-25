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

package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHubFileRecord;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsHubFileRecordMapperProvider;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface StopsHubFileRecordMapper {

    /**
     * 添加算法包的具体文件记录
     *
     * @param record record
     */
    @InsertProvider(type = StopsHubFileRecordMapperProvider.class, method = "addStopsHubFileRecord")
    int addStopsHubFileRecord(StopsHubFileRecord record);

    @Select("select * from stops_hub_file_record where stops_hub_id = #{hubId}")
    @Results({
            @Result(column = "file_path", property = "filePath"),
            @Result(column = "file_path", property = "stopsComponent", one = @One(select = "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getOnlyStopsComponentByBundle", fetchType = FetchType.LAZY))
    })
    List<StopsHubFileRecord> getStopsHubFileRecordByHubId(@Param("hubId") String hubId);

    /**
     * 根据id查询算法包的具体文件记录
     *
     * @param id id
     */
    @Select("select * from stops_hub_file_record where id = #{id}")
    StopsHubFileRecord getStopsHubFileRecordById(@Param("id") String id);

    /**
     * 根据id查询算法包的具体文件记录
     *
     * @param stopBundle stopBundle
     */
    @Select("select * from stops_hub_file_record where file_Path = #{stopBundle}")
    StopsHubFileRecord getStopsHubFileRecordByBundle(@Param("stopBundle") String stopBundle);

    /**
     * 根据id删除算法包的具体文件记录
     *
     * @param id id
     */
    @Delete("delete from stops_hub_file_record where id = #{id}")
    int deleteStopsHubFileRecord(@Param("id") String id);
}
