package org.apache.streampark.console.flow.component.flow.service;

import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.vo.FlowVo;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestAdd;
import org.apache.streampark.console.flow.controller.requestVo.FlowInfoVoRequestUpdate;

import com.github.pagehelper.Page;

import java.util.List;

public interface IFlowService {

  /**
   * Query flow information based on id
   *
   * @param id id
   */
  Flow getFlowById(String username, boolean isAdmin, String id);

  /**
   * Query flow information based on pageId
   *
   * @param fid fid
   * @param pageId pageId
   */
  FlowVo getFlowByPageId(String fid, String pageId);

  /**
   * Query flow information based on id
   *
   * @param id id
   */
  String getFlowVoById(String id);

  /**
   * add flow(Contains drawing board information)
   *
   * @param username username
   * @param flowVo flowVo
   * @throws Exception e
   */
  String addFlow(String username, FlowInfoVoRequestAdd flowVo) throws Exception;

  boolean deleteFLowInfo(String username, boolean isAdmin, String id);

  Integer getMaxStopPageId(String flowId);

  List<FlowVo> getFlowList();

  /**
   * Paging query flow
   *
   * @param username username
   * @param isAdmin isAdmin
   * @param offset Number of pages
   * @param limit Number of pages per page
   * @param param search for the keyword
   */
  Page<FlowVo> getFlowListPage(
      String username, boolean isAdmin, int offset, int limit, String param);

  String getFlowExampleList();

  /**
   * Call the start interface and save the return information
   *
   * @param flowId flowId
   */
  String runFlow(String username, boolean isAdmin, String flowId, String runMode) throws Exception;

  String startFlowAndGetProcessJson(String username, boolean isAdmin, String flowId, String runMode)
      throws Exception;

  /**
   * Call the start interface and save the return information
   *
   * @param publishingId publishingId
   */
  String runFlowByPublishingId(
      String username, boolean isAdmin, String publishingId, String runMode) throws Exception;

  String updateFlowBaseInfo(String username, String fId, FlowInfoVoRequestUpdate flowVo)
      throws Exception;

  String updateFlowNameById(
      String username, String id, String flowGroupId, String flowName, String pageId)
      throws Exception;

  Boolean updateFlowNameById(String username, String id, String flowName) throws Exception;

  Integer getMaxFlowPageIdByFlowGroupId(String flowGroupId);

  String drawingBoardData(String username, boolean isAdmin, String load, String parentAccessPath);
}
