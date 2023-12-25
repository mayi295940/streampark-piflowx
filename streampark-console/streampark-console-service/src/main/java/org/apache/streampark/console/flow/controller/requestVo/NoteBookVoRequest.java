package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "noteBook")
public class NoteBookVoRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "noteBook id")
  private String id;

  @ApiModelProperty(value = "noteBook name", required = true)
  private String name;

  @ApiModelProperty(value = "noteBook description")
  private String description;

  @ApiModelProperty(value = "noteBook code type", required = true)
  private String codeType;

  @ApiModelProperty(value = "noteBook sessions id")
  private String sessionsId;
}
