package org.apache.streampark.console.flow.component.stopsComponent.utils;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;
import java.util.Date;

public class StopsComponentGroupUtils {

  public static StopsComponentGroup stopsComponentGroupNewNoId(String username) {

    StopsComponentGroup stopsComponentGroup = new StopsComponentGroup();
    // basic properties (required when creating)
    stopsComponentGroup.setCrtDttm(new Date());
    stopsComponentGroup.setCrtUser(username);
    // basic properties
    stopsComponentGroup.setEnableFlag(true);
    stopsComponentGroup.setLastUpdateUser(username);
    stopsComponentGroup.setLastUpdateDttm(new Date());
    stopsComponentGroup.setVersion(0L);
    return stopsComponentGroup;
  }

  public static StopsComponentGroup initStopsComponentGroupBasicPropertiesNoId(
      StopsComponentGroup stopsComponentGroup, String username) {
    if (null == stopsComponentGroup) {
      return stopsComponentGroupNewNoId(username);
    }
    // basic properties (required when creating)
    stopsComponentGroup.setCrtDttm(new Date());
    stopsComponentGroup.setCrtUser(username);
    // basic properties
    stopsComponentGroup.setEnableFlag(true);
    stopsComponentGroup.setLastUpdateUser(username);
    stopsComponentGroup.setLastUpdateDttm(new Date());
    stopsComponentGroup.setVersion(0L);
    return stopsComponentGroup;
  }
}
