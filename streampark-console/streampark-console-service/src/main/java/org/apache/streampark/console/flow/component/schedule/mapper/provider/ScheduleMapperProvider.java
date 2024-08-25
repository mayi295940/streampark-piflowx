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

package org.apache.streampark.console.flow.component.schedule.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;

@Mapper
public class ScheduleMapperProvider {

    private String id;
    private String lastUpdateUser;
    private String lastUpdateDttmStr;
    private long version;
    private int enableFlag;
    private String scheduleId;
    private String type;
    private String statusStr;
    private String cronExpression;
    private String planStartTimeStr;
    private String planEndTimeStr;
    private String scheduleProcessTemplateId;
    private String scheduleRunTemplateId;

    private boolean preventSQLInjectionSchedule(Schedule schedule) {
        if (null == schedule || StringUtils.isBlank(schedule.getLastUpdateUser())) {
            return false;
        }
        // Mandatory Field
        String lastUpdateDttm =
            DateUtils.dateTimesToStr(
                null != schedule.getLastUpdateDttm() ? schedule.getLastUpdateDttm() : new Date());
        this.id = SqlUtils.preventSQLInjection(schedule.getId());
        this.enableFlag = ((null != schedule.getEnableFlag() && schedule.getEnableFlag()) ? 1 : 0);
        this.version = (null != schedule.getVersion() ? schedule.getVersion() : 0L);
        this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttm);
        this.lastUpdateUser = SqlUtils.preventSQLInjection(schedule.getLastUpdateUser());

        // Selection field
        String planStartTime =
            (null != schedule.getPlanStartTime()
                ? DateUtils.dateTimesToStr(schedule.getPlanStartTime())
                : null);
        String planEndTime =
            (null != schedule.getPlanEndTime()
                ? DateUtils.dateTimesToStr(schedule.getPlanEndTime())
                : null);
        this.scheduleId = SqlUtils.preventSQLInjection(schedule.getScheduleId());
        this.type = SqlUtils.preventSQLInjection(schedule.getType());
        this.statusStr =
            SqlUtils.preventSQLInjection(
                null != schedule.getStatus() ? schedule.getStatus().name() : "INIT");
        this.cronExpression = SqlUtils.preventSQLInjection(schedule.getCronExpression());
        this.planStartTimeStr = SqlUtils.preventSQLInjection(planStartTime);
        this.planEndTimeStr = SqlUtils.preventSQLInjection(planEndTime);
        this.scheduleRunTemplateId = SqlUtils.preventSQLInjection(schedule.getScheduleRunTemplateId());
        this.scheduleProcessTemplateId =
            SqlUtils.preventSQLInjection(schedule.getScheduleProcessTemplateId());

        return true;
    }

    private void resetSchedule() {
        this.id = null;
        this.lastUpdateUser = null;
        this.lastUpdateDttmStr = null;
        this.version = 0L;
        this.enableFlag = 1;
        this.scheduleId = null;
        this.type = null;
        this.statusStr = null;
        this.cronExpression = null;
        this.planStartTimeStr = null;
        this.planEndTimeStr = null;
        this.scheduleProcessTemplateId = null;
        this.scheduleRunTemplateId = null;
    }

    /**
     * insert schedule
     *
     * @param schedule schedule
     * @return string sql
     */
    public String insert(Schedule schedule) {
        String sqlStr = "SELECT 0";
        boolean flag = this.preventSQLInjectionSchedule(schedule);
        if (flag) {
            String strBuf =
                "INSERT INTO group_schedule "
                    + "( "
                    + SqlUtils.baseFieldName()
                    + ", "
                    + "schedule_id, "
                    + "type, "
                    + "status, "
                    + "cron_expression, "
                    + "plan_start_time, "
                    + "plan_end_time, "
                    + "schedule_run_template_id, "
                    + "schedule_process_template_id "
                    + ") "
                    + "values "
                    + "("
                    + SqlUtils.baseFieldValues(schedule)
                    + ", "
                    + this.scheduleId
                    + ", "
                    + this.type
                    + ", "
                    + this.statusStr
                    + ", "
                    + this.cronExpression
                    + ", "
                    + this.planStartTimeStr
                    + ", "
                    + this.planEndTimeStr
                    + ", "
                    + this.scheduleRunTemplateId
                    + ","
                    + this.scheduleProcessTemplateId
                    + ")";
            this.resetSchedule();
            sqlStr = strBuf + ";";
        }
        return sqlStr;
    }

    /**
     * update schedule
     *
     * @param schedule schedule
     * @return string sql
     */
    public String update(Schedule schedule) {

        String sqlStr = "SELECT 0";
        boolean flag = this.preventSQLInjectionSchedule(schedule);
        if (flag && StringUtils.isNotBlank(this.id)) {
            SQL sql = new SQL();
            // INSERT_INTO brackets is table name
            sql.UPDATE("group_schedule");
            // The first string in the SET is the name of the field corresponding to the table in the
            // database
            sql.SET("last_update_dttm = " + lastUpdateDttmStr);
            sql.SET("last_update_user = " + lastUpdateUser);
            sql.SET("version = " + (version + 1));

            // handle other fields
            sql.SET("enable_flag = " + enableFlag);
            sql.SET("schedule_id = " + this.scheduleId);
            sql.SET("type = " + this.type);
            sql.SET("status = " + this.statusStr);
            sql.SET("cron_expression = " + this.cronExpression);
            sql.SET("plan_start_time = " + this.planStartTimeStr);
            sql.SET("plan_end_time = " + this.planEndTimeStr);
            sql.SET("schedule_run_template_id = " + this.scheduleRunTemplateId);
            sql.SET("schedule_process_template_id = " + this.scheduleProcessTemplateId);
            sql.WHERE("version = " + this.version);
            sql.WHERE("id = " + this.id);
            sqlStr = sql.toString();
        }
        this.resetSchedule();
        return sqlStr;
    }

    /**
     * get Schedule list
     *
     * @param isAdmin is admin
     * @param username username
     * @param param like param
     * @return sql
     */
    public String getScheduleList(boolean isAdmin, String username, String param) {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("SELECT gs.*, CASE gs.type ");
        strBuf.append("WHEN 'FLOW' THEN f.name ");
        strBuf.append("WHEN 'FLOW_GROUP' THEN fg.name ");
        strBuf.append("END schedule_run_template_name ");
        strBuf.append("from group_schedule gs ");
        strBuf.append("LEFT JOIN flow f ON f.id = gs.schedule_run_template_id ");
        strBuf.append("LEFT JOIN flow_group fg ON fg.id = gs.schedule_run_template_id ");
        strBuf.append("where ");
        strBuf.append("gs.enable_flag = 1 ");
        if (StringUtils.isNotBlank(param)) {
            strBuf.append("and ( ");
            strBuf
                .append("gs.type like CONCAT('%',")
                .append(SqlUtils.preventSQLInjection(param))
                .append(",'%') ");
            strBuf
                .append("or gs.cron_expression like CONCAT('%',")
                .append(SqlUtils.preventSQLInjection(param))
                .append(",'%') ");
            strBuf.append(") ");
        }
        if (!isAdmin) {
            strBuf.append("and gs.crt_user = ").append(SqlUtils.preventSQLInjection(username));
        }
        strBuf.append("order by crt_dttm desc ");
        return strBuf.toString();
    }

    /**
     * get Schedule by id
     *
     * @param isAdmin is admin
     * @param username username
     * @param id id
     * @return sql
     */
    public String getScheduleById(boolean isAdmin, String username, String id) {
        if (StringUtils.isBlank(id)) {
            return "SELECT 0";
        }
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("select * ");
        strBuf.append("from group_schedule ");
        strBuf.append("where ");
        strBuf.append("enable_flag = 1 ");
        strBuf.append("and id = ").append(SqlUtils.preventSQLInjection(id)).append(" ");
        if (!isAdmin) {
            strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
        }
        return strBuf.toString();
    }

    /**
     * del Schedule by id
     *
     * @param isAdmin is admin
     * @param username username
     * @param id id
     * @return sql
     */
    public String delScheduleById(boolean isAdmin, String username, String id) {
        if (StringUtils.isBlank(id)) {
            return "SELECT 0";
        }
        if (StringUtils.isBlank(username)) {
            return "SELECT 0";
        }
        StringBuilder strBuf = new StringBuilder();
        String lastUpdateDttm = DateUtils.dateTimesToStr(new Date());
        strBuf.append("update group_schedule set ");
        strBuf.append("enable_flag = 0 , ");
        strBuf
            .append("last_update_user = ")
            .append(SqlUtils.preventSQLInjection(username))
            .append(" , ");
        strBuf
            .append("last_update_dttm = ")
            .append(SqlUtils.preventSQLInjection(lastUpdateDttm))
            .append(" ");
        strBuf.append("where ");
        strBuf.append("enable_flag = 1 ");
        strBuf.append("and id = ").append(SqlUtils.preventSQLInjection(id)).append(" ");
        if (!isAdmin) {
            strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
        }
        return strBuf.toString();
    }

    public String getScheduleIdListByStateRunning(boolean isAdmin, String username) {
        if (StringUtils.isBlank(username)) {
            return "SELECT 0";
        }
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("select * from group_schedule ");
        strBuf.append("where ");
        strBuf.append("enable_flag=1 ");
        strBuf.append("and ");
        strBuf.append("status='RUNNING' ");
        if (!isAdmin) {
            strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
        }
        return strBuf.toString();
    }

    public String getScheduleIdListByScheduleRunTemplateId(
                                                           boolean isAdmin, String username,
                                                           String scheduleRunTemplateId) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(scheduleRunTemplateId)) {
            return "SELECT 0";
        }
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("select count(id) from group_schedule ");
        strBuf.append("where ");
        strBuf.append("enable_flag=1 ");
        strBuf.append("and ");
        strBuf
            .append("schedule_run_template_id=")
            .append(SqlUtils.preventSQLInjection(scheduleRunTemplateId));
        if (!isAdmin) {
            strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
        }
        return strBuf.toString();
    }
}
