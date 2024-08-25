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

package org.apache.streampark.console.flow.component.dashboard.mapper.provider;

public class StatisticProvider {

    public String getFlowProcessStatisticInfo() {
        String sql =
            "SELECT `state` as STATE, count(*) as COUNT FROM `flow_process` WHERE `enable_flag`=1 AND `app_id` IS NOT NULL AND `fk_flow_process_group_id` IS NULL GROUP BY `state`";
        return sql;
    }

    public String getFlowCount() {
        String sql =
            "select count(*) as COUNT from `flow` where `enable_flag` = 1 and `fk_flow_group_id` is null and `is_example` != 1";
        return sql;
    }

    public String getGroupProcessStatisticInfo() {
        String sql =
            "SELECT `state` as STATE, count(*) as COUNT FROM `flow_process_group` WHERE `enable_flag`=1 AND `app_id` IS NOT NULL AND `fk_flow_process_group_id` IS NULL GROUP BY `state`;";
        return sql;
    }

    public String getGroupCount() {
        String sql =
            "select count(*) as COUNT from flow_group where enable_flag = 1 and fk_flow_group_id is null and is_example != 1";
        return sql;
    }

    public String getScheduleStatisticInfo() {
        String sql =
            "select status as STATUS, count(*) as COUNT from group_schedule where enable_flag = 1 GROUP BY status";
        return sql;
    }

    public String getTemplateCount() {
        String sql = "select count(*) as COUNT from flow_template where enable_flag = 1";
        return sql;
    }

    public String getDataSourceCount() {
        String sql =
            "select count(*) as COUNT from data_source where enable_flag = 1 and is_template = 0";
        return sql;
    }

    public String getStopsHubCount() {
        String sql = "select count(*) as COUNT from stops_hub where enable_flag = 1";
        return sql;
    }

    public String getStopsCount() {
        String sql = "select count(*) as COUNT from flow_stops_template";
        return sql;
    }

    public String getStopsGroupCount() {
        String sql = "select count(*) as COUNT from flow_stops_groups";
        return sql;
    }
}
