package org.apache.streampark.console.flow.controller.api.admin;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.service.IStopsService;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopGroupService;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsComponentManageService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.RunStopsVo;
import org.apache.streampark.console.flow.controller.requestVo.UpdatestopsComponentIsShow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "stopsManage api", tags = "stopsManage api")
@RestController
@RequestMapping("/stopsManage")
public class StopsManageCtrl {

  private final IStopsComponentManageService stopsComponentManageServiceImpl;
  private final IStopGroupService stopGroupServiceImpl;
  private final ILogHelperService logHelperServiceImpl;
  private final IStopsService stopsServiceImpl;

  @Autowired
  public StopsManageCtrl(
      IStopsComponentManageService stopsComponentManageServiceImpl,
      IStopGroupService stopGroupServiceImpl,
      ILogHelperService logHelperServiceImpl,
      IStopsService stopsServiceImpl) {
    this.stopsComponentManageServiceImpl = stopsComponentManageServiceImpl;
    this.stopGroupServiceImpl = stopGroupServiceImpl;
    this.logHelperServiceImpl = logHelperServiceImpl;
    this.stopsServiceImpl = stopsServiceImpl;
  }

  /** stopsComponentList all */
  @RequestMapping(value = "/stopsComponentList", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "stopsComponentList", notes = "StopsComponent list")
  public String stopsComponentList() {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return stopGroupServiceImpl.stopsComponentList(username, isAdmin);
  }

  /** update stopsComponentIsShow */
  @RequestMapping(value = "/updatestopsComponentIsShow", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "updatestopsComponentIsShow", notes = "StopsComponent Manage")
  public String updateStopsComponentIsShow(UpdatestopsComponentIsShow stopsManage)
      throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed(
        "update stopsComponentIsShow " + stopsManage.getStopsGroups(), username);
    return stopsComponentManageServiceImpl.updateStopsComponentIsShow(
        username, isAdmin, stopsManage);
  }

  /**
   * isNeedSource
   *
   * @param stopsId stopsId
   */
  @RequestMapping(value = "/isNeedSource", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "isNeedSource", notes = "StopsComponent is need source data")
  @ApiImplicitParam(name = "stopsId", value = "stopsId", required = true)
  public String isNeedSource(String stopsId) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsServiceImpl.isNeedSource(username, isAdmin, stopsId);
  }

  /**
   * runStops
   *
   * @param runStopsVo runStopsVo
   */
  @RequestMapping(value = "/runStops", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "runStops", notes = "run Stops")
  public String runStops(RunStopsVo runStopsVo) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("runStops " + runStopsVo.getStopsId(), username);
    return stopsServiceImpl.runStops(username, isAdmin, runStopsVo);
  }
}
