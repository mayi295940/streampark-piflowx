package org.apache.streampark.console.flow.component.livy.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class NoteBook extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private String sessionsId;
  private String codeType;
  private List<CodeSnippet> codeSnippetList = new ArrayList<>();
}
