package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** stop component table */
@Getter
@Setter
public class Stops extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Flow flow;
  private String name;
  private String engineType;
  private String bundle;
  private String groups;
  private String owner;
  private String description;
  private String inports;
  private PortType inPortType;
  private String outports;
  private PortType outPortType;
  private String pageId;
  private String state;
  private Date startTime;
  private Date stopTime;
  private Boolean isCheckpoint;
  private Boolean isCustomized = false;
  private DataSource dataSource;
  private List<Property> properties = new ArrayList<>();
  private List<Property> oldProperties = new ArrayList<>();
  private List<CustomizedProperty> customizedPropertyList = new ArrayList<>();
  private Boolean isDataSource = false;
  private Boolean isDisabled = false;

  public String getStartTimes() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return startTime != null ? sdf.format(startTime) : "";
  }

  public String getStopTimes() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return stopTime != null ? sdf.format(stopTime) : "";
  }
}
