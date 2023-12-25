package org.apache.streampark.console.flow.component.schedule.mapper;

import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.component.schedule.mapper.provider.ScheduleMapperProvider;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;

@Mapper
public interface ScheduleMapper {

  @InsertProvider(type = ScheduleMapperProvider.class, method = "insert")
  int insert(Schedule schedule);

  /**
   * update schedule
   *
   * @param schedule schedule
   */
  @UpdateProvider(type = ScheduleMapperProvider.class, method = "update")
  int update(Schedule schedule);

  @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleList")
  List<ScheduleVo> getScheduleVoList(boolean isAdmin, String username, String param);

  @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleById")
  ScheduleVo getScheduleVoById(boolean isAdmin, String username, String id);

  @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleById")
  Schedule getScheduleById(boolean isAdmin, String username, String id);

  @DeleteProvider(type = ScheduleMapperProvider.class, method = "delScheduleById")
  int delScheduleById(boolean isAdmin, String username, String id);

  @SelectProvider(type = ScheduleMapperProvider.class, method = "getScheduleIdListByStateRunning")
  List<ScheduleVo> getScheduleIdListByStateRunning(boolean isAdmin, String username);

  @SelectProvider(
      type = ScheduleMapperProvider.class,
      method = "getScheduleIdListByScheduleRunTemplateId")
  int getScheduleIdListByScheduleRunTemplateId(
      boolean isAdmin, String username, String scheduleRunTemplateId);
}
