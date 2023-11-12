package org.apache.streampark.console.flow.component.process.service;

import org.springframework.transaction.annotation.Transactional;

public interface IProcessStopService {

  /**
   * Query processStop based on processId and pageId
   *
   * @param processId
   * @param pageId
   * @return
   */
  @Transactional
  public String getProcessStopVoByPageId(String processId, String pageId);
}
