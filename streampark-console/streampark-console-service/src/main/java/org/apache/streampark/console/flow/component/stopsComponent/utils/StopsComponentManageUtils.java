package org.apache.streampark.console.flow.component.stopsComponent.utils;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentManage;
import java.util.Date;

public class StopsComponentManageUtils {

  public static StopsComponentManage stopsComponentManageNewNoId(String username) {

    StopsComponentManage stopsComponentManage = new StopsComponentManage();
    // basic properties (required when creating)
    stopsComponentManage.setCrtDttm(new Date());
    stopsComponentManage.setCrtUser(username);
    // basic properties
    stopsComponentManage.setEnableFlag(true);
    stopsComponentManage.setLastUpdateUser(username);
    stopsComponentManage.setLastUpdateDttm(new Date());
    stopsComponentManage.setVersion(0L);
    return stopsComponentManage;
  }
}
