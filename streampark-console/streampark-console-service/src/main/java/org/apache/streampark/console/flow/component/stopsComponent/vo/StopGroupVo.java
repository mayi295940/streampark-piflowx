package org.apache.streampark.console.flow.component.stopsComponent.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Group name table */
@Getter
@Setter
public class StopGroupVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String groupName; // Group name

  private List<StopsTemplateVo> stopsTemplateVoList = new ArrayList<>();
}
