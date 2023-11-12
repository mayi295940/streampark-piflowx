package org.apache.streampark.console.flow.component.process.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessAndProcessGroupMapperProvider;
import org.apache.streampark.console.flow.component.process.vo.ProcessAndProcessGroupVo;

@Mapper
public interface ProcessAndProcessGroupMapper {

  /**
   * query all TemplateDataSource
   *
   * @return
   */
  @SelectProvider(
      type = ProcessAndProcessGroupMapperProvider.class,
      method = "getProcessAndProcessGroupList")
  public List<ProcessAndProcessGroupVo> getProcessAndProcessGroupList(String param);

  @SelectProvider(
      type = ProcessAndProcessGroupMapperProvider.class,
      method = "getProcessAndProcessGroupListByUser")
  public List<ProcessAndProcessGroupVo> getProcessAndProcessGroupListByUser(
      String param, String username);
}
