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
import org.apache.streampark.console.flow.common.Eunm.TemplateType;
import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;

import org.apache.commons.lang3.StringUtils;

public class FlowTemplateMapperProvider {

    private String sourceFlowName;
    private String templateTypeStr;
    private String name;
    private String description;
    private String path;
    private String url;

    private boolean preventSQLInjectionDataSource(FlowTemplate flowTemplate) {
        if (null == flowTemplate || StringUtils.isBlank(flowTemplate.getLastUpdateUser())) {
            return false;
        }

        // Selection field
        TemplateType templateType = flowTemplate.getTemplateType();
        this.templateTypeStr =
            SqlUtils.preventSQLInjection(null != templateType ? templateType.name() : "");
        this.sourceFlowName = SqlUtils.preventSQLInjection(flowTemplate.getSourceFlowName());
        this.name = SqlUtils.preventSQLInjection(flowTemplate.getName());
        this.description = SqlUtils.preventSQLInjection(flowTemplate.getDescription());
        this.path = SqlUtils.preventSQLInjection(flowTemplate.getPath());
        this.url = SqlUtils.preventSQLInjection(flowTemplate.getUrl());
        return true;
    }

    private void reset() {
        this.sourceFlowName = null;
        this.templateTypeStr = null;
        this.name = null;
        this.description = null;
        this.path = null;
        this.url = null;
    }

    public String insertFlowTemplate(FlowTemplate flowTemplate) {
        String sqlStr = "select 0";
        if (preventSQLInjectionDataSource(flowTemplate)) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("INSERT INTO flow_template ");

            strBuf.append("( ");
            strBuf.append(SqlUtils.baseFieldName() + ", ");
            strBuf.append("source_flow_name, template_type, name, description, path, url ");
            strBuf.append(") ");

            strBuf.append("values ");
            strBuf.append("(");
            strBuf.append(SqlUtils.baseFieldValues(flowTemplate) + ", ");
            strBuf.append(this.sourceFlowName + ", ");
            strBuf.append(this.templateTypeStr + ", ");
            strBuf.append(this.name + ", ");
            strBuf.append(this.description + ", ");
            strBuf.append(this.path + ", ");
            strBuf.append(this.url + " ");
            strBuf.append(")");
            sqlStr = strBuf.toString();
        }
        this.reset();
        return sqlStr;
    }

    public String updateEnableFlagById(String id, boolean enableFlag) {
        if (StringUtils.isBlank(id)) {
            return "select 0";
        }
        int enableFlagInt = enableFlag ? 1 : 0;
        return "update flow_template ft set ft.enable_flag = "
            + enableFlagInt
            + " where ft.id = "
            + SqlUtils.preventSQLInjection(id);
    }

    public String getFlowTemplateList(String username, boolean isAdmin) {
        String sqlStr = "";
        if (isAdmin) {
            sqlStr = "select ft.* from flow_template ft where ft.enable_flag=1 order by crt_dttm desc ";
        } else {
            String user_value = SqlUtils.preventSQLInjection(username);
            sqlStr =
                "select ft.* from flow_template ft where ft.enable_flag=1 and crt_user="
                    + user_value
                    + " order by ft.crt_dttm desc";
        }
        return sqlStr;
    }

    public String getFlowTemplateListByParam(String username, boolean isAdmin, String param) {
        String sqlStr = "";
        param = StringUtils.isBlank(param) ? "" : param;
        String param_value = SqlUtils.preventSQLInjection(param);
        if (isAdmin) {
            sqlStr =
                "select ft.* from flow_template ft where ft.enable_flag=1 and (ft.name like CONCAT('%',"
                    + param_value
                    + ",'%'))";
        } else {
            String user_value = SqlUtils.preventSQLInjection(username);
            sqlStr =
                "select ft.* from flow_template ft where ft.enable_flag=1 and ft.crt_user="
                    + user_value
                    + " and (ft.name like CONCAT('%',"
                    + param_value
                    + ",'%'))";
        }
        return sqlStr;
    }
}