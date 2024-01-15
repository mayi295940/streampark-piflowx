package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class FlowGroup extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String engineType;
  private String description;
  private String pageId;
  private Boolean isExample = false;
  private MxGraphModel mxGraphModel;
  private List<Flow> flowList = new ArrayList<>();
  private List<FlowGroupPaths> flowGroupPathsList = new ArrayList<>();
  private FlowGroup flowGroup;
  private List<FlowGroup> flowGroupList = new ArrayList<>();
}
