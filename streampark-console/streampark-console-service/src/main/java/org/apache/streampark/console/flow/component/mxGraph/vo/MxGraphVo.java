package org.apache.streampark.console.flow.component.mxGraph.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MxGraphVo implements Serializable {
  /** */
  private static final long serialVersionUID = 1L;

  private String loadId;

  private List<MxCellVo> mxCellVoList = new ArrayList<>();
}
