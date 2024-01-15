package org.apache.streampark.console.flow.component.template.domain;

import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;
import org.apache.streampark.console.flow.component.template.mapper.FlowTemplateMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class FlowTemplateDomain {

  private final FlowTemplateMapper flowTemplateMapper;

  @Autowired
  public FlowTemplateDomain(FlowTemplateMapper flowTemplateMapper) {
    this.flowTemplateMapper = flowTemplateMapper;
  }

  public int insertFlowTemplate(FlowTemplate flowTemplate) {
    return flowTemplateMapper.insertFlowTemplate(flowTemplate);
  }

  public int updateEnableFlagById(String id, boolean enableFlag) {
    return flowTemplateMapper.updateEnableFlagById(id, enableFlag);
  }

  public FlowTemplate getFlowTemplateById(String id) {
    return flowTemplateMapper.getFlowTemplateById(id);
  }

  public List<FlowTemplate> getFlowTemplateList(String username, boolean isAdmin) {
    return flowTemplateMapper.getFlowTemplateList(username, isAdmin);
  }

  public List<FlowTemplate> getFlowTemplateListByParam(
      String username, boolean isAdmin, String param) {
    return flowTemplateMapper.getFlowTemplateListByParam(username, isAdmin, param);
  }
}
