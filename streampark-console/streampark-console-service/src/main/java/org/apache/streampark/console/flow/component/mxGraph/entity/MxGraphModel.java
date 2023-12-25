package org.apache.streampark.console.flow.component.mxGraph.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;

@Setter
@Getter
public class MxGraphModel extends BaseModelUUIDNoCorpAgentId {
  /** */
  private static final long serialVersionUID = 1L;

  private Flow flow;
  private FlowGroup flowGroup;
  private Process process;
  private ProcessGroup processGroup;
  private String dx;
  private String dy;
  private String grid;
  private String gridSize;
  private String guides;
  private String tooltips;
  private String connect;
  private String arrows;
  private String fold;
  private String page;
  private String pageScale;
  private String pageWidth;
  private String pageHeight;
  private String background;
  private List<MxCell> root = new ArrayList<>();
}
