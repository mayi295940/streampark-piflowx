package org.apache.streampark.console.flow.component.system.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.entity.SysUser;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

public class AdminLogMapperProvider {

  private String username;

  private String lastLoginIp;

  private String action;

  private Boolean status;

  private String result;

  private String comment;

  private String ctrDttmStr;

  private String lastUpdateDttmStr;

  private boolean preventSQLInjectionLog(SysLog sysLog) {
    if (null == sysLog) {
      return false;
    }
    // Mandatory Field
    String lastUpdateDttm =
        DateUtils.dateTimesToStr(
            null != sysLog.getLastUpdateDttm() ? sysLog.getLastUpdateDttm() : new Date());
    String ctrDttmStr =
        DateUtils.dateTimesToStr(
            null != sysLog.getLastUpdateDttm() ? sysLog.getLastUpdateDttm() : new Date());
    this.username = SqlUtils.preventSQLInjection(sysLog.getUsername());

    this.action = SqlUtils.preventSQLInjection(sysLog.getAction());
    this.comment = SqlUtils.preventSQLInjection(sysLog.getComment());
    this.result = SqlUtils.preventSQLInjection(sysLog.getResult());
    this.status = (null != sysLog.getStatus() && sysLog.getStatus());
    this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttm);
    this.ctrDttmStr = SqlUtils.preventSQLInjection(ctrDttmStr);
    this.lastLoginIp = SqlUtils.preventSQLInjection(sysLog.getLastLoginIp());

    return true;
  }

  private void resetLog() {
    this.username = null;
    this.lastUpdateDttmStr = null;
    this.status = true;
    this.lastLoginIp = null;
    this.result = null;
    this.comment = null;
    this.action = null;
  }

  public SysUser getLogById(boolean isAdmin, String username, @Param("id") String id) {
    return null;
  }

  public String getLogList(boolean isAdmin, String username, String param) {
    if (StringUtils.isBlank(username)) {
      return "SELECT 0";
    }
    StringBuffer sqlStrbuf = new StringBuffer();
    sqlStrbuf.append("SELECT * ");
    sqlStrbuf.append("FROM sys_operation_log ");
    sqlStrbuf.append("WHERE enable_flag = 1 ");
    if (!isAdmin) {
      sqlStrbuf.append("AND username = " + SqlUtils.preventSQLInjection(username));
    }
    if (StringUtils.isNotBlank(param)) {
      sqlStrbuf.append(" AND ");
      sqlStrbuf.append("( ");
      sqlStrbuf.append(
          "username like CONCAT('%'," + SqlUtils.preventSQLInjection(param) + ",'%') OR ");
      sqlStrbuf.append(
          "last_login_ip like CONCAT('%'," + SqlUtils.preventSQLInjection(param) + ",'%') OR ");
      sqlStrbuf.append(
          "last_update_dttm like CONCAT('%'," + SqlUtils.preventSQLInjection(param) + ",'%')");
      sqlStrbuf.append(") ");
    }
    String sqlStr = sqlStrbuf.toString();
    return sqlStr;
  }

  public String insertSelective(SysLog record) {
    if (null == record) {
      return "SELECT 0";
    }
    this.preventSQLInjectionLog(record);

    StringBuffer strBuf = new StringBuffer();
    strBuf.append("INSERT INTO sys_operation_log ");

    strBuf.append("( ");
    //        strBuf.append(SqlUtils.baseFieldName() + ", ");
    strBuf.append(
        "username, last_login_ip, action, status, result,comment,crt_dttm,last_update_dttm,enable_flag ");
    strBuf.append(") ");

    strBuf.append("values ");
    strBuf.append("(");
    strBuf.append(
        username
            + ","
            + lastLoginIp
            + ","
            + action
            + ","
            + status
            + ","
            + result
            + ","
            + comment
            + ","
            + ctrDttmStr
            + ","
            + lastUpdateDttmStr
            + ","
            + true);
    strBuf.append(")");
    this.resetLog();
    return strBuf.toString() + ";";
  }
}
