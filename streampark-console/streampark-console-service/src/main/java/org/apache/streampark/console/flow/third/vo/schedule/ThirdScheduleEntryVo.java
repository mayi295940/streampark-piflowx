package org.apache.streampark.console.flow.third.vo.schedule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ThirdScheduleEntryVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String scheduleEntryId;
  private String scheduleEntryType;
}
