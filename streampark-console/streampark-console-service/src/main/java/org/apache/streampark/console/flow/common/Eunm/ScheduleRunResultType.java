package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum ScheduleRunResultType {
  INIT("INIT", "Init"),
  SUCCEED("SUCCEED", "Succeed"),
  FAILURE("FAILURE", "Failure");

  private final String value;
  private final String text;

  private ScheduleRunResultType(String text, String value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public static ScheduleRunResultType selectGender(String name) {
    for (ScheduleRunResultType portType : ScheduleRunResultType.values()) {
      if (name.equalsIgnoreCase(portType.name())) {
        return portType;
      }
    }
    return null;
  }
}
