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
@Table(name = "flow_template")
public class FlowTemplate extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(255) COMMENT 'source flow name'")
  private String sourceFlowName;

  @Column(columnDefinition = "varchar(255) COMMENT 'template type'")
  @Enumerated(EnumType.STRING)
  private TemplateType templateType;

  @Column(columnDefinition = "varchar(255) COMMENT 'template name'")
  private String name;

  @Column(columnDefinition = "varchar(1024) COMMENT 'description'")
  private String description;

  private String path;

  private String url;
}
