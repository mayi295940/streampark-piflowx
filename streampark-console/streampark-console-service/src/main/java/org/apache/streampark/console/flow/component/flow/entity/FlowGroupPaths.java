package org.apache.streampark.console.flow.component.flow.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Entity
@Table(name = "FLOW_GROUP_PATH")
@Setter
@Getter
public class FlowGroupPaths extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_FLOW_GROUP_ID")
  private FlowGroup flowGroup;

  @Column(name = "LINE_FROM", columnDefinition = "varchar(255) COMMENT 'line from'")
  private String from;

  @Column(name = "LINE_OUTPORT", columnDefinition = "varchar(255) COMMENT 'line out port'")
  private String outport;

  @Column(name = "LINE_INPORT", columnDefinition = "varchar(255) COMMENT 'line in port'")
  private String inport;

  @Column(name = "LINE_TO", columnDefinition = "varchar(255) COMMENT 'line to'")
  private String to;

  @Column(name = "page_id")
  private String pageId;

  @Column(name = "filter_condition")
  private String filterCondition;
}
