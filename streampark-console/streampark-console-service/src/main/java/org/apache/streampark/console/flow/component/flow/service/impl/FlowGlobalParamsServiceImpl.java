package org.apache.streampark.console.flow.component.flow.service.impl;

import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.flow.domain.FlowGlobalParamsDomain;
import org.apache.streampark.console.flow.component.flow.entity.FlowGlobalParams;
import org.apache.streampark.console.flow.component.flow.service.IFlowGlobalParamsService;
import org.apache.streampark.console.flow.component.flow.utils.FlowGlobalParamsUtils;
import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequestAdd;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowGlobalParamsServiceImpl implements IFlowGlobalParamsService {

  private final FlowGlobalParamsDomain flowGlobalParamsDomain;

  @Autowired
  public FlowGlobalParamsServiceImpl(FlowGlobalParamsDomain flowGlobalParamsDomain) {
    this.flowGlobalParamsDomain = flowGlobalParamsDomain;
  }

  @Override
  public String addFlowGlobalParams(String username, FlowGlobalParamsVoRequestAdd globalParamsVo)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == globalParamsVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    if (StringUtils.isBlank(globalParamsVo.getName())) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    FlowGlobalParams globalParams =
        FlowGlobalParamsUtils.setFlowGlobalParamsBasicInformation(null, true, username);
    // copy
    BeanUtils.copyProperties(globalParamsVo, globalParams);
    // set update info
    globalParams.setLastUpdateDttm(new Date());
    globalParams.setLastUpdateUser(username);
    int affectedRows = flowGlobalParamsDomain.addFlowGlobalParams(globalParams);
    if (affectedRows <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ADD_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("globalParamsId", globalParams.getId());
  }

  @Override
  public String updateFlowGlobalParams(
      String username, boolean isAdmin, FlowGlobalParamsVoRequest globalParamsVo) throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == globalParamsVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    String id = globalParamsVo.getId();
    if (StringUtils.isBlank(id)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    FlowGlobalParams globalParamsById =
        flowGlobalParamsDomain.getFlowGlobalParamsById(username, isAdmin, id);
    if (null == globalParamsById) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    // copy
    BeanUtils.copyProperties(globalParamsVo, globalParamsById);
    int affectedRows = flowGlobalParamsDomain.updateFlowGlobalParams(globalParamsById);
    if (affectedRows <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPDATE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
  }

  @Override
  public String deleteFlowGlobalParamsById(String username, boolean isAdmin, String id) {
    int affectedRows = flowGlobalParamsDomain.updateEnableFlagById(username, id, false);
    if (affectedRows <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DELETE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
  }

  /**
   * Paging query FlowGlobalParams
   *
   * @param username
   * @param isAdmin
   * @param offset Number of pages
   * @param limit Number of pages per page
   * @param param search for the keyword
   * @return
   */
  @Override
  public String getFlowGlobalParamsListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Page<FlowGlobalParams> page = PageHelper.startPage(offset, limit, "crt_dttm desc");
    flowGlobalParamsDomain.getFlowGlobalParamsListParam(username, isAdmin, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * Paging query FlowGlobalParams
   *
   * @param username
   * @param isAdmin
   * @param param search for the keyword
   * @return
   */
  @Override
  public String getFlowGlobalParamsList(String username, boolean isAdmin, String param) {
    List<FlowGlobalParams> flowGlobalParamsListParam =
        flowGlobalParamsDomain.getFlowGlobalParamsListParam(username, isAdmin, param);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("data", flowGlobalParamsListParam);
  }

  @Override
  public String getFlowGlobalParamsById(String username, boolean isAdmin, String id) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(id)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("id"));
    }
    FlowGlobalParams globalParamsById =
        flowGlobalParamsDomain.getFlowGlobalParamsById(username, isAdmin, id);
    if (null == globalParamsById) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_BY_ID_XXX_MSG(id));
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("globalParams", globalParamsById);
  }
}
