package org.apache.streampark.console.flow.component.stopsComponent.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.PortType;

/** Stop component table */
@Getter
@Setter
@Entity
@Table(name = "FLOW_STOPS_TEMPLATE")
public class StopsComponent extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;

  private String bundel;

  private String groups;

  private String owner;

  @Column(columnDefinition = "text(0) COMMENT 'description'")
  private String description;

  private String inports;

  @Enumerated(EnumType.STRING)
  private PortType inPortType;

  private String outports;

  @Enumerated(EnumType.STRING)
  private PortType outPortType;

  private String stopGroup;

  private Boolean isCustomized = false;

  private String visualizationType;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "stopsTemplate")
  @Where(clause = "enable_flag=1")
  private List<StopsComponentProperty> properties = new ArrayList<StopsComponentProperty>();

  //    @ManyToMany(mappedBy = "stopsTemplateList")
  @Transient private List<StopsComponentGroup> stopGroupList = new ArrayList<>();
}
