package org.apache.streampark.console.flow.component.stopsComponent.service;

import java.util.List;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopGroupVo;

public interface IStopGroupService {

  public List<StopGroupVo> getStopGroupAll();

  /** Call getAllStops and Group to manage, and save the stop attribute information */
  public void updateGroupAndStopsListByServer(String username);

  /**
   * stopsComponentList
   *
   * @param username
   * @param isAdmin
   */
  public String stopsComponentList(String username, boolean isAdmin);
}
