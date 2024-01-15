package org.apache.streampark.console.flow.component.template.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.TemplateType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowGroupTemplate extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String flowGroupName;
  private TemplateType templateType;
  private String name;
  private String description;
  private String path;
}
