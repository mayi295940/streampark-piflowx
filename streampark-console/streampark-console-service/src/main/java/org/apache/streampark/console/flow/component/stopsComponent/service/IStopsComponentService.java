package org.apache.streampark.console.flow.component.stopsComponent.service;

import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponent;

public interface IStopsComponentService {

  public StopsComponent getStopsTemplateById(String id);

  public StopsComponent getStopsPropertyById(String id);
}
