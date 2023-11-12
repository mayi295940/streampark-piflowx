package org.apache.streampark.console.flow.component.template.utils;

import java.util.Date;
import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;

public class FlowTemplateUtils {

  public static FlowTemplate newFlowTemplateNoId(String username) {
    FlowTemplate flowTemplate = new FlowTemplate();
    // basic properties (required when creating)
    flowTemplate.setCrtDttm(new Date());
    flowTemplate.setCrtUser(username);
    // basic properties
    flowTemplate.setEnableFlag(true);
    flowTemplate.setLastUpdateUser(username);
    flowTemplate.setLastUpdateDttm(new Date());
    flowTemplate.setVersion(0L);
    return flowTemplate;
  }
}
