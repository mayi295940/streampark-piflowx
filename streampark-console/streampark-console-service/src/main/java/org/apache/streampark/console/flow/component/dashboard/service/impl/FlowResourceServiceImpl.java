package org.apache.streampark.console.flow.component.dashboard.service.impl;

import org.apache.streampark.console.flow.component.dashboard.service.IFlowResourceService;
import org.apache.streampark.console.flow.third.service.IResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowResourceServiceImpl implements IFlowResourceService {

  private final IResource resourceImpl;

  @Autowired
  public FlowResourceServiceImpl(IResource resourceImpl) {
    this.resourceImpl = resourceImpl;
  }

  @Override
  public String getResourceInfo() {
    return resourceImpl.getResourceInfo();
  }
}
