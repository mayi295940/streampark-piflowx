package org.apache.streampark.console.flow.component.process.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.streampark.console.flow.component.process.mapper.ProcessMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.apache.streampark.console.flow.base.util.JsonUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.PageHelperUtils;
import org.apache.streampark.console.flow.base.util.ReturnMapUtils;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.mapper.ProcessAndProcessGroupMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper;
;
import org.apache.streampark.console.flow.component.process.service.IProcessAndProcessGroupService;
import org.apache.streampark.console.flow.component.process.utils.ProcessGroupUtils;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;
import org.apache.streampark.console.flow.component.process.vo.ProcessVo;

@Service
public class ProcessAndProcessGroupServiceImpl implements IProcessAndProcessGroupService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private ProcessAndProcessGroupMapper processAndProcessGroupMapper;

  @Resource private ProcessGroupMapper processGroupMapper;

  @Resource private ProcessMapper processMapper;

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
      return ReturnMapUtils.setFailedMsgRtnJsonStr(ReturnMapUtils.ERROR_MSG);
    }
    Page<Process> page = PageHelper.startPage(offset, limit, "crt_dttm desc");
    if (isAdmin) {
      processAndProcessGroupMapper.getProcessAndProcessGroupList(param);
    } else {
      processAndProcessGroupMapper.getProcessAndProcessGroupListByUser(param, username);
    }
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(ReturnMapUtils.SUCCEEDED_MSG);
    rtnMap = PageHelperUtils.setLayTableParam(page, rtnMap);
    return JsonUtils.toJsonNoException(rtnMap);
  }

  /**
   * getAppInfoList
   *
   * @param taskAppIds task appId array
   * @param groupAppIds group appId array
   * @return json
   */
  public String getAppInfoList(String[] taskAppIds, String[] groupAppIds) {
    if ((null == taskAppIds || taskAppIds.length <= 0)
        && (null == groupAppIds || groupAppIds.length <= 0)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Incoming parameter is null");
    }
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(ReturnMapUtils.SUCCEEDED_MSG);
    if (null != taskAppIds && taskAppIds.length > 0) {
      Map<String, Object> taskAppInfoMap = new HashMap<>();
      List<Process> processListByAppIDs = processMapper.getProcessListByAppIDs(taskAppIds);
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
          processGroupMapper.getProcessGroupListByAppIDs(groupAppIds);
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
    return JsonUtils.toJsonNoException(rtnMap);
  }
}
