package org.apache.streampark.console.flow.component.system.mapper.provider;

import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.system.entity.SysRole;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

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
