package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ApiModel(description = "testData")
public class TestDataVoRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "testData id")
  private String id;

  @ApiModelProperty(value = "testData name", required = true)
  private String name;

  @ApiModelProperty(value = "testData description")
  private String description;

  @ApiModelProperty(value = "schemaVoList")
  private List<TestDataSchemaVoRequest> schemaVoList = new ArrayList<>();
}
