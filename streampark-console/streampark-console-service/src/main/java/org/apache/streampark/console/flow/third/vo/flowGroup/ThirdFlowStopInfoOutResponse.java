package org.apache.streampark.console.flow.third.vo.flowGroup;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ThirdFlowStopInfoOutResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private ThirdFlowStopInfoResponse stop;
}
