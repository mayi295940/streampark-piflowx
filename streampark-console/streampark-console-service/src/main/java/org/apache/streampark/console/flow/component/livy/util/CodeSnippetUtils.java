package org.apache.streampark.console.flow.component.livy.util;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.livy.entity.CodeSnippet;

import java.util.Date;

public class CodeSnippetUtils {

  public static CodeSnippet setCodeSnippetBasicInformation(
      CodeSnippet codeSnippet, boolean isSetId, String username) {
    if (null == codeSnippet) {
      codeSnippet = new CodeSnippet();
    }
    if (isSetId) {
      codeSnippet.setId(UUIDUtils.getUUID32());
    } else {
      codeSnippet.setId(null);
    }
    // set MxGraphModel basic information
    codeSnippet.setCrtDttm(new Date());
    codeSnippet.setCrtUser(username);
    codeSnippet.setLastUpdateDttm(new Date());
    codeSnippet.setLastUpdateUser(username);
    codeSnippet.setVersion(0L);
    return codeSnippet;
  }
}
