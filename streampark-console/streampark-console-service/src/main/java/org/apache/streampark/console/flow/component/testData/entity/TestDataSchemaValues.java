package org.apache.streampark.console.flow.component.testData.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Setter
@Getter
public class TestDataSchemaValues extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String fieldValue;
  private int dataRow;
  private TestData testData;
  private TestDataSchema testDataSchema;
}
