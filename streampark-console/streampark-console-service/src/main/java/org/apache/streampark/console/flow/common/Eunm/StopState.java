package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum StopState {
  INIT("INIT", "init"),
  STARTED("STARTED", "started"),
  COMPLETED("COMPLETED", "completed"),
  FAILED("FAILED", "failed"),
  KILLED("KILLED", "killed");

  private final String value;
  private final String text;

  private StopState(String text, String value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public static StopState selectGender(String name) {
    for (StopState portType : StopState.values()) {
      if (name.equalsIgnoreCase(portType.name())) {
        return portType;
      }
    }
    return null;
  }
}
