package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.common.Eunm.ScheduleState;
import org.apache.streampark.console.flow.component.system.entity.SysSchedule;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysScheduleMapperProvider;
import org.apache.streampark.console.flow.component.system.vo.SysScheduleVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface SysScheduleMapper {

  @InsertProvider(type = SysScheduleMapperProvider.class, method = "insert")
  int insert(SysSchedule sysSchedule);

  @InsertProvider(type = SysScheduleMapperProvider.class, method = "update")
  int update(SysSchedule sysSchedule);

  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
  SysSchedule getSysScheduleById(boolean isAdmin, String id);

  /**
   * getSysScheduleListByStatus
   *
   * @param isAdmin isAdmin
   * @param status status
   */
  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleListByStatus")
  List<SysSchedule> getSysScheduleListByStatus(boolean isAdmin, ScheduleState status);

  /**
   * getSysScheduleList
   *
   * @param param param
   */
  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleList")
  List<SysScheduleVo> getSysScheduleList(boolean isAdmin, String param);

  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
  SysScheduleVo getSysScheduleVoById(boolean isAdmin, String id);

}
