package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

/** stop property */
@Getter
@Setter
public class CustomizedProperty extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Stops stops;
  private String name;
  private String customValue;
  private String description;
}
