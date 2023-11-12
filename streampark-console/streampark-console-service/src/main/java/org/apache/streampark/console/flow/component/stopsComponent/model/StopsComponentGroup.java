package org.apache.streampark.console.flow.component.stopsComponent.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

/** Group name table */
@Setter
@Getter
@Entity
@Table(name = "FLOW_STOPS_GROUPS")
public class StopsComponentGroup extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String groupName; // Group name

  // Group contains stop
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "association_groups_stops_template",
      joinColumns = @JoinColumn(name = "groups_id"),
      inverseJoinColumns = @JoinColumn(name = "stops_template_id"))
  @Where(clause = "enable_flag=1")
  private List<StopsComponent> stopsComponentList = new ArrayList<StopsComponent>();
}
