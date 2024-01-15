package org.apache.streampark.console.flow.component.testData.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TestData extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private List<TestDataSchema> schemaList = new ArrayList<>();
  private List<TestDataSchemaValues> schemaValuesList = new ArrayList<>();
}
