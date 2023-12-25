package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum ScheduleState {
  INIT("INIT", "Init"),
  RUNNING("RUNNING", "Running"),
  SUSPEND("PAUSE", "Pause"),
  STOP("STOP", "Stop");

  private final String value;
  private final String text;

  private ScheduleState(String text, String value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public static ScheduleState selectGender(String name) {
    for (ScheduleState portType : ScheduleState.values()) {
      if (name.equalsIgnoreCase(portType.name())) {
        return portType;
      }
    }
    return null;
  }
}
