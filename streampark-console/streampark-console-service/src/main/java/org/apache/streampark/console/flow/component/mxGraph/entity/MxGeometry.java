package org.apache.streampark.console.flow.component.mxGraph.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Setter
@Getter
@Entity
@Table(name = "MX_GEOMETRY")
public class MxGeometry extends BaseHibernateModelUUIDNoCorpAgentId {
  /** */
  private static final long serialVersionUID = 1L;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_MX_CELL_ID")
  private MxCell mxCell;

  @Column(name = "MX_RELATIVE")
  private String relative;

  @Column(name = "MX_AS")
  private String as;

  @Column(name = "MX_X")
  private String x;

  @Column(name = "MX_Y")
  private String y;

  @Column(name = "MX_WIDTH")
  private String width;

  @Column(name = "MX_HEIGHT")
  private String height;
}
