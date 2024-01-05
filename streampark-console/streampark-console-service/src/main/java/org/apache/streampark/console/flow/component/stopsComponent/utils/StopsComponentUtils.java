package org.apache.streampark.console.flow.component.stopsComponent.utils;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.streampark.console.flow.base.utils.ImageUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.constant.Constants;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentVo;

public class StopsComponentUtils {

  public static StopsComponent stopsComponentNewNoId(String username) {

    StopsComponent stopsComponent = new StopsComponent();
    // basic properties (required when creating)
    stopsComponent.setCrtDttm(new Date());
    stopsComponent.setCrtUser(username);
    // basic properties
    stopsComponent.setEnableFlag(true);
    stopsComponent.setLastUpdateUser(username);
    stopsComponent.setLastUpdateDttm(new Date());
    stopsComponent.setVersion(0L);
    return stopsComponent;
  }

  public static StopsComponent initStopsComponentBasicPropertiesNoId(
      StopsComponent stopsComponent, String username) {
    if (null == stopsComponent) {
      return stopsComponentNewNoId(username);
    }
    // basic properties (required when creating)
    stopsComponent.setCrtDttm(new Date());
    stopsComponent.setCrtUser(username);
    // basic properties
    stopsComponent.setEnableFlag(true);
    stopsComponent.setLastUpdateUser(username);
    stopsComponent.setLastUpdateDttm(new Date());
    stopsComponent.setVersion(0L);
    return stopsComponent;
  }

  public static StopsComponent thirdStopsComponentVoToStopsTemplate(
      String username,
      ThirdStopsComponentVo thirdStopsComponentVo,
      List<StopsComponentGroup> stopGroupByName) {

    if (null == thirdStopsComponentVo) {
      return null;
    }
    if (StringUtils.isBlank(username)) {
      return null;
    }
    if (null == stopGroupByName || stopGroupByName.size() == 0) {
      return null;
    }
    String inports = thirdStopsComponentVo.getInports();
    PortType inPortType = null;
    if (StringUtils.isNotBlank(inports)) {
      for (PortType value : PortType.values()) {
        if (inports.equalsIgnoreCase(value.getValue())) {
          inPortType = value;
        }
      }
      if (null == inPortType) {
        inPortType = PortType.USER_DEFAULT;
      }
    }
    PortType.selectGenderByValue(inports);
    String outports = thirdStopsComponentVo.getOutports();
    PortType outPortType = null;
    if (StringUtils.isNotBlank(outports)) {
      for (PortType value : PortType.values()) {
        if (outports.equalsIgnoreCase(value.getValue())) {
          outPortType = value;
        }
      }
      if (null == outPortType) {
        outPortType = PortType.USER_DEFAULT;
      }
    }

    String icon = thirdStopsComponentVo.getIcon();
    if (StringUtils.isNotBlank(icon)) {

      String imagePath =
          Constants.ENGIN_FLINK.equalsIgnoreCase(thirdStopsComponentVo.getEngineType())
              ? SysParamsCache.ENGINE_FLINK_IMAGES_PATH
              : SysParamsCache.ENGINE_SPARK_IMAGES_PATH;

      ImageUtils.generateImage(
          icon, thirdStopsComponentVo.getName() + "_128x128", "png", imagePath);
    }

    StopsComponent stopsComponent = stopsComponentNewNoId(username);
    stopsComponent.setId(UUIDUtils.getUUID32());
    stopsComponent.setBundle(thirdStopsComponentVo.getBundle());
    stopsComponent.setDescription(thirdStopsComponentVo.getDescription());
    stopsComponent.setGroups(thirdStopsComponentVo.getGroups());
    stopsComponent.setName(thirdStopsComponentVo.getName());
    stopsComponent.setEngineType(thirdStopsComponentVo.getEngineType());
    stopsComponent.setInports(inports);
    stopsComponent.setInPortType(inPortType);
    stopsComponent.setOutports(outports);
    stopsComponent.setOutPortType(outPortType);
    stopsComponent.setOwner(thirdStopsComponentVo.getOwner());
    stopsComponent.setIsCustomized(thirdStopsComponentVo.isCustomized());
    stopsComponent.setIsDataSource(thirdStopsComponentVo.isDataSource());
    stopsComponent.setStopGroupList(stopGroupByName);
    stopsComponent.setVisualizationType(thirdStopsComponentVo.getVisualizationType());

    // todo  图片地址
    String imageUrl =
         SysParamsCache.IMG_PATH_PREFIX +
        "/images/" + thirdStopsComponentVo.getName() + "_128x128.png";
    stopsComponent.setImageUrl(imageUrl);

    List<StopsComponentProperty> listStopsComponentProperty =
        StopsComponentPropertyUtils.thirdStopsComponentPropertyVoListToStopsComponentProperty(
            username, thirdStopsComponentVo.getProperties(), stopsComponent);
    stopsComponent.setProperties(listStopsComponentProperty);
    return stopsComponent;
  }
}
