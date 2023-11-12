package org.apache.streampark.console.flow.component.process.service.Impl;

import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.ReturnMapUtils;
import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper;
import org.apache.streampark.console.flow.component.process.service.IProcessStopService;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.component.process.vo.ProcessStopVo;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper;
import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponent;

@Service
public class ProcessStopServiceImpl implements IProcessStopService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private ProcessStopMapper processStopMapper;

  @Resource private StopsComponentMapper stopsComponentMapper;

  /**
   * Query processStop based on processId and pageId
   *
   * @param processId
   * @param pageId
   * @return
   */
  @Override
  public String getProcessStopVoByPageId(String processId, String pageId) {
    if (StringUtils.isAnyEmpty(processId, pageId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Parameter passed in incorrectly");
    }
    ProcessStop processStopByPageId =
        processStopMapper.getProcessStopByPageIdAndPageId(processId, pageId);
    if (null == processStopByPageId) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("process stop data is null");
    }
    ProcessStopVo processStopVo = ProcessUtils.processStopPoToVo(processStopByPageId);
    StopsComponent stopsComponentByBundle =
        stopsComponentMapper.getStopsComponentByBundle(processStopByPageId.getBundel());
    if (null != stopsComponentByBundle) {
      processStopVo.setVisualizationType(stopsComponentByBundle.getVisualizationType());
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("processStopVo", processStopVo);
  }
}
