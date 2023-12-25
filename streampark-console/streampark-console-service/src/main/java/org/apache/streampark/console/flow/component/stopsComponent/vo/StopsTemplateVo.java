package org.apache.streampark.console.flow.component.stopsComponent.vo;

import org.apache.streampark.console.flow.common.Eunm.PortType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopsTemplateVo implements Serializable {
  private static final long serialVersionUID = 1L;

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

  private List<PropertyTemplateVo> propertiesVo = new ArrayList<>();

  private String imageUrl;
}
