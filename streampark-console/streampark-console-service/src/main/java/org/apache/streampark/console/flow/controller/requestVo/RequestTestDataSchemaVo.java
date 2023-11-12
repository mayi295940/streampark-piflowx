package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "testDataSchema")
public class RequestTestDataSchemaVo implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "testDataSchema id")
  private String id;

  @ApiModelProperty(value = "testDataSchema fieldName")
  private String fieldName;

  @ApiModelProperty(value = "testDataSchema fieldType")
  private String fieldType;

  @ApiModelProperty(value = "testDataSchema fieldDescription")
  private String fieldDescription;

  @ApiModelProperty(value = "testDataSchema fieldSoft", example = "0")
  private int fieldSoft;

  @ApiModelProperty(value = "delete or not")
  private boolean isDelete;
}
