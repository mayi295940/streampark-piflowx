package org.apache.streampark.console.flow.component.livy.service;

import org.apache.streampark.console.flow.controller.requestVo.CodeSnippetVoRequestAdd;
import org.apache.streampark.console.flow.controller.requestVo.CodeSnippetVoRequestUpdate;

public interface ICodeSnippetService {

  public String addCodeSnippet(String username, CodeSnippetVoRequestAdd codeSnippetVo)
      throws Exception;

  public String updateCodeSnippet(String username, CodeSnippetVoRequestUpdate codeSnippetVo);

  public String delCodeSnippet(String username, boolean isAdmin, String codeSnippetId);

  public String getCodeSnippetList(String noteBookId);

  public String runCodeSnippet(String username, String codeSnippetId);

  public String getStatementsResult(String codeSnippetId);
}
