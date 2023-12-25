package org.apache.streampark.console.flow.component.stopsComponent.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ComponentFileType;
import org.apache.streampark.console.flow.common.Eunm.StopsHubState;
import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Setter
@Getter
public class StopsHub extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String mountId;
  private String engineType;
  private String jarName;
  private String jarUrl;
  private StopsHubState status;
  private String bundles;
  private Boolean isPublishing;
  private ComponentFileType type; // component type:PYTHON/SCALA
  private String languageVersion; // the language version that the component depends on
}
