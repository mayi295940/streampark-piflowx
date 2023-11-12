package org.apache.streampark.console.flow.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;
import org.apache.streampark.console.flow.component.flow.service.IPathsService;
import org.apache.streampark.console.flow.component.flow.service.IPropertyService;

@RestController
@RequestMapping("/path")
public class PathCtrl {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  Logger logger = LoggerUtil.getLogger();

  @Autowired private IPropertyService propertyServiceImpl;

  @Autowired private IPathsService pathsServiceImpl;

  /**
   * Query'path'according to'flowId' and'pageId'
   *
   * @param fid
   * @param id
   * @return
   */
  @RequestMapping("/queryPathInfo")
  public String getStopGroup(String fid, String id) {
    return pathsServiceImpl.getPathsByFlowIdAndPageId(fid, id);
  }

  /**
   * Save user-selected ports
   *
   * @param updatePathRequest
   * @return
   */
  @RequestMapping("/savePathsPort")
  @ResponseBody
  public String savePathsPort(UpdatePathRequest updatePathRequest) {
    String username = SessionUserUtil.getCurrentUsername();
    return propertyServiceImpl.saveOrUpdateRoutePath(username, updatePathRequest);
  }
}
