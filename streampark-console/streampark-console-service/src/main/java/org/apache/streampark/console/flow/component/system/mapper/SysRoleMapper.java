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

import org.apache.streampark.console.flow.component.system.entity.SysRole;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysRoleMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Select("select Max(id) from sys_role")
    long getMaxId();

    /**
     * getSysRoleListBySysUserId
     *
     * @param sysUserId sysUserId
     */
    @SelectProvider(type = SysRoleMapperProvider.class, method = "getSysRoleListBySysUserId")
    List<SysRole> getSysRoleListBySysUserId(String sysUserId);

    @InsertProvider(type = SysRoleMapperProvider.class, method = "insertSysRoleList")
    int insertSysRoleList(@Param("userId") String userId, @Param("roles") List<SysRole> roles);

    @SelectProvider(type = SysRoleMapperProvider.class, method = "getSysRoleBySysUserId")
    SysRole getSysRoleBySysUserId(String sysUserId);
}
