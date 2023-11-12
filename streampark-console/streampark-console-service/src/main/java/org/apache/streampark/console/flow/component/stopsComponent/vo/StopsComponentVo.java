package org.apache.streampark.console.flow.component.stopsComponent.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.common.Eunm.PortType;

/** Stop component table */
@Getter
@Setter
public class StopsComponentVo {

  private String id;
  private String name;
  private String bundel;
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
}
