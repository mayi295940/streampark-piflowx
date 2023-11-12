package org.apache.streampark.console.flow.component.flow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.base.util.JsonUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper;
import org.apache.streampark.console.flow.component.flow.mapper.FlowGroupPathsMapper;
import org.apache.streampark.console.flow.component.flow.mapper.FlowMapper;
import org.apache.streampark.console.flow.component.flow.service.IFlowGroupPathsService;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupPathsVo;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupVo;

@Service
public class FlowGroupPathsServiceImpl implements IFlowGroupPathsService {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  Logger logger = LoggerUtil.getLogger();

  @Resource private FlowGroupPathsMapper flowGroupPathsMapper;
  @Resource private FlowGroupMapper flowGroupMapper;
  @Resource private FlowMapper flowMapper;

  @Override
  public String queryPathInfoFlowGroup(String flowGroupId, String pageId) {
    Map<String, Object> rtnMap = new HashMap<String, Object>();
    rtnMap.put("code", 500);
    if (StringUtils.isBlank(flowGroupId) || StringUtils.isBlank(pageId)) {
      rtnMap.put("errorMsg", "The parameter'fid'or'id' is empty");
      logger.warn("The parameter'fid'or'id' is empty");
      return JsonUtils.toJsonNoException(rtnMap);
    }

    List<FlowGroupPaths> flowGroupPathsList =
        flowGroupPathsMapper.getFlowGroupPaths(flowGroupId, pageId, null, null);
    if (null == flowGroupPathsList
        || flowGroupPathsList.size() <= 0
        || null == flowGroupPathsList.get(0)) {
      rtnMap.put("errorMsg", "No'paths'information was queried");
      logger.warn("No'paths'information was queried");
      return JsonUtils.toJsonNoException(rtnMap);
    }
    FlowGroupPaths flowGroupPaths = flowGroupPathsList.get(0);
    String fromName = null;
    String toName = null;
    if (StringUtils.isNotBlank(flowGroupPaths.getFrom())
        && StringUtils.isNotBlank(flowGroupPaths.getTo())) {
      fromName = flowMapper.getFlowNameByPageId(flowGroupId, flowGroupPaths.getFrom());
      if (StringUtils.isBlank(fromName)) {
        fromName = flowGroupMapper.getFlowGroupNameByPageId(flowGroupId, flowGroupPaths.getFrom());
      }
      toName = flowMapper.getFlowNameByPageId(flowGroupId, flowGroupPaths.getTo());
      if (StringUtils.isBlank(toName)) {
        toName = flowGroupMapper.getFlowGroupNameByPageId(flowGroupId, flowGroupPaths.getTo());
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
    rtnMap.put("code", 200);
    rtnMap.put("queryInfo", flowGroupPathsVo);
    rtnMap.put("errorMsg", "Success");
    return JsonUtils.toJsonNoException(rtnMap);
  }
}
