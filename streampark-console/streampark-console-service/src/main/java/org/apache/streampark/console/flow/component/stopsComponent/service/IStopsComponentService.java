package org.apache.streampark.console.flow.component.stopsComponent.service;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;

public interface IStopsComponentService {

  public StopsComponent getStopsTemplateById(String id);

  public StopsComponent getStopsPropertyById(String id);

  /**
   * stopsList for isDataSource is true
   *
   * @return
   */
  public String getDataSourceStopList();

  /**
   * query from flow_stops_property_template
   *
   * @param stopsTemplateBundle
   * @return
   */
  public String getStopsComponentPropertyByStopsId(String stopsTemplateBundle);
}
