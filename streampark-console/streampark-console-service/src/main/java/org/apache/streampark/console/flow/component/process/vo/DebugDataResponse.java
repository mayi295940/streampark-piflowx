package org.apache.streampark.console.flow.component.process.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DebugDataResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  int lastReadLine; // The last number of rows read
  String lastFileName; // Last read file
  boolean isEnd; // Whether to reach the end of the file
  List<String> schema = new ArrayList<>();
  List<String> data = new ArrayList<>();
}
