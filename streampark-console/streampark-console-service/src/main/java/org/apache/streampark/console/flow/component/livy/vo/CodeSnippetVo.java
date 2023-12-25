package org.apache.streampark.console.flow.component.livy.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeSnippetVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String codeContent;
  private String executeId;
  private int codeSnippetSort;
}
