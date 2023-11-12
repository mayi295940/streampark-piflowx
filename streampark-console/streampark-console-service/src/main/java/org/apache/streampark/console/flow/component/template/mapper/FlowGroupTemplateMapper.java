package org.apache.streampark.console.flow.component.template.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.streampark.console.flow.component.template.mapper.provider.FlowGroupTemplateMapperProvider;
import org.apache.streampark.console.flow.component.template.vo.FlowGroupTemplateVo;
import org.springframework.data.repository.query.Param;

@Mapper
public interface FlowGroupTemplateMapper {

  @SelectProvider(
      type = FlowGroupTemplateMapperProvider.class,
      method = "getFlowGroupTemplateVoListPage")
  @Results({@Result(id = true, column = "id", property = "id")})
  public List<FlowGroupTemplateVo> getFlowGroupTemplateVoListPage(
      @Param("username") String username,
      @Param("isAdmin") boolean isAdmin,
      @Param("param") String param);

  @SelectProvider(
      type = FlowGroupTemplateMapperProvider.class,
      method = "getFlowGroupTemplateVoById")
  @Results({@Result(id = true, column = "id", property = "id")})
  public FlowGroupTemplateVo getFlowGroupTemplateVoById(
      @Param("username") String username,
      @Param("isAdmin") boolean isAdmin,
      @Param("id") String id);
}
