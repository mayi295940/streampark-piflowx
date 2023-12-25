package org.apache.streampark.console.flow.component.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysInitRecords implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private Date initDate = new Date();
  private Boolean isSucceed = Boolean.TRUE;
}
