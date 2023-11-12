package org.apache.streampark.console.flow.third.vo.flowGroup;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ThirdFlowStopInfoResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String state;
  private String startTime;
  private String endTime;
}
