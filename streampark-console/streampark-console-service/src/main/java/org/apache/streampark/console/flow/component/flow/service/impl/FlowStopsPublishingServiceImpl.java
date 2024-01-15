package org.apache.streampark.console.flow.component.flow.service.impl;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.flow.domain.FlowStopsPublishingDomain;
import org.apache.streampark.console.flow.component.flow.domain.StopsDomain;
import org.apache.streampark.console.flow.component.flow.entity.FlowStopsPublishing;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.service.IFlowStopsPublishingService;
import org.apache.streampark.console.flow.component.flow.utils.FlowStopsPublishingUtils;
import org.apache.streampark.console.flow.component.flow.utils.StopsUtils;
import org.apache.streampark.console.flow.component.flow.vo.FlowStopsPublishingVo;
import org.apache.streampark.console.flow.component.flow.vo.StopsVo;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;

import org.apache.commons.lang3.StringUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowStopsPublishingServiceImpl implements IFlowStopsPublishingService {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private final Logger logger = LoggerUtil.getLogger();

  @Autowired private FlowStopsPublishingDomain flowStopsPublishingDomain;

  @Autowired private StopsComponentDomain stopsComponentDomain;

  @Autowired private StopsDomain stopsDomain;

  @Override
  public String addFlowStopsPublishing(String username, String name, String stopsIds) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(stopsIds)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("stopsIds"));
    }
    List<String> stopsIdList = new ArrayList<>(Arrays.asList(stopsIds.split(",")));
    String rtnStr = checkParam(name, stopsIdList, true, null);
    if (null != rtnStr) {
      return rtnStr;
    }
    try {
      unbindDatasource(stopsIdList, username);
    } catch (Exception e) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    FlowStopsPublishing flowStopsPublishing =
        FlowStopsPublishingUtils.flowStopsPublishingNewNoId(username);
    flowStopsPublishing.setPublishingId(UUIDUtils.getUUID32());
    flowStopsPublishing.setName(name);
    flowStopsPublishing.setStopsIds(stopsIdList);
    try {
      int affectedRows = flowStopsPublishingDomain.addFlowStopsPublishing(flowStopsPublishing);
      if (affectedRows <= 0) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ADD_ERROR_MSG());
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ADD_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.ADD_SUCCEEDED_MSG());
  }

  @Override
  public String updateFlowStopsPublishing(
      boolean isAdmin, String username, String publishingId, String name, String stopsIds) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(stopsIds)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("stopsIds"));
    }
    List<String> stopsIdList = new ArrayList<>(Arrays.asList(stopsIds.split(",")));
    String rtnStr = checkParam(name, stopsIdList, true, publishingId);
    if (null != rtnStr) {
      return rtnStr;
    }
    try {
      unbindDatasource(stopsIdList, username);
    } catch (Exception e) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    try {
      int affectedRows =
          flowStopsPublishingDomain.updateFlowStopsPublishing(
              isAdmin, username, publishingId, name, stopsIdList);
      if (affectedRows <= 0) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ADD_ERROR_MSG());
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPDATE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.UPDATE_SUCCEEDED_MSG());
  }

  private String checkParam(
      String name, List<String> stopsIdList, boolean isUpdate, String publishingId) {
    if (StringUtils.isBlank(name)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("name"));
    }
    if (null == stopsIdList || stopsIdList.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("stopsIds"));
    }
    if (isUpdate) {
      // Judge whether the name is duplicate
      List<String> publishingIds = flowStopsPublishingDomain.getPublishingIdsByPublishingName(name);
      if (null != publishingIds && publishingIds.size() > 0) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DUPLICATE_NAME_MSG(name));
      }
    } else {
      if (StringUtils.isBlank(publishingId)) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(
            MessageConfig.PARAM_IS_NULL_MSG("publishingId"));
      }
      // Judge whether the name is duplicate
      List<String> publishingIds = flowStopsPublishingDomain.getPublishingIdsByPublishingName(name);
      if (null != publishingIds) {
        if (publishingIds.size() > 1) {
          return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DUPLICATE_NAME_MSG(name));
        }
        if (publishingId.equals(publishingIds.get(0))) {
          return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DUPLICATE_NAME_MSG(name));
        }
      }
    }
    // Judge whether it is disabled
    List<String> disabledStopsNameList = stopsDomain.getDisabledStopsNameListByIds(stopsIdList);
    if (null != disabledStopsNameList && disabledStopsNameList.size() > 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          MessageConfig.STOP_DISABLED_MSG(
              disabledStopsNameList.toString().replace("[", "'").replace("]", "'")));
    }
    // Judge whether there are attributes
    List<String> cannotPublishedStopsNameList =
        stopsDomain.getCannotPublishedStopsNameByIds(stopsIdList);
    if (null != cannotPublishedStopsNameList && cannotPublishedStopsNameList.size() > 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          MessageConfig.STOP_HAS_NO_PROPERTY_MSG(
              cannotPublishedStopsNameList.toString().replace("[", "'").replace("]", "'")));
    }
    return null;
  }

  private void unbindDatasource(List<String> stopsIdList, String username) throws Exception {
    if (null == stopsIdList || stopsIdList.size() == 0) {
      return;
    }
    List<Stops> stopsBindDatasourceByIds = stopsDomain.getStopsBindDatasourceByIds(stopsIdList);
    for (Stops stops : stopsBindDatasourceByIds) {
      Stops stopsX =
          StopsUtils.PropertiesCopyDatasourceToStops(stops, stops.getDataSource(), username, false);
      if (null == stopsX) {
        continue;
      }
      stopsDomain.saveOrUpdate(stops);
    }
  }

  @Override
  public String getFlowStopsPublishingVo(String publishingId) {
    if (StringUtils.isBlank(publishingId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("publishingId"));
    }
    List<FlowStopsPublishingVo> flowStopsPublishingVoList =
        flowStopsPublishingDomain.getFlowStopsPublishingVoByPublishingId(publishingId);
    if (null == flowStopsPublishingVoList || flowStopsPublishingVoList.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    FlowStopsPublishingVo flowStopsPublishingVo = flowStopsPublishingVoList.get(0);
    if (null == flowStopsPublishingVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    Map<String, Object> rtnData = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    rtnData.put("publishingId", flowStopsPublishingVo.getPublishingId());
    rtnData.put("name", flowStopsPublishingVo.getName());
    StopsVo stopsVo = flowStopsPublishingVo.getStopsVo();
    if (null != stopsVo && null != stopsVo.getFlowVo()) {
      int stopsCounts = stopsDomain.getStopsCountsByFlowId(stopsVo.getFlowVo().getId());
      stopsVo.getFlowVo().setStopQuantity(stopsCounts);
      rtnData.put("flowVo", stopsVo.getFlowVo());
    }
    List<Object> stopsDataList = new ArrayList<>();
    Map<String, StopsComponent> stopsComponentMaps = new HashMap<>();
    for (FlowStopsPublishingVo flowStopsPublishingVoI : flowStopsPublishingVoList) {
      if (null == flowStopsPublishingVo || null == flowStopsPublishingVoI.getStopsVo()) {
        continue;
      }
      StopsVo stopsVoI = flowStopsPublishingVoI.getStopsVo();
      StopsComponent stopsComponent = stopsComponentMaps.get(stopsVoI.getBundle());
      if (null == stopsComponent) {
        stopsComponent = stopsComponentDomain.getStopsComponentByBundle(stopsVoI.getBundle());
      }
      stopsVoI = StopsUtils.stopComponentToStopsVo(stopsVoI, stopsComponent);
      stopsDataList.add(stopsVoI);
    }
    rtnData.put("stopsDataList", stopsDataList);
    return ReturnMapUtils.toFormatJson(rtnData);
  }

  @Override
  public String getFlowStopsPublishingListByFlowId(String username, String flowId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(flowId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_BY_ID_XXX_MSG(flowId));
    }
    List<FlowStopsPublishing> flowStopsPublishingList =
        flowStopsPublishingDomain.getFlowStopsPublishingListByFlowId(username, flowId);
    if (null == flowStopsPublishingList || flowStopsPublishingList.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    Map<String, Object> rtnData = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    List<Map<String, String>> publishingDataList = new ArrayList<>();
    Map<String, String> publishingData;
    for (FlowStopsPublishing flowStopsPublishing : flowStopsPublishingList) {
      if (null == flowStopsPublishing || null == flowStopsPublishing.getPublishingId()) {
        continue;
      }
      publishingData = new HashMap<>();
      publishingData.put("name", flowStopsPublishing.getName());
      publishingData.put("publishingId", flowStopsPublishing.getPublishingId());
      publishingDataList.add(publishingData);
    }
    rtnData.put("publishingDataList", publishingDataList);
    return ReturnMapUtils.toFormatJson(rtnData);
  }

  @Override
  public String getFlowStopsPublishingListPager(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Page<FlowStopsPublishing> page = PageHelper.startPage(offset, limit);
    flowStopsPublishingDomain.getFlowStopsPublishingList(username, isAdmin, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  @Override
  public String deleteFlowStopsPublishing(String username, String publishingId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(publishingId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          MessageConfig.NO_DATA_BY_ID_XXX_MSG(publishingId));
    }
    int affectedRows =
        flowStopsPublishingDomain.updateFlowStopsPublishingEnableFlagByPublishingId(
            username, publishingId);
    if (affectedRows <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DELETE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.DELETE_SUCCEEDED_MSG());
  }
}
