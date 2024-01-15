package org.apache.streampark.console.flow.component.stopsComponent.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

// TODO
@Setter
@Getter
public class StopsHubInfoVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String stopHubId;
  private String stopBundle;
  private String stopName;
  private String engineType;

  private String groups;
  private String bundleDescription;
  private String inports;
  private String outports;
  private String owner;
  private String imageUrl;
  private List<StopsComponentPropertyVo> properties;

  // python component properties
  private Boolean isPythonComponent = false;
  private Boolean isHaveParams = false;
}
