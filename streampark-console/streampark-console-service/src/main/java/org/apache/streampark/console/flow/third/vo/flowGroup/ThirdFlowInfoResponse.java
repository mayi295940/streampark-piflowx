package org.apache.streampark.console.flow.third.vo.flowGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdFlowInfoResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String pid;
  private String name;
  private String state;
  private String startTime;
  private String endTime;
  private String progress;

  List<ThirdFlowStopInfoOutResponse> stops = new ArrayList<>();
}
