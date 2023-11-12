package org.apache.streampark.console.flow.third.service;

import java.util.Map;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.third.vo.schedule.ThirdScheduleVo;

public interface ISchedule {

  public Map<String, Object> scheduleStart(
      Schedule schedule, Process process, ProcessGroup processGroup);

  public String scheduleStop(String scheduleId);

  public ThirdScheduleVo scheduleInfo(String scheduleId);
}
