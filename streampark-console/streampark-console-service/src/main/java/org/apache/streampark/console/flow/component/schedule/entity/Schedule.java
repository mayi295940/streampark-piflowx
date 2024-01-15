package org.apache.streampark.console.flow.component.schedule.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Schedule extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String scheduleId;
  private String type;
  private ScheduleState status;
  private String cronExpression;
  private Date planStartTime;
  private Date planEndTime;
  private String scheduleProcessTemplateId;
  private String scheduleRunTemplateId;
}
