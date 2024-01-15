package org.apache.streampark.console.flow.controller.api.flow;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.common.Eunm.DrawingBoardType;
import org.apache.streampark.console.flow.component.flow.service.IFlowGroupService;
import org.apache.streampark.console.flow.component.flow.service.IFlowService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.FlowGroupInfoVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.FlowGroupInfoVoRequestUpDate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "flowGroup api", tags = "flowGroup api")
@Controller
@RequestMapping("/flowGroup")
public class FlowGroupCtrl {

  private final IFlowGroupService flowGroupServiceImpl;
  private final ILogHelperService logHelperServiceImpl;
  private final IFlowService flowServiceImpl;

  @Autowired
  public FlowGroupCtrl(
      IFlowGroupService flowGroupServiceImpl,
      ILogHelperService logHelperServiceImpl,
      IFlowService flowServiceImpl) {
    this.flowGroupServiceImpl = flowGroupServiceImpl;
    this.logHelperServiceImpl = logHelperServiceImpl;
    this.flowServiceImpl = flowServiceImpl;
  }

  /** ‘flowGroupList’ paged query */
  @RequestMapping(value = "/getFlowGroupListPage", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "getFlowGroupListPage", notes = "get FlowGroup list page")
  public String getFlowGroupListPage(Integer page, Integer limit, String param) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowGroupServiceImpl.getFlowGroupListPage(username, isAdmin, page, limit, param);
  }

  /** Save add flowGroup */
  @RequestMapping(value = "/saveOrUpdateFlowGroup", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "saveOrUpdateFlowGroup", notes = "save or update FlowGroup")
  public String saveOrUpdateFlowGroup(FlowGroupInfoVoRequest flowGroupVo) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    logHelperServiceImpl.logAuthSucceed("saveOrUpdateFlowGroup " + flowGroupVo.getName(), username);
    return flowGroupServiceImpl.saveOrUpdate(username, flowGroupVo);
  }

  @RequestMapping(value = "/updateFlowGroupBaseInfo", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "updateFlowGroupBaseInfo", notes = "update FlowGroup base info")
  public String updateFlowGroupBaseInfo(String fId, FlowGroupInfoVoRequestUpDate flowGroupVo)
      throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    return flowGroupServiceImpl.updateFlowGroupBaseInfo(username, fId, flowGroupVo);
  }

  @RequestMapping(value = "/queryFlowGroupData", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "queryFlowGroupData", notes = "query FlowGroup data")
  public String queryFlowGroupData(String load) {
    return flowGroupServiceImpl.getFlowGroupVoInfoById(load);
  }

  @RequestMapping(value = "/queryIdInfo", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "queryIdInfo", notes = "query id info")
  public String queryIdInfo(String fId, String pageId) {
    return flowGroupServiceImpl.queryIdInfo(fId, pageId);
  }

  /** findFlowByGroup */
  @RequestMapping(value = "/findFlowByGroup", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "findFlowByGroup", notes = "find FlowByGroup")
  public String findFlowByGroup(String fId, String pageId) {
    return flowGroupServiceImpl.queryIdInfo(fId, pageId);
  }

  /** Enter the front page of the drawing board */
  @RequestMapping(value = "/drawingBoardData", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "drawingBoardData", notes = "drawingBoard data")
  public String drawingBoardData(
      String parentAccessPath, String load, DrawingBoardType drawingBoardType, String processType) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowGroupServiceImpl.drawingBoardData(username, isAdmin, load, parentAccessPath);
  }

  @RequestMapping(value = "/runFlowGroup", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "runFlowGroup", notes = "run FlowGroup")
  public String runFlowGroup(String flowGroupId, String runMode) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("runFlowGroup" + runMode, username);
    return flowGroupServiceImpl.runFlowGroup(isAdmin, username, flowGroupId, runMode);
  }

  /** Delete flow association information according to flowId */
  @RequestMapping(value = "/deleteFlowGroup", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "deleteFlowGroup", notes = "delete FlowGroup")
  public String deleteFlowGroup(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("deleteFlowGroup" + id, username);
    return flowGroupServiceImpl.deleteFLowGroupInfo(isAdmin, username, id);
  }

  @RequestMapping(value = "/copyFlowToGroup", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "copyFlowToGroup", notes = "copy Flow to FlowGroup")
  public String copyFlowToGroup(String flowId, String flowGroupId) throws Exception {
    String username = SessionUserUtil.getCurrentUsername();
    return flowGroupServiceImpl.copyFlowToGroup(username, flowId, flowGroupId);
  }

  @RequestMapping(value = "/updateFlowNameById", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "updateFlowNameById", notes = "update Flow name by id")
  public String updateFlowNameById(
      String updateType,
      String parentId,
      String currentNodeId,
      String currentNodePageId,
      String name)
      throws Exception {

    String username = SessionUserUtil.getCurrentUsername();
    if ("flowGroup".equals(updateType)) {
      return flowGroupServiceImpl.updateFlowGroupNameById(
          username, currentNodeId, parentId, name, currentNodePageId);
    }
    return flowServiceImpl.updateFlowNameById(
        username, currentNodeId, parentId, name, currentNodePageId);
  }
}
