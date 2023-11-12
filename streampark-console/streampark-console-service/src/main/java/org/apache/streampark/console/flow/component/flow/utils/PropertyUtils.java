package org.apache.streampark.console.flow.component.flow.utils;

import java.util.Date;
import org.apache.streampark.console.flow.component.flow.entity.Property;

public class PropertyUtils {

  public static Property propertyNewNoId(String username) {

    Property property = new Property();
    // basic properties (required when creating)
    property.setCrtDttm(new Date());
    property.setCrtUser(username);
    // basic properties
    property.setEnableFlag(true);
    property.setLastUpdateUser(username);
    property.setLastUpdateDttm(new Date());
    property.setVersion(0L);
    return property;
  }
}
