package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.mapper.provider.AdminLogMapperProvider;
import org.apache.streampark.console.flow.component.system.vo.SysLogVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface AdminLogMapper {

  @SelectProvider(type = AdminLogMapperProvider.class, method = "getLogList")
  List<SysLogVo> getLogList(boolean isAdmin, String username, String param);

  @InsertProvider(type = AdminLogMapperProvider.class, method = "insertSelective")
  int insertSelective(SysLog record);
}
