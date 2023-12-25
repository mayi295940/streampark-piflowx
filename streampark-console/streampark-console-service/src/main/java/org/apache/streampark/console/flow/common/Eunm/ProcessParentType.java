package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum ProcessParentType {
  PROCESS("PROCESS", "Process"),
  GROUP("GROUP", "Group"),
  PROJECT("PROJECT", "Project");

  private final String value;
  private final String text;

  private ProcessParentType(String value, String text) {
    this.value = value;
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public static ProcessParentType selectGender(String name) {
    for (ProcessParentType portType : ProcessParentType.values()) {
      if (name.equalsIgnoreCase(portType.name())) {
        return portType;
      }
    }
    return null;
  }
}
