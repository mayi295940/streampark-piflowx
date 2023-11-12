package org.apache.streampark.console.flow.component.mxGraph.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MxGeometryVo implements Serializable {
  /** */
  private static final long serialVersionUID = 1L;

  private String relative;

  private String as;

  private String x;

  private String y;

  private String width;

  private String height;
}
