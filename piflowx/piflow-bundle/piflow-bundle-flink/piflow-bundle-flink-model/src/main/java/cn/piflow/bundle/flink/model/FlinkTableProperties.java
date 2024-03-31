package cn.piflow.bundle.flink.model;

import java.util.List;

public class FlinkTableProperties {

  private List<CustomProperty> customPropertyList;

  public List<CustomProperty> getCustomPropertyList() {
    return customPropertyList;
  }

  public void setCustomPropertyList(List<CustomProperty> customPropertyList) {
    this.customPropertyList = customPropertyList;
  }
}
