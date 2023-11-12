package org.apache.streampark.console.flow.component.template.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.TemplateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "FLOW_GROUP_TEMPLATE")
public class FlowGroupTemplate extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(255) COMMENT 'source flow name'")
  private String flowGroupName;

  @Column(columnDefinition = "varchar(255) COMMENT 'template type'")
  @Enumerated(EnumType.STRING)
  private TemplateType templateType;

  private String name;

  @Column(name = "description", columnDefinition = "varchar(1024) COMMENT 'description'")
  private String description;

  private String path;
}
