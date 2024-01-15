package org.apache.streampark.console.flow.component.schedule.utils;

import org.apache.streampark.console.flow.component.schedule.entity.Schedule;

import java.util.Date;

public class ScheduleUtils {

  public static Schedule newScheduleNoId(String username) {

    Schedule schedule = new Schedule();
    // basic properties (required when creating)
    schedule.setCrtDttm(new Date());
    schedule.setCrtUser(username);
    // basic properties
    schedule.setEnableFlag(true);
    schedule.setLastUpdateUser(username);
    schedule.setLastUpdateDttm(new Date());
    schedule.setVersion(0L);
    return schedule;
  }
}
