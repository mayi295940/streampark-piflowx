package org.apache.streampark.console.flow.component.mxGraph.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MxCell extends BaseModelUUIDNoCorpAgentId {
  /** */
  private static final long serialVersionUID = 1L;

  private MxGraphModel mxGraphModel;
  private String pageId;
  private String parent;
  private String style;
  private String edge; // Line has
  private String source; // Line has
  private String target; // Line has
  private String value;
  private String vertex;
  private MxGeometry mxGeometry;
}
