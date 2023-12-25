package org.apache.streampark.console.flow.component.dataSource.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourcePropertyVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String value;
  private String description;
}
