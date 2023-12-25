package org.apache.streampark.console.flow.component.mxGraph.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MxGeometry extends BaseModelUUIDNoCorpAgentId {
  /** */
  private static final long serialVersionUID = 1L;

  private MxCell mxCell;
  private String relative;
  private String as;
  private String x;
  private String y;
  private String width;
  private String height;
}
