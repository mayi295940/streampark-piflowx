package org.apache.streampark.console.flow.component.process.mapper;

import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessAndProcessGroupMapperProvider;
import org.apache.streampark.console.flow.component.process.vo.ProcessAndProcessGroupVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface ProcessAndProcessGroupMapper {

  /** query all TemplateDataSource */
  @SelectProvider(
      type = ProcessAndProcessGroupMapperProvider.class,
      method = "getProcessAndProcessGroupList")
  List<ProcessAndProcessGroupVo> getProcessAndProcessGroupList(String param);

  @SelectProvider(
      type = ProcessAndProcessGroupMapperProvider.class,
      method = "getProcessAndProcessGroupListByUser")
  List<ProcessAndProcessGroupVo> getProcessAndProcessGroupListByUser(String param, String username);

}
