package org.apache.streampark.console.flow.component.livy.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeSnippet extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String codeContent;
  private String executeId;
  private int codeSnippetSort;

  private NoteBook noteBook;
}
