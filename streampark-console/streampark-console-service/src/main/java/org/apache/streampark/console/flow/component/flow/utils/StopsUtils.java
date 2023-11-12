package org.apache.streampark.console.flow.component.flow.utils;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.dataSource.utils.DataSourceUtils;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;
import org.apache.streampark.console.flow.component.flow.entity.CustomizedProperty;
import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.vo.StopsCustomizedPropertyVo;
import org.apache.streampark.console.flow.component.flow.vo.StopsPropertyVo;
import org.apache.streampark.console.flow.component.flow.vo.StopsVo;
import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponentProperty;
import org.springframework.beans.BeanUtils;

public class StopsUtils {

  public static Stops stopsNewNoId(String username) {

    Stops stops = new Stops();
    // basic properties (required when creating)
    stops.setCrtDttm(new Date());
    stops.setCrtUser(username);
    // basic properties
    stops.setEnableFlag(true);
    stops.setLastUpdateUser(username);
    stops.setLastUpdateDttm(new Date());
    stops.setVersion(0L);
    return stops;
  }

  public static Stops initStopsBasicPropertiesNoId(Stops stops, String username) {
    if (null == stops) {
      return stopsNewNoId(username);
    }
    // basic properties (required when creating)
    stops.setCrtDttm(new Date());
    stops.setCrtUser(username);
    // basic properties
    stops.setEnableFlag(true);
    stops.setLastUpdateUser(username);
    stops.setLastUpdateDttm(new Date());
    stops.setVersion(0L);
    return stops;
  }

  /**
   * stopsList Po To Vo
   *
   * @param stopsList
   * @return
   */
  public static List<StopsVo> stopsListPoToVo(List<Stops> stopsList) {
    List<StopsVo> stopsVoList = null;
    if (null != stopsList && stopsList.size() > 0) {
      stopsVoList = new ArrayList<StopsVo>();
      for (Stops stop : stopsList) {
        StopsVo stopsVo = stopPoToVo(stop, null);
        if (null != stopsVo) {
          stopsVoList.add(stopsVo);
        }
      }
    }
    return stopsVoList;
  }

  /**
   * stop Po To Vo
   *
   * @param stop
   * @return
   */
  public static StopsVo stopPoToVo(Stops stop, StopsComponent stopComponent) {
    if (null == stop) {
      return null;
    }
    StopsVo stopsVo = new StopsVo();
    BeanUtils.copyProperties(stop, stopsVo);
    stopsVo.setIsCheckpoint(stop.getIsCheckpoint());
    DataSource dataSource = stop.getDataSource();
    // datasource Property Map(Key is the attribute name)
    Map<String, String> dataSourcePropertyMap = DataSourceUtils.dataSourceToPropertyMap(dataSource);
    if (null != dataSource) {
      DataSourceVo dataSourceVo = new DataSourceVo();
      BeanUtils.copyProperties(dataSource, dataSourceVo);
      stopsVo.setDataSourceVo(dataSourceVo);
    }
    List<StopsPropertyVo> propertyVos =
        propertyListPoToVo(stop.getProperties(), dataSourcePropertyMap);
    stopsVo.setPropertiesVo(propertyVos);
    List<StopsCustomizedPropertyVo> stopsCustomizedPropertyVoList =
        customizedPropertyListPoToVo(stop.getCustomizedPropertyList());
    stopsVo.setStopsCustomizedPropertyVoList(stopsCustomizedPropertyVoList);
    List<StopsPropertyVo> oldPropertyVos =
        propertyListPoToVo(stop.getOldProperties(), dataSourcePropertyMap);
    stopsVo.setOldPropertiesVo(oldPropertyVos);
    if (null != stopComponent) {
      List<StopsComponentProperty> stopComponentProperties = stopComponent.getProperties();
      List<StopsPropertyVo> propertiesVo = stopsVo.getPropertiesVo();
      if (null != propertiesVo
          && propertiesVo.size() > 0
          && null != stopComponentProperties
          && stopComponentProperties.size() > 0) {
        Map<String, StopsComponentProperty> property_map = new HashMap<>();
        for (StopsComponentProperty stopsComponentProperty : stopComponentProperties) {
          if (null == stopsComponentProperty) {
            continue;
          }
          property_map.put(stopsComponentProperty.getName(), stopsComponentProperty);
        }
        for (StopsPropertyVo propertyVo : propertiesVo) {
          if (null == propertiesVo) {
            continue;
          }
          StopsComponentProperty stopsComponentProperty = property_map.get(propertyVo.getName());
          if (null == stopsComponentProperty) {
            continue;
          }
          propertyVo.setExample(stopsComponentProperty.getExample());
          propertyVo.setLanguage(stopsComponentProperty.getLanguage());
        }
        stopsVo.setPropertiesVo(propertiesVo);
      }
    }
    return stopsVo;
  }

  /**
   * PropertyList(properties)Po To Vo
   *
   * @param properties
   * @param dataSourcePropertyMap
   * @return
   */
  public static List<StopsPropertyVo> propertyListPoToVo(
          List<Property> properties, Map<String, String> dataSourcePropertyMap) {
    List<StopsPropertyVo> propertiesVo = null;
    if (null != properties && properties.size() > 0) {
      propertiesVo = new ArrayList<>();
      for (Property property : properties) {
        if (null != property) {
          StopsPropertyVo propertyVo = new StopsPropertyVo();
          BeanUtils.copyProperties(property, propertyVo);
          // "stop" attribute isSelect
          Boolean isLocked = propertyVo.getIsLocked();
          // Judge empty
          if (null != isLocked && isLocked) {
            // "stop" attribute name
            String name = property.getName();
            // Judge empty
            if (StringUtils.isNotBlank(name)) {
              // Go to the map of the "datasource" attribute
              String value = dataSourcePropertyMap.get(name.toLowerCase());
              // Judge empty
              if (StringUtils.isNotBlank(value)) {
                propertyVo.setCustomValue(value);
              }
            }
          }
          propertiesVo.add(propertyVo);
        }
      }
    }
    return propertiesVo;
  }

  /**
   * CustomizedPropertyList(properties)Po To Vo
   *
   * @param customizedPropertyList
   * @return
   */
  public static List<StopsCustomizedPropertyVo> customizedPropertyListPoToVo(
      List<CustomizedProperty> customizedPropertyList) {
    List<StopsCustomizedPropertyVo> propertiesVo = null;
    if (null != customizedPropertyList && customizedPropertyList.size() > 0) {

      propertiesVo = new ArrayList<>();
      for (CustomizedProperty customizedProperty : customizedPropertyList) {
        if (null != customizedProperty) {
          StopsCustomizedPropertyVo customizedPropertyVo = new StopsCustomizedPropertyVo();
          BeanUtils.copyProperties(customizedProperty, customizedPropertyVo);
          propertiesVo.add(customizedPropertyVo);
        }
      }
    }
    return propertiesVo;
  }
}
