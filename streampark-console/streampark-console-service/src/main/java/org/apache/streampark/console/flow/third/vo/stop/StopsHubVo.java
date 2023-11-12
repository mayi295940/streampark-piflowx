package org.apache.streampark.console.flow.third.vo.stop;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopsHubVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String mountId;
  private List<ThirdStopsComponentVo> stops;
}
