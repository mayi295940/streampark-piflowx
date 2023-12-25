package org.apache.streampark.console.flow.component.flow.vo;

import lombok.Getter;
import lombok.Setter;

/** stop component table */
@Getter
@Setter
public class FlowStopsPublishingVo {

  private static final long serialVersionUID = 1L;

  private String id;
  private String publishingId;
  private String name;
  private String state;
  private StopsVo stopsVo;
}
