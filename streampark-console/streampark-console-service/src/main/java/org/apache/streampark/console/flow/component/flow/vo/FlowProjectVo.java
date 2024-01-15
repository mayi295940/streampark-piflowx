package org.apache.streampark.console.flow.component.flow.vo;

import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FlowProjectVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String uuid;
  private String description;
  private Boolean isExample = false;
  private MxGraphModelVo mxGraphModelVo;
  private List<FlowVo> flowVoList = new ArrayList<>();
  private List<FlowGroupVo> flowGroupVoList = new ArrayList<>();
}
