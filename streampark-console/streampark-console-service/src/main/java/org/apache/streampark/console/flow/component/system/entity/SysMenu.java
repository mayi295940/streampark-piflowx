package org.apache.streampark.console.flow.component.system.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.SysRoleType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysMenu extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String menuName;
  private String menuUrl;
  private String menuParent;
  private SysRoleType menuJurisdiction;
  private String menuDescription;
  private Integer menuSort = 9;
}
