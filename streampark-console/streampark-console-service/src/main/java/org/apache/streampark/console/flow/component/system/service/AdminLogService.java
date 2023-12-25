package org.apache.streampark.console.flow.component.system.service;

import org.apache.streampark.console.flow.component.system.entity.SysLog;

public interface AdminLogService {

  /**
   * @param username username
   * @param isAdmin admin
   * @param offset offset
   * @param limit limit
   * @param param param
   * @return
   */
  public String getLogListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param);

  public void add(SysLog log);
}
