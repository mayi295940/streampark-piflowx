package org.apache.streampark.console.flow.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.PipelineSpringContextUtil;
import org.apache.streampark.console.flow.common.executor.ServicesExecutor;
import org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper;
import org.apache.streampark.console.flow.third.service.IGroup;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class RunningProcessGroupSync extends QuartzJobBean {

  Logger logger = LoggerUtil.getLogger();

  @Autowired private ProcessGroupMapper processGroupMapper;

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
    logger.info("processGroupSync start : " + formatter.format(new Date()));
    List<String> runningProcessGroup = processGroupMapper.getRunningProcessGroupAppId();
    if (CollectionUtils.isNotEmpty(runningProcessGroup)) {
      Runnable runnable =
          new Thread(
              new Thread() {
                @Override
                public void run() {
                  try {
                    IGroup groupImpl = (IGroup) PipelineSpringContextUtil.getBean("groupImpl");
                    groupImpl.updateFlowGroupsByInterface(runningProcessGroup);
                  } catch (Exception e) {
                    logger.error("errorMsg:", e);
                  }
                }
              });
      ServicesExecutor.getServicesExecutorServiceService().execute(runnable);
    }
    logger.info("processGroupSync end : " + formatter.format(new Date()));
  }
}
