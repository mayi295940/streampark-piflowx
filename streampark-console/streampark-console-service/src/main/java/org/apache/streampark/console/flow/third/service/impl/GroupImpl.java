package org.apache.streampark.console.flow.third.service.impl;

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.domain.ProcessGroupDomain;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.third.service.IGroup;
import org.apache.streampark.console.flow.third.utils.ThirdFlowGroupInfoResponseUtils;
import org.apache.streampark.console.flow.third.vo.flowGroup.ThirdFlowGroupInfoOutResponse;
import org.apache.streampark.console.flow.third.vo.flowGroup.ThirdFlowGroupInfoResponse;
import org.apache.streampark.console.flow.third.vo.flowGroup.ThirdFlowInfoOutResponse;
import org.apache.streampark.console.flow.third.vo.flowGroup.ThirdFlowStopInfoOutResponse;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GroupImpl implements IGroup {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private Logger logger = LoggerUtil.getLogger();

  private final ProcessGroupDomain processGroupDomain;

  @Autowired
  public GroupImpl(ProcessGroupDomain processGroupDomain) {
    this.processGroupDomain = processGroupDomain;
  }

  @Override
  public Map<String, Object> startFlowGroup(ProcessGroup processGroup, RunModeType runModeType) {
    if (null == processGroup) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.PARAM_IS_NULL_MSG("processGroup"));
    }
    // String json = ProcessUtil.processGroupToJson(processGroup, runModeType);
    // String formatJson = JsonFormatTool.formatJson(json);
    String formatJson = ProcessUtils.processGroupToJson(processGroup, runModeType);
    logger.info("\n" + formatJson);
    // ===============================临时===============================
    // String path = FileUtils.createJsonFile(formatJson, processGroup.getName(),
    // ApiConfig.VIDEOS_PATH);
    // logger.info(path);
    // String doPost = HttpUtils.doPost(ApiConfig.getFlowGroupStartUrl(), path, null);
    // ===============================临时===============================
    String doPost = HttpUtils.doPost(ApiConfig.getFlowGroupStartUrl(), formatJson, null);
    logger.info("Return information：" + doPost);
    if (StringUtils.isBlank(doPost)) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
    }
    if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Exception")) {
      logger.warn("Return information：" + doPost);
      return ReturnMapUtils.setFailedMsg(MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doPost);
    }
    try {
      // Convert a json string to a json object
      JSONObject obj = JSONObject.fromObject(doPost).getJSONObject("group");
      String groupId = obj.getString("id");
      if (StringUtils.isNotBlank(groupId)) {
        return ReturnMapUtils.setSucceededCustomParam("appId", groupId);
      } else {
        return ReturnMapUtils.setFailedMsg(
            "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
      }
    } catch (Exception e) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.CONVERSION_FAILED_MSG());
    }
  }

  @Override
  public String stopFlowGroup(String processGroupId) {
    Map<String, String> map = new HashMap<>();
    map.put("groupId", processGroupId);
    String json = JSONObject.fromObject(map).toString();
    String doPost = HttpUtils.doPost(ApiConfig.getFlowGroupStopUrl(), json, 5 * 1000);
    if (StringUtils.isBlank(doPost)
        || doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())
        || doPost.contains("Exception")) {
      logger.warn("Interface return exception");
    } else {
      logger.info("Interface return value: " + doPost);
    }
    return doPost;
  }

  @Override
  public String getFlowGroupInfoStr(String groupId) {
    if (StringUtils.isBlank(groupId)) {
      return null;
    }
    Map<String, String> map = new HashMap<>();
    map.put("groupId", groupId);
    String doGet = HttpUtils.doGet(ApiConfig.getFlowGroupInfoUrl(), map, 5 * 1000);
    return doGet;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public ThirdFlowGroupInfoResponse getFlowGroupInfo(String groupId) {
    ThirdFlowGroupInfoResponse thirdFlowGroupInfoResponse = null;
    String doGet = getFlowGroupInfoStr(groupId);
    if (StringUtils.isBlank(doGet)) {
      logger.warn("Interface return values is null");
      return null;
    }
    if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
      logger.warn("Interface exception: " + doGet);
      return null;
    }
    // Also convert the json string to a json object, and then convert the json object to a java
    // object, as shown below.
    // Convert a json string to a json object
    JSONObject obj = JSONObject.fromObject(doGet);
    // Needed when there is a List in jsonObj
    Map<String, Class> classMap = new HashMap<>();
    // Key is the name of the List in jsonObj, and the value is a generic class of list
    classMap.put("flows", ThirdFlowInfoOutResponse.class);
    classMap.put("stops", ThirdFlowStopInfoOutResponse.class);
    classMap.put("groups", ThirdFlowGroupInfoOutResponse.class);
    // Convert a json object to a java object
    ThirdFlowGroupInfoOutResponse thirdFlowGroupInfoOutResponse =
        (ThirdFlowGroupInfoOutResponse)
            JSONObject.toBean(obj, ThirdFlowGroupInfoOutResponse.class, classMap);
    if (null == thirdFlowGroupInfoOutResponse) {
      logger.warn("conversion exception");
      return null;
    }
    thirdFlowGroupInfoResponse = thirdFlowGroupInfoOutResponse.getGroup();
    if (null == thirdFlowGroupInfoResponse) {
      logger.warn("conversion exception");
      return null;
    }
    String progressNums = thirdFlowGroupInfoResponse.getProgress();
    if (StringUtils.isNotBlank(progressNums)) {
      try {
        double progressNumsD = Double.parseDouble(progressNums);
        thirdFlowGroupInfoResponse.setProgress(String.format("%.2f", progressNumsD));
      } catch (Throwable e) {
        logger.warn("Progress conversion failed");
      }
    }
    return thirdFlowGroupInfoResponse;
  }

  /**
   * getFlowGroupProgress
   *
   * @param groupId
   * @return
   */
  @Override
  public Double getFlowGroupProgress(String groupId) {
    if (StringUtils.isBlank(groupId)) {
      logger.warn("groupId is null");
      return null;
    }
    Map<String, String> param = new HashMap<>();
    param.put("groupId", groupId);
    String doGet = HttpUtils.doGet(ApiConfig.getFlowGroupProgressUrl(), param, 5 * 1000);
    if (StringUtils.isBlank(doGet)) {
      logger.warn("The interface return value is empty.");
      return null;
    }
    if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
      logger.warn("Interface exception: " + doGet);
      return null;
    }
    try {
      return Double.parseDouble(doGet);
    } catch (Exception e) {
      logger.error("Conversion exception", e);
      return null;
    }
  }

  /**
   * update FlowGroup By Interface
   *
   * @param groupId
   * @throws Exception
   */
  @Override
  public void updateFlowGroupByInterface(String groupId) throws Exception {
    ThirdFlowGroupInfoResponse thirdFlowGroupInfoResponse = getFlowGroupInfo(groupId);
    Double flowGroupProgress = getFlowGroupProgress(groupId);
    // Determine if the progress returned by the interface is empty
    if (null == thirdFlowGroupInfoResponse) {
      return;
    }
    ProcessGroup processGroupByAppId = processGroupDomain.getProcessGroupByAppId(groupId);
    if (null == processGroupByAppId) {
      return;
    }
    processGroupByAppId =
        ThirdFlowGroupInfoResponseUtils.setProcessGroup(
            processGroupByAppId, thirdFlowGroupInfoResponse);
    if (null == flowGroupProgress || Double.isNaN(flowGroupProgress)) {
      flowGroupProgress = 0.0;
    } else if (Double.isInfinite(flowGroupProgress)) {
      flowGroupProgress = 100.0;
    }
    processGroupByAppId.setProgress(String.format("%.2f", flowGroupProgress));
    processGroupDomain.saveOrUpdateSyncTask(processGroupByAppId);
  }

  /**
   * update FlowGroups By Interface
   *
   * @param groupIds
   * @throws Exception
   */
  @Override
  public void updateFlowGroupsByInterface(List<String> groupIds) throws Exception {
    if (null != groupIds && groupIds.size() > 0) {
      for (String groupId : groupIds) {
        this.updateFlowGroupByInterface(groupId);
      }
    }
  }
}
