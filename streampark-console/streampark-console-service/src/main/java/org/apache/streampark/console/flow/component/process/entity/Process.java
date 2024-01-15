package org.apache.streampark.console.flow.component.process.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ProcessParentType;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.flow.entity.FlowGlobalParams;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Process extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String engineType;
  private String driverMemory;
  private String executorNumber;
  private String executorMemory;
  private String executorCores;
  private String viewXml;
  private String description;
  private String pageId;
  private String flowId;
  private String appId;
  private String parentProcessId;
  private String processId;
  private ProcessState state;
  private Date startTime;
  private Date endTime;
  private String progress;
  private RunModeType runModeType = RunModeType.RUN;
  private ProcessParentType processParentType;
  private Schedule schedule;
  private ProcessGroup processGroup;
  private MxGraphModel mxGraphModel;
  private List<ProcessStop> processStopList = new ArrayList<>();
  private List<ProcessPath> processPathList = new ArrayList<>();
  List<FlowGlobalParams> flowGlobalParamsList;

  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }
}
