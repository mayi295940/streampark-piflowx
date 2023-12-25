package org.apache.streampark.console.flow.component.stopsComponent.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StopsComponentProperty extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String stopsTemplate;
  private String name;
  private String displayName;
  private String description;
  private String defaultValue;
  private String allowableValues;
  private Boolean required;
  private Boolean sensitive;
  private Long propertySort;
  private String example;
  private String language;
}
