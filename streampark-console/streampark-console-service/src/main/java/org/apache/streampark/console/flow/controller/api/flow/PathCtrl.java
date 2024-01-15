package org.apache.streampark.console.flow.controller.api.flow;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;
import org.apache.streampark.console.flow.component.flow.service.IPathsService;
import org.apache.streampark.console.flow.component.flow.service.IPropertyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "path api", tags = "path api")
@RestController
@RequestMapping("/path")
public class PathCtrl {

  private final IPropertyService propertyServiceImpl;
  private final IPathsService pathsServiceImpl;

  @Autowired
  public PathCtrl(IPropertyService propertyServiceImpl, IPathsService pathsServiceImpl) {
    this.propertyServiceImpl = propertyServiceImpl;
    this.pathsServiceImpl = pathsServiceImpl;
  }

  @RequestMapping(value = "/queryPathInfo", method = RequestMethod.POST)
  @ApiOperation(value = "queryPathInfo", notes = "query Path info")
  public String getStopGroup(String fid, String id) {
    return pathsServiceImpl.getPathsByFlowIdAndPageId(fid, id);
  }

  @RequestMapping(value = "/savePathsPort", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "savePathsPort", notes = "save Paths port")
  public String savePathsPort(UpdatePathRequest updatePathRequest) {
    String username = SessionUserUtil.getCurrentUsername();
    return propertyServiceImpl.saveOrUpdateRoutePath(username, updatePathRequest);
  }
}
