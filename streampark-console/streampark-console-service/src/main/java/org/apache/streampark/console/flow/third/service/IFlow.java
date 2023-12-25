package org.apache.streampark.console.flow.third.service;

import java.util.Map;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdProgressVo;

public interface IFlow {

  Map<String, Object> startFlow(Process process, String checkpoint, RunModeType runModeType);

  String stopFlow(String appId);

  ThirdProgressVo getFlowProgress(String appId);

  String getFlowLog(String appId);

  String getCheckpoints(String appID);

  String getDebugData(String appID, String stopName, String portName);

  String getVisualizationData(String appID, String stopName, String visualizationType);

  ThirdFlowInfoVo getFlowInfo(String appid);

  void getProcessInfoAndSave(String appid) throws Exception;

  void processInfoAndSaveSync() throws Exception;

  String getTestDataPathUrl();
}
