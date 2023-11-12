package org.apache.streampark.console.flow.component.stopsComponent.service;

import org.apache.streampark.console.flow.controller.requestVo.UpdatestopsComponentIsShow;

public interface IStopsComponentManageService {

  /**
   * updateStopsComponentsIsShow
   *
   * @param username
   * @param isAdmin
   * @param stopsManage
   * @return
   * @throws Exception
   */
  public String updateStopsComponentIsShow(
      String username, boolean isAdmin, UpdatestopsComponentIsShow stopsManage) throws Exception;
}
