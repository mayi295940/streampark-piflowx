package org.apache.streampark.console.flow.component.stopsComponent.utils;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHub;

import java.util.Date;

public class StopsHubUtils {

  public static StopsHub stopsHubNewNoId(String username) {

    StopsHub stopsHub = new StopsHub();
    // basic properties (required when creating)
    stopsHub.setCrtDttm(new Date());
    stopsHub.setCrtUser(username);
    // basic properties
    stopsHub.setEnableFlag(true);
    stopsHub.setLastUpdateUser(username);
    stopsHub.setLastUpdateDttm(new Date());
    stopsHub.setVersion(0L);
    return stopsHub;
  }
}
