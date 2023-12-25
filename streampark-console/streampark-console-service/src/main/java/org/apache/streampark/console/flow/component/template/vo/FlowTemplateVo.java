package org.apache.streampark.console.flow.component.template.vo;

import org.apache.streampark.console.flow.component.flow.vo.FlowVo;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowTemplateVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String description;
  private String value;
  private String path;
  private FlowVo flowVo;
}
