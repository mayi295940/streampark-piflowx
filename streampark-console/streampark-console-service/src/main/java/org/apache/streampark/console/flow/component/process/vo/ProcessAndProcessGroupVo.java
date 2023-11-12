package org.apache.streampark.console.flow.component.process.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessAndProcessGroupVo implements Serializable {

  private static final long serialVersionUID = 1L;
  private String id;
  private Date lastUpdateDttm;
  private Date crtDttm;
  private String appId;
  private String name;
  private String description;
  private Date startTime;
  private Date endTime;
  private String progress;
  private String state;
  private String parentProcessId;
  private String processType;
}
