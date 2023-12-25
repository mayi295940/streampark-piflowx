package org.apache.streampark.console.flow.third.service;

import org.apache.streampark.console.flow.third.vo.stop.StopsHubVo;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentVo;

import java.util.List;
import java.util.Map;

public interface IStop {
  /**
   * Call the group interface
   *
   * @return
   */
  public String[] getAllGroup();

  public String[] getAllStops();

  public Map<String, List<String>> getStopsListWithGroup(String engineType);

  public ThirdStopsComponentVo getStopInfo(String bundleStr);

  public String getStopsHubPath();

  public StopsHubVo mountStopsHub(String stopsHubName);

  public StopsHubVo unmountStopsHub(String stopsHubMountId);
}
