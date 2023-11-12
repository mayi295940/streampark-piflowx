package org.apache.streampark.console.flow.component.flow.service;

import org.apache.streampark.console.flow.component.flow.vo.StopsCustomizedPropertyVo;

public interface ICustomizedPropertyService {

  public String addStopCustomizedProperty(
      String username, StopsCustomizedPropertyVo stopsCustomizedPropertyVo);

  public String updateStopsCustomizedProperty(
      String username, StopsCustomizedPropertyVo stopsCustomizedPropertyVo);

  public String deleteStopsCustomizedProperty(String username, String customPropertyId);

  public String deleteRouterStopsCustomizedProperty(String username, String customPropertyId);

  public String getRouterStopsCustomizedProperty(String customPropertyId);
}
