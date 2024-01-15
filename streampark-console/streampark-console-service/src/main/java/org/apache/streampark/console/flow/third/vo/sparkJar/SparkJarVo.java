package org.apache.streampark.console.flow.third.vo.sparkJar;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SparkJarVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String mountId;
  // private List<ThirdStopsComponentVo> stops;

}
