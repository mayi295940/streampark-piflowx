package org.apache.streampark.console.flow.component.process.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.common.Eunm.ProcessParentType;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.component.process.entity.Process;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.Map;

public class ProcessMapperProvider {

  private String id;
  private String lastUpdateDttmStr;
  private String lastUpdateUser;
  private int enableFlag;
  private long version;
  private String name;
  private String engineType;
  private String environment;
  private String description;
  private String appId;
  private String pageId;
  private String progress;
  private String runModeTypeStr;
  private String flowId;
  private String parentProcessId;
  private String processParentType;
  private String processId;
  private String stateName;
  private String startTimeStr;
  private String endTimeStr;
  private String schedule_id;
  private String processGroup_id;
  private String viewXml;

  private boolean preventSQLInjectionProcess(Process process) {
    if (null == process || StringUtils.isBlank(process.getLastUpdateUser())) {
      return false;
    }

    // Mandatory Field
    this.id = SqlUtils.preventSQLInjection(process.getId());
    this.lastUpdateUser = SqlUtils.preventSQLInjection(process.getLastUpdateUser());
    String lastUpdateDttmStr =
        DateUtils.dateTimesToStr(
            null != process.getLastUpdateDttm() ? process.getLastUpdateDttm() : new Date());
    this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttmStr);
    this.enableFlag = ((null != process.getEnableFlag() && process.getEnableFlag()) ? 1 : 0);
    this.version = (null != process.getVersion() ? process.getVersion() : 0L);

    // Selection field
    this.name = SqlUtils.preventSQLInjection(process.getName());
    this.environment = SqlUtils.preventSQLInjection(process.getEnvironment());
    this.viewXml = SqlUtils.preventSQLInjection(process.getViewXml());
    this.description = SqlUtils.preventSQLInjection(process.getDescription());
    this.appId = SqlUtils.preventSQLInjection(process.getAppId());
    this.pageId = SqlUtils.preventSQLInjection(process.getPageId());
    this.processId = SqlUtils.preventSQLInjection(process.getProcessId());
    this.stateName =
        SqlUtils.preventSQLInjection(null != process.getState() ? process.getState().name() : null);
    String startTime =
        (null != process.getStartTime() ? DateUtils.dateTimesToStr(process.getStartTime()) : null);
    this.startTimeStr = SqlUtils.preventSQLInjection(startTime);
    String endTime =
        (null != process.getEndTime() ? DateUtils.dateTimesToStr(process.getEndTime()) : null);
    this.endTimeStr = SqlUtils.preventSQLInjection(endTime);
    this.progress = SqlUtils.preventSQLInjection(process.getProgress());
    this.flowId = SqlUtils.preventSQLInjection(process.getFlowId());
    this.runModeTypeStr =
        SqlUtils.preventSQLInjection(
            null != process.getRunModeType() ? process.getRunModeType().name() : null);
    this.parentProcessId = SqlUtils.preventSQLInjection(process.getParentProcessId());
    this.processParentType =
        SqlUtils.preventSQLInjection(
            null != process.getProcessParentType() ? process.getProcessParentType().name() : null);
    this.schedule_id =
        SqlUtils.preventSQLInjection(
            null != process.getSchedule() ? process.getSchedule().getId() : null);
    this.processGroup_id =
        SqlUtils.preventSQLInjection(
            null != process.getProcessGroup() ? process.getProcessGroup().getId() : null);
    return true;
  }

  private void reset() {
    this.id = null;
    this.lastUpdateDttmStr = null;
    this.lastUpdateUser = null;
    this.enableFlag = 1;
    this.version = 0L;
    this.name = null;
    this.engineType = null;
    this.environment = null;
    this.description = null;
    this.appId = null;
    this.pageId = null;
    this.progress = null;
    this.runModeTypeStr = null;
    this.flowId = null;
    this.parentProcessId = null;
    this.processParentType = null;
    this.processId = null;
    this.stateName = null;
    this.startTimeStr = null;
    this.endTimeStr = null;
    this.schedule_id = null;
    this.processGroup_id = null;
    this.viewXml = null;
  }

  /**
   * addProcess
   *
   * @param process process
   */
  public String addProcess(Process process) {
    String sqlStr = "SELECT 0";
    if (this.preventSQLInjectionProcess(process)) {
      String strBuf =
          "INSERT INTO flow_process "
              + "( "
              + SqlUtils.baseFieldName()
              + ", "
              + "name, "
              + "engine_type, "
              + "environment, "
              + "description, "
              + "app_id, "
              + "page_id, "
              + "process_id, "
              + "state, "
              + "start_time, "
              + "end_time, "
              + "progress, "
              + "flow_id, "
              + "run_mode_type, "
              + "parent_process_id, "
              + "process_parent_type, "
              + "fk_group_schedule_id, "
              + "fk_flow_process_group_id, "
              + "view_xml "
              + ") "
              + "VALUES "
              + "("
              + SqlUtils.baseFieldValues(process)
              + ", "
              + name
              + ", "
              + engineType
              + ", "
              + environment
              + ", "
              + description
              + ", "
              + appId
              + ", "
              + pageId
              + ", "
              + processId
              + ", "
              + stateName
              + ", "
              + startTimeStr
              + ", "
              + endTimeStr
              + ", "
              + progress
              + ", "
              + flowId
              + ", "
              + runModeTypeStr
              + ", "
              + parentProcessId
              + ", "
              + processParentType
              + ", "
              + schedule_id
              + ", "
              + processGroup_id
              + ", "
              + viewXml
              + " "
              + ") ";
      this.reset();
      return strBuf + ";";
    }
    this.reset();
    return sqlStr;
  }

  /**
   * update process
   *
   * @param process process
   */
  public String updateProcess(Process process) {
    String sqlStr = "SELECT 0";
    if (this.preventSQLInjectionProcess(process)) {
      SQL sql = new SQL();
      sql.UPDATE("flow_process");

      // Process the required fields first
      sql.SET("last_update_dttm = " + lastUpdateDttmStr);
      sql.SET("last_update_user = " + lastUpdateUser);
      sql.SET("version = " + (version + 1));

      // handle other fields
      sql.SET("enable_flag=" + enableFlag);
      sql.SET("name=" + name);
      sql.SET("environment=" + environment);
      sql.SET("view_xml=" + viewXml);
      sql.SET("description=" + description);
      sql.SET("app_id=" + appId);
      sql.SET("process_id=" + processId);
      sql.SET("state=" + stateName);
      sql.SET("start_time=" + startTimeStr);
      sql.SET("end_time=" + endTimeStr);
      sql.SET("progress=" + progress);
      sql.SET("run_mode_type=" + runModeTypeStr);
      sql.WHERE("version = " + version);
      sql.WHERE("id = " + id);
      if (StringUtils.isNotBlank(id)) {
        sqlStr = sql.toString();
      }
    }
    this.reset();
    return sqlStr;
  }

  /**
   * Query process by process ID
   *
   * @param id process ID
   */
  public String getProcessById(String username, boolean isAdmin, String id) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(id)) {
      StringBuilder strBuf = new StringBuilder();
      strBuf.append("select * ");
      strBuf.append("from flow_process ");
      strBuf.append("where enable_flag = 1 ");
      strBuf.append("and id= ").append(SqlUtils.preventSQLInjection(id));
      if (!isAdmin) {
        strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
      }
      sqlStr = strBuf.toString();
    }
    return sqlStr;
  }

  /**
   * Query process by processGroup ID
   *
   * @param processGroupId processGroup ID
   */
  public String getProcessByProcessGroupId(String processGroupId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(processGroupId)) {
      sqlStr =
          "select * "
              + "from flow_process "
              + "where enable_flag = 1 "
              + "and fk_flow_process_group_id= "
              + SqlUtils.preventSQLInjection(processGroupId);
    }
    return sqlStr;
  }

  /** Query process list(processList) */
  public String getProcessList() {
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_process");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("app_id is not null");
    sql.ORDER_BY("crt_dttm desc", "last_update_dttm desc");
    return sql.toString();
  }

  /**
   * Query process list according to param(processList)
   *
   * @param param param
   */
  public String getProcessListByParam(String username, boolean isAdmin, String param) {
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("SELECT * ");
    strBuf.append("FROM flow_process ");
    strBuf.append("WHERE ");
    strBuf.append("enable_flag = 1 ");
    strBuf.append("AND app_id IS NOT NULL ");
    strBuf
        .append("AND process_parent_type = ")
        .append(SqlUtils.addSqlStrAndReplace(ProcessParentType.PROCESS.name()));
    strBuf.append("AND fk_flow_process_group_id IS NULL ");
    if (StringUtils.isNotBlank(param)) {
      strBuf.append("and ( ");
      strBuf
          .append("app_id LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("OR name LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("OR state LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("OR description LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf.append(") ");
    }
    if (!isAdmin) {
      strBuf.append("AND crt_user = ").append(SqlUtils.preventSQLInjection(username));
    }
    strBuf.append("ORDER BY crt_dttm DESC,last_update_dttm DESC ");

    return strBuf.toString();
  }

  /** Query processGroup list according to param(processList) */
  public String getProcessGroupListByParam(String username, boolean isAdmin, String param) {
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("select * ");
    strBuf.append("from flow_process ");
    strBuf.append("where ");
    strBuf.append("enable_flag = 1 ");
    strBuf.append("and app_id is not null ");
    strBuf
        .append("and process_parent_type = ")
        .append(SqlUtils.addSqlStrAndReplace(ProcessParentType.GROUP.name()));
    strBuf.append("and fk_flow_process_group_id is null ");
    if (StringUtils.isNotBlank(param)) {
      strBuf.append("and ( ");
      strBuf
          .append("app_id like CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("or name like CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("or state like CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf
          .append("or description like CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf.append(") ");
    }
    if (!isAdmin) {
      strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
    }
    strBuf.append("order by crt_dttm desc,last_update_dttm desc ");

    return strBuf.toString();
  }

  /** Query the running process list according to flowId(processList) */
  public String getRunningProcessList(String flowId) {
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_process");
    sql.WHERE("app_id is not null");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("flow_id = " + SqlUtils.preventSQLInjection(flowId));
    sql.WHERE("state = " + SqlUtils.preventSQLInjection(ProcessState.STARTED.name()));
    sql.ORDER_BY("crt_dttm desc", "last_update_dttm desc");
    return sql.toString();
  }

  /**
   * Query process according to process appId
   *
   * @param appID appID
   */
  public String getProcessByAppId(String appID) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(appID)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("flow_process");
      sql.WHERE("enable_flag = 1");
      sql.WHERE("app_id = " + SqlUtils.preventSQLInjection(appID));
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query process id according to process appId
   *
   * @param appID appID
   */
  public String getProcessIdByAppId(String appID) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(appID)) {
      SQL sql = new SQL();
      sql.SELECT("id");
      sql.FROM("flow_process");
      sql.WHERE("enable_flag = 1");
      sql.WHERE("app_id = " + SqlUtils.preventSQLInjection(appID));
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query process according to process appId
   *
   * @param appID appID
   */
  public String getProcessNoGroupByAppId(String appID) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(appID)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("flow_process");
      sql.WHERE("enable_flag = 1");
      sql.WHERE("fk_flow_process_group_id is null");
      sql.WHERE("app_id = " + SqlUtils.preventSQLInjection(appID));
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /** Query process list according to the process AppId array */
  public String getProcessListByAppIDs(Map<String, String[]> map) {
    String sqlStr = "SELECT 0";
    String[] appIDs = map.get("appIDs");
    if (null != appIDs && appIDs.length > 0) {
      SQL sql = new SQL();
      String appIDsStr = SqlUtils.strArrayToStr(appIDs);
      if (StringUtils.isNotBlank(appIDsStr)) {
        // appIDsStr = appIDsStr.replace(",", "','");
        // appIDsStr = "'" + appIDsStr + "'";

        sql.SELECT("*");
        sql.FROM("flow_process");
        sql.WHERE("enable_flag = 1");
        sql.WHERE("app_id in (" + appIDsStr + ")");

        sqlStr = sql.toString();
      }
    }
    return sqlStr;
  }

  /**
   * Tombstone
   *
   * @param id id
   */
  public String updateEnableFlag(String id, String username) {
    String sqlStr = "SELECT 0";
    if (!StringUtils.isAnyEmpty(id, username)) {
      sqlStr =
          "update flow_process "
              + "set "
              + "last_update_dttm = "
              + SqlUtils.preventSQLInjection(DateUtils.dateTimesToStr(new Date()))
              + ", "
              + "last_update_user = "
              + SqlUtils.preventSQLInjection(username)
              + ", "
              + "version=(version+1), "
              + "enable_flag = 0 "
              + "where enable_flag = 1 "
              + "and id = "
              + SqlUtils.preventSQLInjection(id);
    }
    return sqlStr;
  }

  /** Query tasks that need to be synchronized */
  public String getRunningProcess() {
    return "select app_id from flow_process "
        + "where "
        + "enable_flag=1 "
        + "and "
        + "app_id is not null "
        + "and "
        + "( "
        + "( "
        + "state!="
        + SqlUtils.preventSQLInjection(ProcessState.COMPLETED.name())
        + " "
        + "and "
        + "state!="
        + SqlUtils.preventSQLInjection(ProcessState.FAILED.name())
        + "  "
        + "and "
        + "state!="
        + SqlUtils.preventSQLInjection(ProcessState.KILLED.name())
        + " "
        + ") "
        + "or "
        + "state is null "
        + ") ";
  }

  /**
   * Query process by processGroup ID
   *
   * @param processGroupId processGroupId
   */
  public String getProcessByPageId(
      String username, boolean isAdmin, String processGroupId, String pageId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(processGroupId) && StringUtils.isNotBlank(pageId)) {
      StringBuilder strBuf = new StringBuilder();
      strBuf.append("select * ");
      strBuf.append("from flow_process ");
      strBuf.append("where enable_flag = 1 ");
      strBuf.append("and page_id= ").append(pageId).append(" ");
      strBuf
          .append("and fk_flow_process_group_id= ")
          .append(SqlUtils.preventSQLInjection(processGroupId));

      if (!isAdmin) {
        strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
      }

      sqlStr = strBuf.toString();
    }
    return sqlStr;
  }

  /** Query based on pid and pageIds */
  @SuppressWarnings("rawtypes")
  public String getProcessByPageIds(Map map) {
    String processId = (String) map.get("processGroupId");
    String[] pageIds = (String[]) map.get("pageIds");
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(processId) && null != pageIds && pageIds.length > 0) {
      String pageIdsStr = SqlUtils.strArrayToStr(pageIds);
      if (StringUtils.isNotBlank(pageIdsStr)) {

        // pageIdsStr = pageIdsStr.replace(",", "','");
        // pageIdsStr = "'" + pageIdsStr + "'";
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("flow_process");
        sql.WHERE("enable_flag = 1");
        sql.WHERE("fk_flow_process_group_id = " + SqlUtils.preventSQLInjection(processId));
        sql.WHERE("page_id in ( " + pageIdsStr + ")");

        sqlStr = sql.toString();
      }
    }
    return sqlStr;
  }

  public String getGlobalParamsIdsByProcessId(String processId) {
    if (StringUtils.isBlank(processId)) {
      return "SELECT 0";
    }
    return ("SELECT global_params_id FROM `association_global_params_flow` WHERE process_id= "
        + SqlUtils.preventSQLInjection(processId));
  }

  public String linkGlobalParams(String processId, String[] globalParamsIds) {
    if (StringUtils.isBlank(processId) || globalParamsIds.length == 0) {
      return "SELECT 0";
    }
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("INSERT INTO `association_global_params_flow` ");
    strBuf.append("( ");
    strBuf.append("`process_id`, ");
    strBuf.append("`global_params_id` ");
    strBuf.append(") ");
    strBuf.append("values ");
    for (int i = 0; i < globalParamsIds.length; i++) {
      strBuf.append("( ");
      strBuf.append(SqlUtils.preventSQLInjection(processId)).append(", ");
      strBuf.append(SqlUtils.preventSQLInjection(globalParamsIds[i]));
      strBuf.append(") ");
      if ((i + 1) < globalParamsIds.length) {
        strBuf.append(", ");
      }
    }
    return strBuf.toString();
  }

  public String unlinkGlobalParams(String processId, String[] globalParamsIds) {
    if (StringUtils.isBlank(processId) || globalParamsIds.length == 0) {
      return "SELECT 0";
    }
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("DELETE FROM `association_global_params_flow` ");
    strBuf.append("WHERE ");
    strBuf.append(" `process_id`= ").append(SqlUtils.preventSQLInjection(processId));
    strBuf.append(" AND ");
    strBuf.append(" `global_params_id` in ");
    strBuf.append("( ");
    for (int i = 0; i < globalParamsIds.length; i++) {
      strBuf.append(SqlUtils.preventSQLInjection(globalParamsIds[i]));
      if ((i + 1) < globalParamsIds.length) {
        strBuf.append(", ");
      }
    }
    strBuf.append(") ");
    return strBuf.toString();
  }
}
