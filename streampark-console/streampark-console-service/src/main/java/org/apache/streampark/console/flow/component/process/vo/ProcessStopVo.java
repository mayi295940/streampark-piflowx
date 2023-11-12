package org.apache.streampark.console.flow.component.process.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.util.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;

@Getter
@Setter
public class ProcessStopVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private ProcessVo processVo;
  private String name;
  private String bundel;
  private String groups;
  private String owner;
  private String description;
  private String inports;
  private PortType inPortType;
  private String outports;
  private PortType outPortType;
  private String state;
  private Date startTime;
  private Date endTime;
  private String pageId;
  private String visualizationType;
  private List<ProcessStopPropertyVo> processStopPropertyVoList =
      new ArrayList<ProcessStopPropertyVo>();

  public String getStartTimeStr() {
    return DateUtils.dateTimesToStr(this.startTime);
  }

  public String getEndTimeStr() {
    return DateUtils.dateTimesToStr(this.endTime);
  }
}
