package org.apache.streampark.console.flow.component.schedule.domain;

import java.util.List;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.component.schedule.mapper.ScheduleMapper;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class ScheduleDomain {

  private final ScheduleMapper scheduleMapper;

  @Autowired
  public ScheduleDomain(ScheduleMapper scheduleMapper) {
    this.scheduleMapper = scheduleMapper;
  }

  public int insert(Schedule schedule) {
    return scheduleMapper.insert(schedule);
  }

  public int update(Schedule schedule) {
    return scheduleMapper.update(schedule);
  }

  public List<ScheduleVo> getScheduleVoList(boolean isAdmin, String username, String param) {
    return scheduleMapper.getScheduleVoList(isAdmin, username, param);
  }

  public ScheduleVo getScheduleVoById(boolean isAdmin, String username, String id) {
    return scheduleMapper.getScheduleVoById(isAdmin, username, id);
  }

  public Schedule getScheduleById(boolean isAdmin, String username, String id) {
    return scheduleMapper.getScheduleById(isAdmin, username, id);
  }

  public int delScheduleById(boolean isAdmin, String username, String id) {
    return scheduleMapper.delScheduleById(isAdmin, username, id);
  }

  public List<ScheduleVo> getScheduleIdListByStateRunning(boolean isAdmin, String username) {
    return scheduleMapper.getScheduleIdListByStateRunning(isAdmin, username);
  }

  public int getScheduleIdListByScheduleRunTemplateId(
      boolean isAdmin, String username, String scheduleRunTemplateId) {
    return scheduleMapper.getScheduleIdListByScheduleRunTemplateId(
        isAdmin, username, scheduleRunTemplateId);
  }
}
