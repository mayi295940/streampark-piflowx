package org.apache.streampark.console.flow.component.flow.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupPathsVo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class FlowGroupPathsUtil {
  /**
   * pathsList Po To Vo
   *
   * @param flowGroupPathsList
   * @return
   */
  public static List<FlowGroupPathsVo> flowGroupPathsPoToVo(
      List<FlowGroupPaths> flowGroupPathsList) {
    List<FlowGroupPathsVo> flowGroupPathsVoList = null;
    if (null != flowGroupPathsList && flowGroupPathsList.size() > 0) {
      flowGroupPathsVoList = new ArrayList<>();
      for (FlowGroupPaths flowGroupPaths : flowGroupPathsList) {
        if (null != flowGroupPaths) {
          FlowGroupPathsVo flowGroupPathsVo = new FlowGroupPathsVo();
          BeanUtils.copyProperties(flowGroupPaths, flowGroupPathsVo);
          flowGroupPathsVoList.add(flowGroupPathsVo);
        }
      }
    }
    return flowGroupPathsVoList;
  }

  /**
   * pathsVoList Vo To Po
   *
   * @param flowGroupPathsVoList
   * @return
   */
  public static List<FlowGroupPaths> flowGroupPathsListVoToPo(
      String username, List<FlowGroupPathsVo> flowGroupPathsVoList) {
    List<FlowGroupPaths> flowGroupPathsList = null;
    if (null != flowGroupPathsVoList && flowGroupPathsVoList.size() > 0) {
      flowGroupPathsList = new ArrayList<>();
      for (FlowGroupPathsVo flowGroupPathsVo : flowGroupPathsVoList) {
        if (null != flowGroupPathsVo) {
          FlowGroupPaths flowGroupPaths = new FlowGroupPaths();
          BeanUtils.copyProperties(flowGroupPathsVo, flowGroupPaths);
          flowGroupPaths.setId(UUIDUtils.getUUID32());
          flowGroupPaths.setCrtDttm(new Date());
          flowGroupPaths.setCrtUser(username);
          flowGroupPaths.setLastUpdateDttm(new Date());
          flowGroupPaths.setLastUpdateUser(username);
          flowGroupPaths.setEnableFlag(true);
          flowGroupPathsList.add(flowGroupPaths);
        }
      }
    }
    return flowGroupPathsList;
  }
}
