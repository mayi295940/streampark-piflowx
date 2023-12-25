package org.apache.streampark.console.flow.component.stopsComponent.service;

import org.apache.streampark.console.flow.component.stopsComponent.vo.StopGroupVo;
import java.util.List;

public interface IStopGroupService {

  List<StopGroupVo> getStopGroupAll(String engineType);

  /** Call getAllStops and Group to manage, and save the stop attribute information */
  void updateGroupAndStopsListByServer(String username, String flowId);

  /**
   * stopsComponentList
   *
   * @param username
   * @param isAdmin
   */
  String stopsComponentList(String username, boolean isAdmin);
}
