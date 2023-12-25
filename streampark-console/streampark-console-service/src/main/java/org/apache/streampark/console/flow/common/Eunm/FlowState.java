package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum FlowState {
  STARTED("STARTED", "start up"),
  COMPLETED("COMPLETED", "completed"),
  HOME("FAILED", "failed"),
  ABORTED("ABORTED", "aborted"),
  FORK("FORK", "FORK");

  private final String value;
  private final String text;

  private FlowState(String text, String value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public static FlowState selectGender(String name) {
    for (FlowState portType : FlowState.values()) {
      if (name.equalsIgnoreCase(portType.name())) {
        return portType;
      }
    }
    return null;
  }
}
