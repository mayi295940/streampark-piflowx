package org.apache.streampark.console.flow.component.testData.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Setter
@Getter
public class TestDataSchema extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String fieldName;
  private String fieldType;
  private String fieldDescription;
  private int fieldSoft;
  private TestData testData;
  private List<TestDataSchemaValues> schemaValuesList = new ArrayList<>();
}
