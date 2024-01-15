package org.apache.streampark.console.flow.component.stopsComponent.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Getter
@Setter
public class StopsComponentManage extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String bundle;

  private String stopsGroups;

  private Boolean isShow = true;
}
