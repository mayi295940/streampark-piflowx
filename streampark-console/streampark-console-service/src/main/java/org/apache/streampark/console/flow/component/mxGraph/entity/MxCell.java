package org.apache.streampark.console.flow.component.mxGraph.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Entity
@Setter
@Getter
@Table(name = "MX_CELL")
public class MxCell extends BaseHibernateModelUUIDNoCorpAgentId {
  /** */
  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_MX_GRAPH_ID")
  private MxGraphModel mxGraphModel;

  @Column(name = "MX_PAGEID")
  private String pageId;

  @Column(name = "MX_PARENT")
  private String parent;

  @Column(name = "MX_STYLE")
  private String style;

  @Column(name = "MX_EDGE")
  private String edge; // Line has

  @Column(name = "MX_SOURCE")
  private String source; // Line has

  @Column(name = "MX_TARGET")
  private String target; // Line has

  @Column(name = "MX_VALUE")
  private String value;

  @Column(name = "MX_VERTEX")
  private String vertex;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "mxCell")
  @Where(clause = "enable_flag=1")
  private MxGeometry mxGeometry;
}
