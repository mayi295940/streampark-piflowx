package org.apache.streampark.console.flow.component.stopsComponent.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyTemplateVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String stopsTemplate;

  private String name;

  private String displayName;

  private String description;

  private String defaultValue;

  private String allowableValues;

  private Boolean required;

  private Boolean sensitive;
}
