package org.apache.streampark.console.flow.third.vo.flowGroup;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdFlowInfoOutResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private ThirdFlowInfoResponse flow;
}
