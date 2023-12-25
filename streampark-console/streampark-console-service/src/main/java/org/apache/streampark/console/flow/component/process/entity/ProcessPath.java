package org.apache.streampark.console.flow.component.process.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessPath extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Process process;
  private String from;
  private String outport;
  private String inport;
  private String to;
  private String pageId;
}
