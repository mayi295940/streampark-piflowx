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

package org.apache.streampark.console.flow.component.stopsComponent.mapper.provider;

import org.apache.streampark.console.flow.base.utils.SqlUtils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class StopsComponentGroupProvider {

    /** 查詢所有組 */
    public String getStopGroupList(@Param("engineType") String engineType) {
        String sqlStr = "";
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("flow_stops_groups");
        sql.WHERE("enable_flag = 1 and engine_type = '" + engineType + "' ");
        sql.ORDER_BY(" group_name ");
        sqlStr = sql.toString();
        return sqlStr;
    }

    public String getStopGroupByGroupNameList(
                                              @Param("groupName") List<String> groupName,
                                              @Param("engineType") String engineType) {
        return "select * from flow_stops_groups where group_name in ("
            + SqlUtils.strListToStr(groupName)
            + ") and enable_flag = 1 and engine_type = '"
            + engineType
            + "'";
    }
}
