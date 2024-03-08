package org.apache.streampark.console.flow.component.flow.env;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SparkEnv {

  private String driverMemory = "1g";
  private String executorNumber = "1";
  private String executorMemory = "1g";
  private String executorCores = "1";

}
