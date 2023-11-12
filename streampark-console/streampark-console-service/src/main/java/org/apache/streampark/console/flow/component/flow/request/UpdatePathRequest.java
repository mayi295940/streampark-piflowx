package org.apache.streampark.console.flow.component.flow.request;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePathRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  String flowId; // flow id
  String pathLineId; // path id
  String sourceId; // source stop page id
  String sourcePortVal; // source port val
  String targetId; // target stop page id
  String targetPortVal; // target port val
  String sourceFilter; // source filter val
  String targetFilter; // target filter val
  boolean isSourceRoute; // source is Route
  boolean isTargetRoute; // target is Route
}
