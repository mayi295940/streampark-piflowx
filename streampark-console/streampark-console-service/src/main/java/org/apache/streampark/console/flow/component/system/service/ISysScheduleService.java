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

package org.apache.streampark.console.flow.component.system.service;

import org.apache.streampark.console.flow.component.system.vo.SysScheduleVo;

import org.springframework.stereotype.Service;

@Service
public interface ISysScheduleService {

    /**
     * Paging query schedule
     *
     * @param offset Number of pages
     * @param limit Number of pages per page
     * @param param search for the keyword
     * @return
     */
    public String getScheduleListPage(
                                      String username, boolean isAdmin, Integer offset, Integer limit, String param);

    /**
     * Get schedule by id
     *
     * @param username
     * @param isAdmin
     * @param scheduleId
     * @return
     */
    public String getScheduleById(String username, boolean isAdmin, String scheduleId);

    /**
     * Add SysSchedule
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleVo
     * @return
     */
    public String createJob(String username, boolean isAdmin, SysScheduleVo sysScheduleVo);

    /**
     * Run once timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String runOnce(String username, boolean isAdmin, String sysScheduleId);

    /**
     * Start timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String startJob(String username, boolean isAdmin, String sysScheduleId);

    /**
     * Stop timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String stopJob(String username, boolean isAdmin, String sysScheduleId);

    /**
     * Pause timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String pauseJob(String username, boolean isAdmin, String sysScheduleId);

    /**
     * Resume timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String resume(String username, boolean isAdmin, String sysScheduleId);

    /**
     * Update timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleVo
     * @return
     */
    public String update(String username, boolean isAdmin, SysScheduleVo sysScheduleVo);

    /**
     * Delete timed task
     *
     * @param username
     * @param isAdmin
     * @param sysScheduleId
     * @return
     */
    public String deleteTask(String username, boolean isAdmin, String sysScheduleId);
}
