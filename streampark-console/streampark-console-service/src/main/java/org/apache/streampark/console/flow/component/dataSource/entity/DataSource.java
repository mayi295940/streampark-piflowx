package org.apache.streampark.console.flow.component.dataSource.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DataSource extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String dataSourceType;
  private String dataSourceName;
  private String dataSourceDescription;
  private Boolean isTemplate = false;
  private List<Stops> stopsList = new ArrayList<>();
  private List<DataSourceProperty> dataSourcePropertyList = new ArrayList<>();
  private String stopsTemplateBundle;
  private StopsComponent stopsComponent;
  private Boolean isAvailable = true;
  private String imageUrl;
}
