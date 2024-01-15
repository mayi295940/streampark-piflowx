package org.apache.streampark.console.flow.component.template.mapper;

import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;
import org.apache.streampark.console.flow.component.template.mapper.provider.FlowTemplateMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface FlowTemplateMapper {

  @InsertProvider(type = FlowTemplateMapperProvider.class, method = "insertFlowTemplate")
  int insertFlowTemplate(FlowTemplate flowTemplate);

  @UpdateProvider(type = FlowTemplateMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String id, boolean enableFlag);

  @Select("select ft.* from flow_template ft where enable_flag and ft.id=#{id}")
  FlowTemplate getFlowTemplateById(@Param("id") String id);

  @SelectProvider(type = FlowTemplateMapperProvider.class, method = "getFlowTemplateList")
  List<FlowTemplate> getFlowTemplateList(String username, boolean isAdmin);

  @SelectProvider(type = FlowTemplateMapperProvider.class, method = "getFlowTemplateListByParam")
  List<FlowTemplate> getFlowTemplateListByParam(String username, boolean isAdmin, String param);
}
