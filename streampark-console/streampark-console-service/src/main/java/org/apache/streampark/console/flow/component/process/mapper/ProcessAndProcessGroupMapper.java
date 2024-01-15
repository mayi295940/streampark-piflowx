package org.apache.streampark.console.flow.component.process.mapper;

import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessAndProcessGroupMapperProvider;
import org.apache.streampark.console.flow.component.process.vo.ProcessAndProcessGroupVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

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
