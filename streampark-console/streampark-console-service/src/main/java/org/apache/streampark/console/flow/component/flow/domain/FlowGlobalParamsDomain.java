package org.apache.streampark.console.flow.component.flow.domain;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.FlowGlobalParams;
import org.apache.streampark.console.flow.component.flow.mapper.FlowGlobalParamsMapper;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class FlowGlobalParamsDomain {

  private final FlowGlobalParamsMapper flowGlobalParamsMapper;

  @Autowired
  public FlowGlobalParamsDomain(FlowGlobalParamsMapper flowGlobalParamsMapper) {
    this.flowGlobalParamsMapper = flowGlobalParamsMapper;
  }

  public int saveOrUpdate(FlowGlobalParams globalParams) throws Exception {
    if (null == globalParams) {
      throw new Exception("save failed, flow is null");
    }
    if (StringUtils.isBlank(globalParams.getId())) {
      return addFlowGlobalParams(globalParams);
    }
    return updateFlowGlobalParams(globalParams);
  }

  public int addFlowGlobalParams(FlowGlobalParams globalParams) throws Exception {
    if (null == globalParams) {
      throw new Exception("save failed");
    }
    String id = globalParams.getId();
    if (StringUtils.isBlank(id)) {
      globalParams.setId(UUIDUtils.getUUID32());
    }
    int affectedRows = flowGlobalParamsMapper.addGlobalParams(globalParams);
    if (affectedRows <= 0) {
      throw new Exception("save failed");
    }
    return affectedRows;
  }

  public int updateFlowGlobalParams(FlowGlobalParams globalParams) throws Exception {
    if (null == globalParams) {
      return 0;
    }
    return flowGlobalParamsMapper.updateGlobalParams(globalParams);
  }

  public FlowGlobalParams getFlowGlobalParamsById(String username, boolean isAdmin, String id) {
    return flowGlobalParamsMapper.getGlobalParamsById(username, isAdmin, id);
  }

  public List<FlowGlobalParams> getFlowGlobalParamsByIds(String[] ids) {
    return flowGlobalParamsMapper.getFlowGlobalParamsByIds(ids);
  }

  public List<FlowGlobalParams> getFlowGlobalParamsListParam(
      String username, boolean isAdmin, String param) {
    return flowGlobalParamsMapper.getGlobalParamsListParam(username, isAdmin, param);
  }

  public Integer updateEnableFlagById(String username, String flowId, boolean enableFalg) {
    return flowGlobalParamsMapper.updateEnableFlagById(username, flowId, enableFalg);
  }
}
