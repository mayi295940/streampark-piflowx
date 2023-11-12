package org.apache.streampark.console.flow.component.mxGraph.service.impl;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.component.mxGraph.mapper.MxGeometryMapper;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxGraphService;

@Service
public class MxGraphServiceImpl implements IMxGraphService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private MxGeometryMapper mxGeometryMapper;

  @Override
  public int deleteMxGraphById(String username, String id) {
    return mxGeometryMapper.updateEnableFlagById(username, id);
  }
}
