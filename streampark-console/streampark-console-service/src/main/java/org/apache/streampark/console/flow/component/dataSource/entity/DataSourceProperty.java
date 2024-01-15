package org.apache.streampark.console.flow.component.dataSource.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSourceProperty extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private DataSource dataSource;

  private String name;
  private String value;
  private String description;
}
