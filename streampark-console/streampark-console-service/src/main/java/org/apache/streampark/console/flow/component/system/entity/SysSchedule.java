package org.apache.streampark.console.flow.component.system.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ScheduleRunResultType;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysSchedule extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String jobName;
  private String jobClass;
  private ScheduleState status;
  private ScheduleRunResultType lastRunResult;
  private String cronExpression;
}
