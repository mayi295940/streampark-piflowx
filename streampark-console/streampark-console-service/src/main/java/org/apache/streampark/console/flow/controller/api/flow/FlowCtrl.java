package org.apache.streampark.console.flow.controller.api.flow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.streampark.console.base.domain.RestRequest;
import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.service.IFlowService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestAdd;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "flow api", tags = "flow api")
@RestController
@RequestMapping("/flow")
public class FlowCtrl {

  private final IFlowService flowServiceImpl;
  private final ILogHelperService logHelperServiceImpl;

  @Autowired
  public FlowCtrl(IFlowService flowServiceImpl, ILogHelperService logHelperServiceImpl) {
    this.flowServiceImpl = flowServiceImpl;
    this.logHelperServiceImpl = logHelperServiceImpl;
  }

  @Operation(summary = "List flow page")
  @ApiOperation(value = "getFlowListPage", notes = "get Flow list page")
  @PostMapping("getFlowListPage")
  public RestResponse getFlowListPage(RestRequest restRequest, String param) {
    boolean isAdmin = SessionUserUtil.isAdmin();
    String username = SessionUserUtil.getCurrentUsername();
    return RestResponse.success(
        flowServiceImpl.getFlowListPage(
            username, isAdmin, restRequest.getPageNum(), restRequest.getPageSize(), param));
  }

  /** Enter the front page of the drawing board */
  @GetMapping(value = "/drawingBoardData")
  @ApiOperation(value = "drawingBoardData", notes = "drawingBoard data")
  public String drawingBoardData(String load, String parentAccessPath) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowServiceImpl.drawingBoardData(username, isAdmin, load, parentAccessPath);
  }

  @PostMapping(value = "/runFlow")
  @ApiOperation(value = "runFlow", notes = "run Flow")
  public String runFlow(String flowId, String runMode) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("run flow", username);
    return flowServiceImpl.runFlow(username, isAdmin, flowId, runMode);
  }

  @PostMapping(value = "/runFlowByPublishingId")
  @ApiOperation(value = "runFlowByPublishingId", notes = "run Flow")
  public String runFlowByPublishingId(String publishingId, String runMode) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("run flow", username);
    return flowServiceImpl.runFlowByPublishingId(username, isAdmin, publishingId, runMode);
  }

  @PostMapping(value = "/queryFlowData")
  @ApiOperation(value = "queryFlowData", notes = "query Flow data")
  public String queryFlowData(String load) {
    return flowServiceImpl.getFlowVoById(load);
  }

  @PostMapping(value = "/saveFlowInfo")
  @ApiOperation(value = "saveFlowInfo", notes = "save Flow info")
  public RestResponse saveFlowInfo(FlowInfoVoRequestAdd flowVo) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    logHelperServiceImpl.logAuthSucceed("save flow", username);
    return RestResponse.success(flowServiceImpl.addFlow(username, flowVo));
  }

  /** Delete flow association information according to flowId */
  @GetMapping(value = "/deleteFlow")
  @ApiOperation(value = "deleteFlow", notes = "delete Flow")
  public String deleteFlow(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("delete flow " + id, username);
    return flowServiceImpl.deleteFLowInfo(username, isAdmin, id);
  }

  @PostMapping(value = "/updateFlowBaseInfo")
  @ApiOperation(value = "updateFlowBaseInfo", notes = "update Flow base info")
  @ApiImplicitParam(name = "fId", value = "fId")
  public String updateFlowBaseInfo(String fId, FlowInfoVoRequestUpdate flowVo) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    logHelperServiceImpl.logAuthSucceed("update flow base " + flowVo.getName(), username);
    return flowServiceImpl.updateFlowBaseInfo(username, fId, flowVo);
  }
}
