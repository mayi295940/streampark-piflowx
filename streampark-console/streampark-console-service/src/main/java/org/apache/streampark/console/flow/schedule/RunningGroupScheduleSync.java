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
import org.apache.streampark.console.flow.base.utils.SpringContextUtil;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;
import org.apache.streampark.console.flow.common.executor.ServicesExecutor;
import org.apache.streampark.console.flow.component.process.domain.ProcessDomain;
import org.apache.streampark.console.flow.component.process.domain.ProcessGroupDomain;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.utils.ProcessGroupUtils;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.component.schedule.domain.ScheduleDomain;
import org.apache.streampark.console.flow.component.schedule.entity.Schedule;
import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;
import org.apache.streampark.console.flow.third.service.ISchedule;
import org.apache.streampark.console.flow.third.vo.schedule.ThirdScheduleEntryVo;
import org.apache.streampark.console.flow.third.vo.schedule.ThirdScheduleVo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class RunningGroupScheduleSync extends QuartzJobBean {

    private final Logger logger = LoggerUtil.getLogger();

    @Autowired
    private ScheduleDomain scheduleDomain;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
        logger.info("groupScheduleSync start : " + formatter.format(new Date()));
        List<ScheduleVo> scheduleRunningList =
            scheduleDomain.getScheduleIdListByStateRunning(true, "sync");
        if (CollectionUtils.isNotEmpty(scheduleRunningList)) {
            ISchedule scheduleImpl = (ISchedule) SpringContextUtil.getBean("scheduleImpl");
            for (ScheduleVo scheduleVo : scheduleRunningList) {
                if (null == scheduleVo) {
                    continue;
                }
                ThirdScheduleVo thirdScheduleVo = scheduleImpl.scheduleInfo(scheduleVo.getScheduleId());
                Schedule scheduleById = scheduleDomain.getScheduleById(true, "sync", scheduleVo.getId());
                if ("STOPED".equals(thirdScheduleVo.getState())) {
                    scheduleById.setStatus(ScheduleState.STOP);
                    scheduleDomain.update(scheduleById);
                }
                List<ThirdScheduleEntryVo> entryList = thirdScheduleVo.getEntryList();
                if (CollectionUtils.isEmpty(entryList)) {
                    return;
                }
                ServicesExecutor.getServicesExecutorServiceService()
                    .execute(new ScheduleRunnable(scheduleVo.getScheduleProcessTemplateId(), entryList));
            }
        }
        logger.info("groupScheduleSync end : " + formatter.format(new Date()));
    }

    class ScheduleRunnable implements Runnable {

        private final String scheduleProcessTemplateId;
        private final List<ThirdScheduleEntryVo> entryList;

        public ScheduleRunnable(
                                String scheduleProcessTemplateId, List<ThirdScheduleEntryVo> entryList) {
            this.scheduleProcessTemplateId = scheduleProcessTemplateId;
            this.entryList = entryList;
        }

        @Override
        public void run() {
            try {
                ProcessDomain processDomain = (ProcessDomain) SpringContextUtil.getBean("processDomain");
                ProcessGroupDomain processGroupDomain =
                    (ProcessGroupDomain) SpringContextUtil.getBean("processGroupDomain");
                for (ThirdScheduleEntryVo thirdScheduleEntryVo : entryList) {
                    if (null == thirdScheduleEntryVo) {
                        continue;
                    }
                    String scheduleEntryType = thirdScheduleEntryVo.getScheduleEntryType();
                    if ("Flow".equals(scheduleEntryType)) {
                        String processIdByAppId =
                            processDomain.getProcessIdByAppId(
                                "sync", true, thirdScheduleEntryVo.getScheduleEntryId());
                        if (StringUtils.isNotBlank(processIdByAppId)) {
                            continue;
                        }
                        Process processById =
                            processDomain.getProcessById("sync", true, scheduleProcessTemplateId);
                        if (processById == null) {
                            logger.warn("sync failed");
                            continue;
                        }
                        // copy and Create
                        Process processCopy =
                            ProcessUtils.copyProcess(processById, "sync", RunModeType.RUN, true);
                        if (null == processCopy) {
                            logger.warn("sync failed");
                            continue;
                        }
                        try {
                            processCopy.setAppId(thirdScheduleEntryVo.getScheduleEntryId());
                            int addProcess = processDomain.addProcess(processCopy);
                            if (addProcess <= 0) {
                                logger.warn("sync failed");
                            }
                        } catch (Exception e) {
                            logger.error("error:", e);
                        }
                        continue;
                    }
                    List<String> processGroupIdByAppId =
                        processGroupDomain.getProcessGroupIdByAppId(
                            thirdScheduleEntryVo.getScheduleEntryId());
                    if (null != processGroupIdByAppId && processGroupIdByAppId.size() > 0) {
                        continue;
                    }
                    ProcessGroup processGroupById =
                        processGroupDomain.getProcessGroupById("sync", true, scheduleProcessTemplateId);
                    if (null == processGroupById) {
                        continue;
                    }
                    // copy and Create
                    ProcessGroup copyProcessGroup =
                        ProcessGroupUtils.copyProcessGroup(processGroupById, "sync", RunModeType.RUN, true);
                    if (null == copyProcessGroup) {
                        continue;
                    }
                    try {
                        copyProcessGroup.setAppId(thirdScheduleEntryVo.getScheduleEntryId());
                        int addProcessGroup = processGroupDomain.addProcessGroup(copyProcessGroup);
                        if (addProcessGroup <= 0) {
                            logger.warn("sync failed");
                        }
                    } catch (Exception e) {
                        logger.error("error:", e);
                    }
                }
            } catch (Exception e) {
                logger.error("update process group data error", e);
            }
        }
    }
}