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

package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysInitRecords;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysInitRecordsMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysInitRecordsMapper {

    @InsertProvider(type = SysInitRecordsMapperProvider.class, method = "insertSysInitRecords")
    Integer insertSysInitRecords(SysInitRecords sysInitRecords);

    @Select("select * from sys_init_records")
    List<SysInitRecords> getSysInitRecordsList();

    @Select("select * from sys_init_records where id=#{id}")
    SysInitRecords getSysInitRecordsById(@Param("id") String id);

    @Select("select * from sys_init_records order by init_date desc limit #{limit}")
    SysInitRecords getSysInitRecordsLastNew(@Param("limit") int limit);
}
