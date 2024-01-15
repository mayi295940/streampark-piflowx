package org.apache.streampark.console.flow.third.service;

import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.third.vo.schedule.ThirdScheduleVo;

import java.util.Map;

public interface ISchedule {

  Map<String, Object> scheduleStart(Schedule schedule, Process process, ProcessGroup processGroup);

  String scheduleStop(String scheduleId);

  ThirdScheduleVo scheduleInfo(String scheduleId);
}
