package org.apache.streampark.console.flow.component.process.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessGroupPathVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private ProcessGroupVo processGroupVo;
  private String from;
  private String outport;
  private String inport;
  private String to;
  private String pageId;
}
