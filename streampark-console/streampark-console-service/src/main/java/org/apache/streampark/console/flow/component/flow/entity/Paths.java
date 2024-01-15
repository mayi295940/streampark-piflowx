package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Paths extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Flow flow;
  private String from;
  private String outport;
  private String inport;
  private String to;
  private String pageId;
  private String filterCondition;
}
