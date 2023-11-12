package org.apache.streampark.console.flow.component.dashboard.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.component.dashboard.service.IFlowResourceService;
import org.apache.streampark.console.flow.third.service.IResource;

@Service
public class FlowResourceServiceImpl implements IFlowResourceService {

  @Resource private IResource flowResourceImpl;

  @Override
  public String getResourceInfo() {
      return flowResourceImpl.getResourceInfo();
  }
}
