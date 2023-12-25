package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowGroupInfoVoRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "flowGroup id")
  private String id;

  @ApiModelProperty(value = "flowGroup name", required = true)
  private String name;

  @ApiModelProperty(value = "flowGroup description")
  private String description;

  @ApiModelProperty(value = "flowGroup pageId")
  private String pageId;
}
