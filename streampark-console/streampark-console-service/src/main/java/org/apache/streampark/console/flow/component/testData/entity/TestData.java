package org.apache.streampark.console.flow.component.testData.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Setter
@Getter
public class TestData extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private List<TestDataSchema> schemaList = new ArrayList<>();
  private List<TestDataSchemaValues> schemaValuesList = new ArrayList<>();
}
