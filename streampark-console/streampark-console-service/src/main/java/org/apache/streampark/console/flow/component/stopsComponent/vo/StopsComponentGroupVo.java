package org.apache.streampark.console.flow.component.stopsComponent.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Group name table */
@Setter
@Getter
public class StopsComponentGroupVo {

  private String id; // Group id
  private String groupName; // Group name

  private List<StopsComponentVo> stopsComponentVoList = new ArrayList<>();
}
