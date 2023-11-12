package org.apache.streampark.console.flow.component.process.domain;

import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.UUIDUtils;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxGraphModelDomainU;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroupPath;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class ProcessGroupDomainU {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  Logger logger = LoggerUtil.getLogger();

  @Resource private ProcessGroupMapper processGroupMapper;

  @Resource private ProcessGroupPathMapper processGroupPathMapper;

  @Resource private ProcessDomainU processDomainU;

  @Resource private MxGraphModelDomainU mxGraphModelDomainU;

  /**
   * Add process of things
   *
   * @param processGroup processGroup
   * @return affected rows
   */
  public int addProcessGroup(ProcessGroup processGroup) throws Exception {
    if (null == processGroup) {
      return 0;
    }
    if (StringUtils.isBlank(processGroup.getId())) {
      processGroup.setId(UUIDUtils.getUUID32());
    }
    int addProcessGroupCounts = processGroupMapper.addProcessGroup(processGroup);
    if (addProcessGroupCounts <= 0) {
      throw new Exception("save failed");
    }
    // save path
    // Number of save Paths
    int addProcessGroupPathCounts = 0;
    List<ProcessGroupPath> processGroupPathList = processGroup.getProcessGroupPathList();
    if (null != processGroupPathList && processGroupPathList.size() > 0) {
      for (ProcessGroupPath processGroupPath : processGroupPathList) {
        processGroupPath.setProcessGroup(processGroup);
      }
      addProcessGroupPathCounts =
          processGroupPathMapper.addProcessGroupPathList(processGroupPathList);
    }
    // Save Process
    // Number of deposits in Stop
    int addProcessListCounts = 0;
    List<Process> processList = processGroup.getProcessList();
    if (null != processList && processList.size() > 0) {
      for (Process process : processList) {
        process.setProcessGroup(processGroup);
        addProcessListCounts += processDomainU.addProcess(process);
      }
    }
    // Save Process
    // Number of deposits in Stop
    int addProcessGroupListCounts = 0;
    List<ProcessGroup> processGroupList = processGroup.getProcessGroupList();
    if (null != processGroupList && processGroupList.size() > 0) {
      for (ProcessGroup processGroup_i : processGroupList) {
        addProcessGroupListCounts += this.addProcessGroup(processGroup_i);
      }
    }
    int addMxGraphModel = 0;
    MxGraphModel mxGraphModel = processGroup.getMxGraphModel();
    if (null != mxGraphModel) {
      processGroup.setProcessGroup(processGroup);
      addMxGraphModel = mxGraphModelDomainU.addMxGraphModel(mxGraphModel);
      if (addMxGraphModel <= 0) {
        throw new Exception("save failed");
      }
    }
    int influenceCounts =
        (addProcessGroupCounts
            + addProcessGroupPathCounts
            + addProcessListCounts
            + addProcessGroupListCounts
            + addMxGraphModel);
    return influenceCounts;
  }

  public List<String> getProcessGroupIdByAppId(String appId) {
    if (StringUtils.isBlank(appId)) {
      logger.warn("process id is null");
      return null;
    }
    return processGroupMapper.getProcessGroupIdByAppId(appId);
  }

  public ProcessGroup getProcessGroupById(String username, boolean isAdmin, String processGroupId) {
    if (StringUtils.isBlank(processGroupId) || StringUtils.isBlank(username)) {
      logger.warn("process id is null");
      return null;
    }
    return processGroupMapper.getProcessGroupByIdAndUser(username, isAdmin, processGroupId);
  }
}
