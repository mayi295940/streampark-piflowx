package org.apache.streampark.console.flow.schedule;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.SpringContextUtil;
import org.apache.streampark.console.flow.common.executor.ServicesExecutor;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper;
import org.apache.streampark.console.flow.third.service.IGroup;

import org.apache.commons.collections.CollectionUtils;

import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Component
public class RunningProcessGroupSync extends QuartzJobBean {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private final Logger logger = LoggerUtil.getLogger();

  @Autowired private ProcessGroupMapper processGroupMapper;

  @SneakyThrows
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
    logger.info("processGroupSync start : " + formatter.format(new Date()));
    List<String> runningProcessGroup = processGroupMapper.getRunningProcessGroupAppId();
    if (CollectionUtils.isNotEmpty(runningProcessGroup)) {
      for (String groupId : runningProcessGroup) {
        Future<?> future = ServicesExecutor.TASK_FUTURE.get(groupId);
        if (null != future) {
          if (!future.isDone()) {
            continue;
          }
          ServicesExecutor.TASK_FUTURE.remove(groupId);
        }
        Future<?> submit =
            ServicesExecutor.getServicesExecutorServiceService()
                .submit(new ProcessGroupCallable(groupId));
        ServicesExecutor.TASK_FUTURE.put(groupId, submit);
      }
    }
    logger.info("processGroupSync end : " + formatter.format(new Date()));
  }

  class ProcessGroupCallable implements Callable<String> {
    private String groupId;

    public ProcessGroupCallable(String groupId) {
      this.groupId = groupId;
    }

    @Override
    public String call() throws Exception {
      IGroup groupImpl = (IGroup) SpringContextUtil.getBean("groupImpl");
      groupImpl.updateFlowGroupByInterface(groupId);
      return null;
    }
  }
}
