package org.apache.streampark.console.flow.component.stopsComponent.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper;
import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsComponentService;

@Service
public class StopsComponentServiceImpl implements IStopsComponentService {

  @Resource private StopsComponentMapper stopsComponentMapper;

  @Override
  public StopsComponent getStopsTemplateById(String id) {
    return stopsComponentMapper.getStopsComponentById(id);
  }

  @Override
  public StopsComponent getStopsPropertyById(String id) {
    return stopsComponentMapper.getStopsComponentAndPropertyById(id);
  }
}
