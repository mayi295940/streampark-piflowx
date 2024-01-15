package org.apache.streampark.console.flow.component.template.mapper;

import org.apache.streampark.console.flow.component.template.mapper.provider.FlowGroupTemplateMapperProvider;
import org.apache.streampark.console.flow.component.template.vo.FlowGroupTemplateVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface FlowGroupTemplateMapper {

  @SelectProvider(
      type = FlowGroupTemplateMapperProvider.class,
      method = "getFlowGroupTemplateVoListPage")
  @Results({@Result(id = true, column = "id", property = "id")})
  List<FlowGroupTemplateVo> getFlowGroupTemplateVoListPage(
      String username, boolean isAdmin, String param);

  @SelectProvider(
      type = FlowGroupTemplateMapperProvider.class,
      method = "getFlowGroupTemplateVoById")
  @Results({@Result(id = true, column = "id", property = "id")})
  FlowGroupTemplateVo getFlowGroupTemplateVoById(String username, boolean isAdmin, String id);
}
