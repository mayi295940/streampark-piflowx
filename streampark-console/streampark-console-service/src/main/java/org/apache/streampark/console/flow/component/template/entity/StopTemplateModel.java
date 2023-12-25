package org.apache.streampark.console.flow.component.template.entity;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopTemplateModel implements Serializable {
  /** stop template */
  private static final long serialVersionUID = 1L;

  private FlowTemplate flowTemplate;

  private String id;
  private String pageId;
  private String name;
  private String bundle;
  private String owner;
  private String description;
  private String inports;
  private Boolean enableFlag = Boolean.TRUE;
  private PortType inPortType;
  private String outports;
  private PortType outPortType;
  private Boolean isCheckpoint;
  private String groups;
  private Date crtDttm = new Date();
  private Long version;
  private String crtUser;

  private List<PropertyTemplateModel> properties = new ArrayList<>();

  public String getCrtDttmString() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return crtDttm != null ? sdf.format(crtDttm) : "";
  }
}
