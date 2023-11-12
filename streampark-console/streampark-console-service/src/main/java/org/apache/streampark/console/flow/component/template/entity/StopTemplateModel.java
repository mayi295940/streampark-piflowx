package org.apache.streampark.console.flow.component.template.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.apache.streampark.console.flow.base.util.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;

@Getter
@Setter
@Entity
@Table(name = "STOPS_TEMPLATE")
public class StopTemplateModel implements Serializable {
  /** stop template */
  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "FK_TEMPLATE_ID")
  private FlowTemplate flowTemplate;

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  @GeneratedValue(generator = "idGenerator")
  @Column(length = 40)
  private String id;

  @Column(name = "page_id")
  private String pageId;

  private String name;

  private String bundel;

  private String owner;

  private String description;

  private String inports;

  @Column(nullable = false)
  private Boolean enableFlag = Boolean.TRUE;

  @Enumerated(EnumType.STRING)
  private PortType inPortType;

  private String outports;

  @Enumerated(EnumType.STRING)
  private PortType outPortType;

  private Boolean isCheckpoint;

  private String groups;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date crtDttm = new Date();

  @Version @Column private Long version;

  private String crtUser;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "stopsVo")
  private List<PropertyTemplateModel> properties = new ArrayList<PropertyTemplateModel>();

  public String getCrtDttmString() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return crtDttm != null ? sdf.format(crtDttm) : "";
  }
}
