package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowGlobalParams extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String type;
  private String content;
}
