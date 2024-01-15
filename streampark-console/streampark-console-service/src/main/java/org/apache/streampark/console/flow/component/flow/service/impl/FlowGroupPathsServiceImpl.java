package org.apache.streampark.console.flow.component.flow.service.impl;

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.flow.domain.FlowDomain;
import org.apache.streampark.console.flow.component.flow.domain.FlowGroupDomain;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.service.IFlowGroupPathsService;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupPathsVo;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowGroupPathsServiceImpl implements IFlowGroupPathsService {

  private final FlowGroupDomain flowGroupDomain;
  private final FlowDomain flowDomain;

  @Autowired
  public FlowGroupPathsServiceImpl(FlowGroupDomain flowGroupDomain, FlowDomain flowDomain) {
    this.flowGroupDomain = flowGroupDomain;
    this.flowDomain = flowDomain;
  }

  @Override
  public String queryPathInfoFlowGroup(String flowGroupId, String pageId) {
    if (StringUtils.isBlank(flowGroupId) || StringUtils.isBlank(pageId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    List<FlowGroupPaths> flowGroupPathsList =
        flowGroupDomain.getFlowGroupPaths(flowGroupId, pageId, null, null);
    if (null == flowGroupPathsList
        || flowGroupPathsList.size() <= 0
        || null == flowGroupPathsList.get(0)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_PATH_DATA_MSG());
    }
    FlowGroupPaths flowGroupPaths = flowGroupPathsList.get(0);
    String fromName = null;
    String toName = null;
    if (StringUtils.isNotBlank(flowGroupPaths.getFrom())
        && StringUtils.isNotBlank(flowGroupPaths.getTo())) {
      fromName = flowDomain.getFlowNameByPageId(flowGroupId, flowGroupPaths.getFrom());
      if (StringUtils.isBlank(fromName)) {
        fromName = flowGroupDomain.getFlowGroupNameByPageId(flowGroupId, flowGroupPaths.getFrom());
      }
      toName = flowDomain.getFlowNameByPageId(flowGroupId, flowGroupPaths.getTo());
      if (StringUtils.isBlank(toName)) {
        toName = flowGroupDomain.getFlowGroupNameByPageId(flowGroupId, flowGroupPaths.getTo());
      }
    }
    FlowGroupPathsVo flowGroupPathsVo = new FlowGroupPathsVo();
    BeanUtils.copyProperties(flowGroupPaths, flowGroupPathsVo);
    FlowGroup flowGroup = flowGroupPaths.getFlowGroup();
    if (null != flowGroup) {
      FlowGroupVo flowGroupVo = new FlowGroupVo();
      BeanUtils.copyProperties(flowGroup, flowGroupVo);
      flowGroupPathsVo.setFlowGroupVo(flowGroupVo);
    }
    flowGroupPathsVo.setFlowFrom(fromName);
    flowGroupPathsVo.setFlowTo(toName);
    if (StringUtils.isBlank(flowGroupPathsVo.getInport())) {
      flowGroupPathsVo.setInport("default");
    }
    if (StringUtils.isBlank(flowGroupPathsVo.getOutport())) {
      flowGroupPathsVo.setOutport("default");
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("queryInfo", flowGroupPathsVo);
  }
}
