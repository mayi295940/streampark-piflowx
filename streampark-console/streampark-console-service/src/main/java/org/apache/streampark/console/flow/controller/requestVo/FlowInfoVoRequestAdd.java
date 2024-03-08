package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowInfoVoRequestAdd implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "flow id")
  private String id;

  @ApiModelProperty(value = "flow name", required = true)
  private String name;

  @ApiModelProperty(value = "engine type", required = true)
  private String engineType;

  @ApiModelProperty(value = "runtime mode", required = false)
  private String runtimeMode;

  @ApiModelProperty(value = "flow description")
  private String description; // description

  @ApiModelProperty(value = "flow driverMemory", required = true)
  private String driverMemory;

  @ApiModelProperty(value = "flow executorNumber", required = true)
  private String executorNumber;

  @ApiModelProperty(value = "flow executorMemory", required = true)
  private String executorMemory;

  @ApiModelProperty(value = "flow executorCores", required = true)
  private String executorCores;

  @ApiModelProperty(value = "flow globalParams ids")
  private String[] globalParamsIds;
}
