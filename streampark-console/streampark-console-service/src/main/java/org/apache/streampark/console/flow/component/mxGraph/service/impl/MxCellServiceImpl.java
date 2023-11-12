package org.apache.streampark.console.flow.component.mxGraph.service.impl;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.mapper.MxCellMapper;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxCellService;

@Service
public class MxCellServiceImpl implements IMxCellService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private MxCellMapper mxCellMapper;

  @Override
  public int deleteMxCellById(String username, String id) {
    return mxCellMapper.updateEnableFlagById(username, id);
  }

  @Override
  public MxCell getMeCellById(String id) {
    MxCell meCellById = mxCellMapper.getMeCellById(id);
    return meCellById;
  }
}
