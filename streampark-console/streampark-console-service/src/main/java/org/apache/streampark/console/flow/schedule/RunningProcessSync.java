/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.flow.schedule;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.common.executor.ServicesExecutor;
import org.apache.streampark.console.flow.component.process.domain.ProcessDomain;
import org.apache.streampark.console.flow.third.service.IFlow;

import org.apache.commons.collections.CollectionUtils;

import lombok.Getter;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class RunningProcessSync extends QuartzJobBean {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private final Logger logger = LoggerUtil.getLogger();

    private final ProcessDomain processDomain;
    private final IFlow flowImpl;

    @Autowired
    public RunningProcessSync(ProcessDomain processDomain, IFlow flowImpl) {
        this.processDomain = processDomain;
        this.flowImpl = flowImpl;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
        logger.info("processSync start : " + formatter.format(new Date()));
        List<String> runningProcess = processDomain.getRunningProcessAppId();
        if (CollectionUtils.isNotEmpty(runningProcess)) {

            for (String appId : runningProcess) {
                Future<?> future = ServicesExecutor.TASK_FUTURE.get(appId);
                if (null != future) {
                    if (!future.isDone()) {
                        continue;
                    }
                    ServicesExecutor.TASK_FUTURE.remove(appId);
                }
                Future<?> submit =
                    ServicesExecutor.getServicesExecutorServiceService().submit(new ProcessRunnable(appId));
                ServicesExecutor.TASK_FUTURE.put(appId, submit);
            }
        }
        logger.info("processSync end : " + formatter.format(new Date()));
    }

    @Getter
    class ProcessRunnable implements Runnable {

        private final String appId;

        public ProcessRunnable(String appId) {
            this.appId = appId;
        }

        @Override
        public void run() {
            try {
                flowImpl.getProcessInfoAndSave(appId);
            } catch (Exception e) {
                logger.error("update process data error", e);
            }
        }
    }
}
