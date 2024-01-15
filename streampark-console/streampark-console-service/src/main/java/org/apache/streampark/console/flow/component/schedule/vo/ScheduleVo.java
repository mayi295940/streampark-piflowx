package org.apache.streampark.console.flow.component.schedule.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ScheduleVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private Date crtDttm;
  private String scheduleId;
  private String type;
  private ScheduleState status;
  private String cronExpression;
  private Date planStartTime;
  private Date planEndTime;
  private String scheduleProcessTemplateId;
  private String scheduleRunTemplateId;
  private String scheduleRunTemplateName;

  public String getCrtDttmStr() {
    return DateUtils.dateTimesToStr(this.crtDttm);
  }

  public String getPlanStartTimeStr() {
    return DateUtils.dateTimesToStr(this.planStartTime);
  }

  public void setPlanStartTimeStr(String planStartTimeStr) {
    this.planStartTime = DateUtils.strToTime(planStartTimeStr);
  }

  public String getPlanEndTimeStr() {
    return DateUtils.dateTimesToStr(this.planEndTime);
  }

  public void setPlanEndTimeStr(String planEndTimeStr) {
    this.planEndTime = DateUtils.strToTime(planEndTimeStr);
  }
}
