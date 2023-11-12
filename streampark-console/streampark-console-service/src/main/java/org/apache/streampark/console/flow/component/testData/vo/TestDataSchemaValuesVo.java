package org.apache.streampark.console.flow.component.testData.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestDataSchemaValuesVo {

  private String id;
  private String fieldValue;
  private int dataRow;
  private TestDataSchemaVo testDataSchemaVo;
}
