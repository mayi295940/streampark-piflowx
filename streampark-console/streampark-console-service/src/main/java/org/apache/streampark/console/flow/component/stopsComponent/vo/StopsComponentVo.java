package org.apache.streampark.console.flow.component.stopsComponent.vo;

import org.apache.streampark.console.flow.common.Eunm.PortType;
import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Getter
@Setter
public class StopsComponentVo {

  private String id;
  private String name;
  private String bundle;
  private String groups;
  private String owner;
  private String description;
  private String inports;
  private PortType inPortType;
  private String outports;
  private PortType outPortType;
  private String stopGroup;
  private Boolean isCustomized = false;
  private String visualizationType;
  private Boolean isShow = true;
  private Boolean isDataSource = false;
}
