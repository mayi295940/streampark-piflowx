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

package org.apache.streampark.console.flow.component.system.mapper.provider;

import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.system.entity.SysRole;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SysRoleMapperProvider {

    /**
     * getSysRoleListBySysUserId
     *
     * @param sysUserId sysUserId
     */
    public String getSysRoleListBySysUserId(String sysUserId) {
        String strSql = "SELECT 0";
        if (StringUtils.isNotBlank(sysUserId)) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("select * ");
            strBuf.append("from sys_role ");
            strBuf.append("where ");
            strBuf.append("fk_sys_user_id = '").append(sysUserId).append("'");
            strSql = strBuf.toString();
        }
        return strSql;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String insertSysRoleList(Map map) {
        List<SysRole> roles = (List<SysRole>) map.get("roles");
        String userId = (String) map.get("userId");
        if (null == roles || roles.isEmpty() || StringUtils.isBlank(userId)) {
            return "SELECT 0";
        }
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("INSERT INTO sys_role ");
        strBuf.append("(id,role,fk_sys_user_id)");
        strBuf.append("values");
        SysRole sysRole;
        for (int i = 0; i < roles.size(); i++) {
            sysRole = roles.get(i);
            if (null == sysRole) {
                continue;
            }
            if (i != 0 && roles.size() - 1 > i) {
                strBuf.append(",(");
            } else {
                strBuf.append("(");
            }
            strBuf.append(sysRole.getId() + ", ");
            strBuf.append(SqlUtils.preventSQLInjection(sysRole.getRole().name()) + ", ");
            strBuf.append(SqlUtils.preventSQLInjection(userId));
            strBuf.append(")");
        }
        return strBuf.toString();
    }

    /**
     * getSysRoleBySysUserId
     *
     * @param sysUserId sysUserId
     */
    public String getSysRoleBySysUserId(String sysUserId) {
        String strSql = "SELECT 0";
        if (StringUtils.isNotBlank(sysUserId)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SELECT * ");
            buffer.append("FROM sys_role ");
            buffer.append("WHERE ");
            buffer.append("fk_sys_user_id = '").append(sysUserId).append("' ORDER BY role ASC LIMIT 1");
            strSql = buffer.toString();
        }
        return strSql;
    }
}
