package org.apache.streampark.console.flow.component.stopsComponent.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class PublishComponentVo implements Serializable {
  private static final long serialVersionUID = 1L;
  // fill when update component market
  private String id;
  private String name;
  // base64
  private String logo;
  private String description;
  private String category;
  private String bundle;
  private String authorName;
  private List<String> software;
  // front-end/back-end/algorithm
  private String componentType;
  private List<String> dependencies;
  private List<Map<String, String>> parameters;
}
