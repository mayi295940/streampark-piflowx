package org.apache.streampark.console.flow.component.testData.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class TestDataVo {

  private String id;
  private String name;
  private String description;
  private Date crtDttm;
  private String crtDttmString;
  private Date lastUpdateDttm;
  private String lastUpdateDttmString;
  private List<TestDataSchemaVo> schemaVoList = new ArrayList<>();
}
