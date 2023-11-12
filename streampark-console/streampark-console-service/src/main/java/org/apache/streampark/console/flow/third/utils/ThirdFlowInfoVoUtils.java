package org.apache.streampark.console.flow.third.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.streampark.console.flow.base.util.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.StopState;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopsVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoVo;

public class ThirdFlowInfoVoUtils {

  public static Process setProcess(Process process, ThirdFlowInfoVo thirdFlowInfoVo) {

    if (null != thirdFlowInfoVo && null != process) {
      Map<String, ThirdFlowInfoStopVo> stopsMap = new HashMap<>();
      List<ThirdFlowInfoStopsVo> stops = thirdFlowInfoVo.getStops();
      process.setLastUpdateUser("syncTask");
      process.setLastUpdateDttm(new Date());
      process.setProgress(thirdFlowInfoVo.getProgress());
      String thirdFlowInfoVoState = thirdFlowInfoVo.getState();
      if (StringUtils.isNotBlank(thirdFlowInfoVoState)) {
        ProcessState processState = null;
        if ("NEW".equals(thirdFlowInfoVoState) || "NEW_SAVING".equals(thirdFlowInfoVoState)) {
          processState = ProcessState.INIT;
        } else if ("RUNNING".equals(thirdFlowInfoVoState)) {
          processState = ProcessState.SUBMITTED;
        } else if ("FINISHED".equals(thirdFlowInfoVoState)) {
          processState = ProcessState.COMPLETED;
        } else {
          processState = ProcessState.selectGender(thirdFlowInfoVoState);
        }

        process.setState(processState);
      }
      // process.setName(thirdFlowInfoVo.getName());
      // process.setProcessId(thirdFlowInfoVo.getPid());
      process.setProcessId(thirdFlowInfoVo.getId());
      process.setStartTime(DateUtils.strCstToDate(thirdFlowInfoVo.getStartTime()));
      process.setEndTime(DateUtils.strCstToDate(thirdFlowInfoVo.getEndTime()));
      List<ProcessStop> processStopList = process.getProcessStopList();
      if (CollectionUtils.isNotEmpty(stops) && CollectionUtils.isNotEmpty(processStopList)) {
        for (ThirdFlowInfoStopsVo thirdFlowInfoStopsVo : stops) {
          if (null != thirdFlowInfoStopsVo) {
            ThirdFlowInfoStopVo stopVo = thirdFlowInfoStopsVo.getStop();
            if (null != stopVo) {
              stopsMap.put(stopVo.getName(), stopVo);
            }
          }
        }
        for (ProcessStop processStop : processStopList) {
          if (null != processStop) {
            ThirdFlowInfoStopVo stopVo = stopsMap.get(processStop.getName());
            if (null != stopVo) {
              processStop.setState(StopState.selectGender(stopVo.getState()));
              processStop.setStartTime(DateUtils.strCstToDate(stopVo.getStartTime()));
              processStop.setEndTime(DateUtils.strCstToDate(stopVo.getEndTime()));
            }
          }
        }
      }
    }
    return process;
  }
}
