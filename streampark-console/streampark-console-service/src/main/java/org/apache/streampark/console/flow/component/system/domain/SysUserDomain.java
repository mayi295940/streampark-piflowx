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

package org.apache.streampark.console.flow.component.system.domain;

import org.apache.streampark.console.flow.component.system.entity.SysRole;
import org.apache.streampark.console.flow.component.system.entity.SysUser;
import org.apache.streampark.console.flow.component.system.mapper.SysRoleMapper;
import org.apache.streampark.console.flow.component.system.mapper.SysUserMapper;
import org.apache.streampark.console.flow.component.system.vo.SysUserVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class SysUserDomain {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public SysUserDomain(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    public int addSysUser(SysUser sysUser) throws Exception {
        if (null == sysUser) {
            throw new Exception("sysUser is null");
        }
        int insertSysUserAffectedRows = sysUserMapper.insertSysUser(sysUser);
        if (insertSysUserAffectedRows <= 0) {
            throw new Exception("save failed");
        }
        List<SysRole> roles = sysUser.getRoles();
        if (null == roles) {
            throw new Exception("save failed");
        }
        int insertSysRoleListAffectedRows = sysRoleMapper.insertSysRoleList(sysUser.getId(), roles);
        if (insertSysRoleListAffectedRows <= 0) {
            throw new Exception("save failed");
        }
        return insertSysUserAffectedRows + insertSysRoleListAffectedRows;
    }

    public int updateSysUser(SysUser sysUser) throws Exception {
        if (null == sysUser) {
            throw new Exception("sysUser is null");
        }
        return sysUserMapper.updateSysUser(sysUser);
    }

    public List<SysUserVo> getSysUserVoList(boolean isAdmin, String username, String param) {
        return sysUserMapper.getSysUserVoList(isAdmin, username, param);
    }

    public SysUserVo getSysUserVoById(boolean isAdmin, String username, String param) {
        return sysUserMapper.getSysUserVoById(isAdmin, username, param);
    }

    public SysUser getSysUserById(boolean isAdmin, String username, String param) {
        return sysUserMapper.getSysUserById(isAdmin, username, param);
    }

    public SysUser findUserByUserName(String userName) {
        return sysUserMapper.findUserByUserName(userName);
    }

    public List<SysUser> findUserByName(String name) {
        return sysUserMapper.findUserByName(name);
    }

    public long getSysRoleMaxId() {
        return sysRoleMapper.getMaxId();
    }

    public int deleteUserById(String id) {
        if (StringUtils.isBlank(id)) {
            return 0;
        }
        return sysUserMapper.deleteUserById(id);
    }

    public String checkUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return sysUserMapper.checkUsername(username);
    }
}