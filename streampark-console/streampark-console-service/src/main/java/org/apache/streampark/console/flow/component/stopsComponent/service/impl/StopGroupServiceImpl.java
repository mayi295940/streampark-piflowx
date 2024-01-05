package org.apache.streampark.console.flow.component.stopsComponent.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.ComponentFileType;
import org.apache.streampark.console.flow.component.dataSource.domain.DataSourceDomain;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.mapper.FlowMapper;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentGroupDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopGroupService;
import org.apache.streampark.console.flow.component.stopsComponent.utils.StopsComponentGroupUtils;
import org.apache.streampark.console.flow.component.stopsComponent.utils.StopsComponentUtils;
import org.apache.streampark.console.flow.component.stopsComponent.vo.PropertyTemplateVo;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopGroupVo;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsComponentGroupVo;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsComponentVo;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsTemplateVo;
import org.apache.streampark.console.flow.third.service.IStop;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentVo;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class StopGroupServiceImpl implements IStopGroupService {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private final Logger logger = LoggerUtil.getLogger();

  private final IStop stopImpl;
  private final StopsComponentDomain stopsComponentDomain;
  private final StopsComponentGroupDomain stopsComponentGroupDomain;
  private final DataSourceDomain dataSourceDomain;
  private final FlowMapper flowMapper;

  @Autowired
  public StopGroupServiceImpl(
      IStop stopImpl,
      StopsComponentDomain stopsComponentDomain,
      StopsComponentGroupDomain stopsComponentGroupDomain,
      DataSourceDomain dataSourceDomain,
      FlowMapper flowMapper) {
    this.stopImpl = stopImpl;
    this.stopsComponentDomain = stopsComponentDomain;
    this.stopsComponentGroupDomain = stopsComponentGroupDomain;
    this.dataSourceDomain = dataSourceDomain;
    this.flowMapper = flowMapper;
  }

  /**
   * Query all groups and all stops under it
   *
   * @return StopGroupVo list
   */
  @Override
  public List<StopGroupVo> getStopGroupAll(String engineType) {
    List<StopsComponentGroup> stopGroupList = stopsComponentDomain.getStopGroupList(engineType);
    if (CollectionUtils.isEmpty(stopGroupList)) {
      return null;
    }
    List<StopGroupVo> stopGroupVoList = new ArrayList<>();
    for (StopsComponentGroup stopGroup : stopGroupList) {
      if (null == stopGroup) {
        continue;
      }
      List<StopsComponent> stopsComponentList = stopGroup.getStopsComponentList();
      if (null == stopsComponentList || stopsComponentList.size() == 0) {
        continue;
      }
      StopGroupVo stopGroupVo = new StopGroupVo();
      BeanUtils.copyProperties(stopGroup, stopGroupVo);

      List<StopsTemplateVo> stopsTemplateVoList = new ArrayList<>();
      for (StopsComponent stopsComponent : stopsComponentList) {
        if (null == stopsComponent) {
          continue;
        }
        StopsTemplateVo stopsTemplateVo = new StopsTemplateVo();
        BeanUtils.copyProperties(stopsComponent, stopsTemplateVo);
        List<StopsComponentProperty> properties = stopsComponent.getProperties();
        if (null != properties && properties.size() > 0) {
          List<PropertyTemplateVo> propertiesVo = new ArrayList<>();
          for (StopsComponentProperty stopsComponentProperty : properties) {
            PropertyTemplateVo propertyTemplateVo = new PropertyTemplateVo();
            BeanUtils.copyProperties(stopsComponentProperty, propertyTemplateVo);
            propertiesVo.add(propertyTemplateVo);
          }
          stopsTemplateVo.setPropertiesVo(propertiesVo);
        }
        stopsTemplateVoList.add(stopsTemplateVo);
      }
      stopGroupVo.setStopsTemplateVoList(stopsTemplateVoList);

      stopGroupVoList.add(stopGroupVo);
    }
    return stopGroupVoList;
  }

  @Override
  public void updateGroupAndStopsListByServer(String username, String flowId) {
    // get all default and scala components

    String engineType = "";
    Flow flow = flowMapper.getFlowById(flowId);
    if (flow != null) {
      engineType = flow.getEngineType();
    }

    if (StringUtils.isEmpty(engineType)) {
      throw new IllegalArgumentException("engine type is empty");
    }

    Map<String, List<String>> stopsListWithGroup = stopImpl.getStopsListWithGroup(engineType);
    if (null == stopsListWithGroup || stopsListWithGroup.isEmpty()) {
      return;
    }

    /*1.get all default components and delete*/
    List<StopsComponent> systemDefaultStops =
        stopsComponentDomain.getSystemDefaultStops(engineType);
    if (systemDefaultStops != null && systemDefaultStops.size() > 0) {
      for (StopsComponent systemDefaultStop : systemDefaultStops) {
        stopsComponentDomain.deleteStopsComponent(systemDefaultStop);
      }
    }

    int addStopsComponentGroupRows = 0;

    // StopsComponent bundle list
    List<String> stopsBundleList = new ArrayList<>();

    // Loop stopsListWithGroup
    // compare server groups and old groups
    Map<String, StopsComponentGroup> stopsComponentGroupMap = new HashMap<>();

    // get old groups
    List<StopsComponentGroup> stopsComponentGroupList =
        stopsComponentDomain.getStopGroupByGroupNameList(
            new ArrayList<>(stopsListWithGroup.keySet()), engineType);

    for (StopsComponentGroup sGroup : stopsComponentGroupList) {
      stopsComponentGroupMap.put(sGroup.getGroupName(), sGroup);
    }

    for (String groupName : stopsListWithGroup.keySet()) {
      if (StringUtils.isBlank(groupName)) {
        continue;
      }
      StopsComponentGroup stopsComponentGroup = stopsComponentGroupMap.get(groupName);
      // insert new group
      if (stopsComponentGroup == null) {
        stopsComponentGroup = StopsComponentGroupUtils.stopsComponentGroupNewNoId(username);
        stopsComponentGroup.setId(UUIDUtils.getUUID32());
        stopsComponentGroup.setGroupName(groupName);
        stopsComponentGroup.setEngineType(engineType);
        addStopsComponentGroupRows +=
            stopsComponentDomain.addStopsComponentGroup(stopsComponentGroup);
      }
      // add group info
      // get current group stops bundle list
      List<String> list = stopsListWithGroup.get(groupName);
      stopsBundleList.addAll(list);
    }

    logger.debug("Successful insert Group" + addStopsComponentGroupRows + "piece of data!!!");

    // Determine if it is empty
    if (stopsBundleList.isEmpty()) {
      return;
    }

    // Deduplication
    HashSet<String> stopsBundleListDeduplication = new HashSet<>(stopsBundleList);
    stopsBundleList.clear();
    stopsBundleList.addAll(stopsBundleListDeduplication);

    int updateStopsComponentNum = 0;
    for (String bundle : stopsBundleList) {

      if (StringUtils.isBlank(bundle)) {
        continue;
      }

      // 2.First query "stopInfo" according to "bundle"
      logger.info("Now the call is：" + bundle);

      StopsComponent ifExistStop = stopsComponentDomain.getOnlyStopsComponentByBundle(bundle);

      if (ifExistStop == null) {
        // new default component,insert
        ThirdStopsComponentVo thirdStopsComponentVo = stopImpl.getStopInfo(bundle);
        if (null == thirdStopsComponentVo) {
          logger.warn("bundle:" + bundle + " is not data");
          continue;
        }

        List<String> list = Arrays.asList(thirdStopsComponentVo.getGroups().split(","));

        // Query group information according to groupName in stops
        List<StopsComponentGroup> stopGroupByName =
            stopsComponentDomain.getStopGroupByNameList(
                list, thirdStopsComponentVo.getEngineType());

        StopsComponent stopsComponent =
            StopsComponentUtils.thirdStopsComponentVoToStopsTemplate(
                username, thirdStopsComponentVo, stopGroupByName);
        if (null == stopsComponent) {
          continue;
        }
        stopsComponent.setComponentType(ComponentFileType.DEFAULT);
        stopsComponentDomain.addStopsComponentAndChildren(stopsComponent);
        logger.debug("========association_groups_stops_template=====start============");
        stopsComponentDomain.stopsComponentLinkStopsComponentGroupList(
            stopsComponent, stopsComponent.getStopGroupList());
        updateStopsComponentNum++;
      } else {
        logger.info("component exists,just skip,bundle is {} method is reload stops", bundle);
      }
    }
    //        //查询出所有dataSource中不为空且去重后的stops_template_bundle
    //        List<String> dataSourceStopTemplateBundleList =
    // dataSourceDomain.getAllStopDataSourceBundle();
    //        if (dataSourceStopTemplateBundleList != null &&
    // dataSourceStopTemplateBundleList.size() >0 ){
    //            for (String stopsTemplateBundle:dataSourceStopTemplateBundleList){
    //                //1.根据stopsTemplateBundle查询stop组件(返回的只有stopsComponent表中字段,无其他任何多余字段)
    //                StopsComponent stopsComponent =
    // stopsComponentDomain.getOnlyStopsComponentByBundle(stopsTemplateBundle);
    //                //2. 如果返回值不为空且为数据源组件,那么把对应stopDataSource全部改为可用
    //                if (stopsComponent != null && stopsComponent.getIsDataSource()) {
    //
    // dataSourceDomain.updateDataSourceIsAvailableByBundle(stopsTemplateBundle,1);
    //                    //修改一次图片地址：这里之所以没在上面的修改是否可用中修改图片地址,
    //                    // 单独写个方法,虽然会多一次连接,为了和修改是否可用功能分开
    //
    // dataSourceDomain.updateDataSourceImageUrlByBundle(stopsTemplateBundle,stopsComponent.getImageUrl());
    //                } else {
    //                    //3.反之,改为不可用
    //
    // dataSourceDomain.updateDataSourceIsAvailableByBundle(stopsTemplateBundle,0);
    //                }
    //            }
    //        }
    logger.info("update StopsComponent Num :" + updateStopsComponentNum);
  }

  @Override
  public String stopsComponentList(String username, boolean isAdmin) {
    if (!isAdmin) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Permission error");
    }
    List<StopsComponentGroupVo> stopGroupList = stopsComponentGroupDomain.getManageStopGroupList();
    for (StopsComponentGroupVo stopsComponentGroupVo : stopGroupList) {
      List<StopsComponentVo> stopsComponentVoList = stopsComponentGroupVo.getStopsComponentVoList();
      for (StopsComponentVo stopsComponentVo : stopsComponentVoList) {
        stopsComponentVo.setGroups(stopsComponentGroupVo.getGroupName());
      }
      stopsComponentGroupVo.setStopsComponentVoList(stopsComponentVoList);
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("stopGroupList", stopGroupList);
  }
}
