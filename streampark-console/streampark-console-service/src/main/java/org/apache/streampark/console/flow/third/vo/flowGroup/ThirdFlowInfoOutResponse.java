package org.apache.streampark.console.flow.third.vo.flowGroup;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ThirdFlowInfoOutResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private ThirdFlowInfoResponse flow;
}
