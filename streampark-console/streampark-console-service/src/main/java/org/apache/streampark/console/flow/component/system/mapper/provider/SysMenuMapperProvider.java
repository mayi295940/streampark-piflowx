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
import org.apache.streampark.console.flow.common.Eunm.SysRoleType;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class SysMenuMapperProvider {

    /**
     * getSysMenuList
     *
     * @param role
     * @return
     */
    public String getSysMenuList(String role) {
        String sqlStr = "SELECT 0";
        if (StringUtils.isNotBlank(role)) {
            SQL sql = new SQL();
            sql.SELECT("*");
            sql.FROM("sys_menu");
            if (!SysRoleType.ADMIN.getValue().equals(role)) {
                sql.WHERE("menu_jurisdiction = " + SqlUtils.addSqlStrAndReplace(role));
            }
            sql.WHERE("enable_flag = 1");
            sql.ORDER_BY("menu_sort asc", "last_update_dttm desc");
            sqlStr = sql.toString();
        }
        return sqlStr;
    }

    /**
     * getSampleMenuList
     *
     * @return
     */
    public String getSampleMenuList() {
        String sqlStr = "SELECT 0";
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("sys_menu");
        sql.WHERE("enable_flag = 1");
        sql.WHERE("menu_parent = 'Example'");
        sqlStr = sql.toString();
        return sqlStr;
    }

    public static String deleteSampleMenuListByIds(Map<String, String[]> map) {
        String sqlStr = "SELECT 0";
        if (null == map) {
            return sqlStr;
        }
        String[] ids = map.get("ids");
        if (null == ids || ids.length <= 0) {
            return sqlStr;
        }
        SQL sql = new SQL();
        sql.DELETE_FROM("sys_menu");
        sql.WHERE("id in (" + SqlUtils.strArrayToStr(ids) + ")");
        sqlStr = sql.toString();
        return sqlStr;
    }
}
