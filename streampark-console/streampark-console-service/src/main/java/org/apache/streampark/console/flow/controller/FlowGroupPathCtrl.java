package org.apache.streampark.console.flow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.streampark.console.flow.component.flow.service.IFlowGroupPathsService;

@RestController
@RequestMapping("/flowGroupPath/")
public class FlowGroupPathCtrl {

  @Autowired private IFlowGroupPathsService flowGroupPathsServiceImpl;

  /**
   * Query'path'according to'flowId' and'pageId'
   *
   * @param fid
   * @param id
   * @return
   */
  @RequestMapping("/queryPathInfoFlowGroup")
  public String queryPathInfoFlowGroup(String fid, String id) {
    return flowGroupPathsServiceImpl.queryPathInfoFlowGroup(fid, id);
  }
}
