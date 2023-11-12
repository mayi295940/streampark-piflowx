package org.apache.streampark.console.flow.component.template.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowTemplateModelVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String description;
}
