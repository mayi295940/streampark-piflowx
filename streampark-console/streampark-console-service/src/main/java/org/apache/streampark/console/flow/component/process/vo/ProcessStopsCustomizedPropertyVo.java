package org.apache.streampark.console.flow.component.process.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/** stop property */
@Getter
@Setter
public class ProcessStopsCustomizedPropertyVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String customValue;
  private String description;
  private String stopId;
}
