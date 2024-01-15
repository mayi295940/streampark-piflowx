package org.apache.streampark.console.flow.component.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Statistics {

  private String id;
  private String loginIp;
  private String loginUser;
  private Date loginTime;
}
