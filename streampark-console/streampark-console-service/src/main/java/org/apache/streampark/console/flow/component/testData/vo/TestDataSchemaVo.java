package org.apache.streampark.console.flow.component.testData.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TestDataSchemaVo {

  private String id;
  private String fieldName;
  private String fieldType;
  private String fieldDescription;
  private int fieldSoft;
  private TestDataVo testDataVo;
  private List<TestDataSchemaValuesVo> schemaValuesVoList = new ArrayList<>();
}
