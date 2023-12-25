package org.apache.streampark.console.flow.component.process.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.domain.ProcessGroupDomain;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.service.IProcessAndProcessGroupService;
import org.apache.streampark.console.flow.component.process.utils.ProcessGroupUtils;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;
import org.apache.streampark.console.flow.component.process.vo.ProcessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessAndProcessGroupServiceImpl implements IProcessAndProcessGroupService {

  private final ProcessGroupDomain processGroupDomain;

  @Autowired
  public ProcessAndProcessGroupServiceImpl(ProcessGroupDomain processGroupDomain) {

    this.processGroupDomain = processGroupDomain;
  }

  /**
   * Query ProcessAndProcessGroupList (parameter space-time non-paging)
   *
   * @param offset Number of pages
   * @param limit Number each page
   * @param param Search content
   * @return json
   */
  @Override
  public String getProcessAndProcessGroupListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Page<Process> page = PageHelper.startPage(offset, limit, "crt_dttm desc");
    if (isAdmin) {
      processGroupDomain.getProcessAndProcessGroupList(param);
    } else {
      processGroupDomain.getProcessAndProcessGroupListByUser(param, username);
    }
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * getAppInfoList
   *
   * @param taskAppIds task appId array
   * @param groupAppIds group appId array
   * @return json
   */
  @Override
  public String getAppInfoList(String[] taskAppIds, String[] groupAppIds) {
    if ((null == taskAppIds || taskAppIds.length == 0)
        && (null == groupAppIds || groupAppIds.length == 0)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    if (null != taskAppIds && taskAppIds.length > 0) {
      Map<String, Object> taskAppInfoMap = new HashMap<>();
      List<Process> processListByAppIDs = processGroupDomain.getProcessListByAppIDs(taskAppIds);
      if (CollectionUtils.isNotEmpty(processListByAppIDs)) {
        for (Process process : processListByAppIDs) {
          ProcessVo processVo = ProcessUtils.processPoToVo(process);
          if (null == processVo) {
            continue;
          }
          taskAppInfoMap.put(processVo.getAppId(), processVo);
        }
      }
      rtnMap.put("taskAppInfo", taskAppInfoMap);
    }
    if (null != groupAppIds && groupAppIds.length > 0) {
      Map<String, Object> groupAppInfoMap = new HashMap<>();
      List<ProcessGroup> processGroupListByAppIDs =
          processGroupDomain.getProcessGroupListByAppIDs(groupAppIds);
      if (CollectionUtils.isNotEmpty(processGroupListByAppIDs)) {
        for (ProcessGroup processGroup : processGroupListByAppIDs) {
          ProcessGroupVo processGroupVo = ProcessGroupUtils.processGroupPoToVo(processGroup);
          if (null == processGroupVo) {
            continue;
          }
          groupAppInfoMap.put(processGroupVo.getAppId(), processGroupVo);
        }
      }
      rtnMap.put("groupAppInfo", groupAppInfoMap);
    }
    return ReturnMapUtils.toJson(rtnMap);
  }
}
