package org.apache.streampark.console.flow.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.PipelineSpringContextUtil;
import org.apache.streampark.console.flow.common.executor.ServicesExecutor;
import org.apache.streampark.console.flow.component.process.mapper.ProcessMapper;
import org.apache.streampark.console.flow.third.service.IFlow;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class RunningProcessSync extends QuartzJobBean {

  Logger logger = LoggerUtil.getLogger();

  @Autowired private ProcessMapper processMapper;

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
    logger.info("processSync start : " + formatter.format(new Date()));
    List<String> runningProcess = processMapper.getRunningProcessAppId();
    if (CollectionUtils.isNotEmpty(runningProcess)) {
      Runnable runnable =
          new Thread(
              new Thread() {
                @Override
                public void run() {
                  for (String appId : runningProcess) {
                    try {
                      IFlow getFlowInfoImpl = (IFlow) PipelineSpringContextUtil.getBean("flowImpl");
                      getFlowInfoImpl.getProcessInfoAndSave(appId);
                    } catch (Exception e) {
                      logger.error("errorMsg:", e);
                    }
                  }
                }
              });
      ServicesExecutor.getServicesExecutorServiceService().execute(runnable);
    }
    logger.info("processSync end : " + formatter.format(new Date()));
  }
}
