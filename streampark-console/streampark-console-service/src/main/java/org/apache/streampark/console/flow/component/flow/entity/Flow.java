package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Flow extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String engineType;
  private String uuid;
  private String driverMemory = "1g";
  private String executorNumber = "1";
  private String executorMemory = "1g";
  private String executorCores = "1";
  private String description;
  private String pageId;
  private Boolean isExample = false;
  private FlowGroup flowGroup;
  private MxGraphModel mxGraphModel;
  private List<Stops> stopsList = new ArrayList<>();
  private List<Paths> pathsList = new ArrayList<>();
  List<FlowGlobalParams> flowGlobalParamsList;
  private List<DataSource> dataSourceList = new ArrayList<>();
}
