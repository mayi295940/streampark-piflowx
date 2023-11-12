package org.apache.streampark.console.flow.component.system.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.SysRoleType;

@Getter
@Setter
@Entity
@Table(name = "SYS_MENU")
public class SysMenu extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(255) COMMENT 'menu name'")
  private String menuName;

  @Column(columnDefinition = "varchar(255) COMMENT 'menu url'")
  private String menuUrl;

  @Column(columnDefinition = "varchar(255) COMMENT 'menu parent'")
  private String menuParent;

  @Column(columnDefinition = "varchar(255) COMMENT 'jurisdiction'")
  @Enumerated(EnumType.STRING)
  private SysRoleType menuJurisdiction;

  @Column(columnDefinition = "varchar(1024) COMMENT 'description'")
  private String menuDescription;

  @Column(columnDefinition = "int(11) COMMENT 'menu sort'")
  private Integer menuSort = 9;
}
