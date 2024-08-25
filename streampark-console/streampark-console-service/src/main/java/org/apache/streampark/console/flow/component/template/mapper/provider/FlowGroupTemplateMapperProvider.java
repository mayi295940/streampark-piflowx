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

package org.apache.streampark.console.flow.component.template.mapper.provider;

import org.apache.streampark.console.flow.base.utils.SqlUtils;

import org.apache.commons.lang3.StringUtils;

public class FlowGroupTemplateMapperProvider {

    public String getFlowGroupTemplateVoListPage(String username, boolean isAdmin, String param) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("select* ");
        strBuf.append("from flow_group_template ");
        strBuf.append("where ");
        strBuf.append("enable_flag = 1 ");
        if (StringUtils.isNotBlank(param)) {
            strBuf.append("and name like CONCAT('%'," + SqlUtils.preventSQLInjection(param) + ",'%') ");
        }
        if (!isAdmin) {
            strBuf.append("and crt_user = " + SqlUtils.preventSQLInjection(username));
        }
        strBuf.append("order by crt_dttm desc ");
        return strBuf.toString();
    }

    public String getFlowGroupTemplateVoById(String username, boolean isAdmin, String id) {
        String sqlStr = "SELECT 0";
        if (StringUtils.isNotBlank(id)) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("select* ");
            strBuf.append("from flow_group_template ");
            strBuf.append("where ");
            strBuf.append("enable_flag = 1 ");
            strBuf.append("and ");
            strBuf.append("id = " + SqlUtils.addSqlStrAndReplace(id) + " ");
            if (!isAdmin) {
                strBuf.append("and crt_user = " + SqlUtils.preventSQLInjection(username));
            }
            sqlStr = strBuf.toString();
        }
        return sqlStr;
    }
}
