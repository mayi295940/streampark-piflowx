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

package org.apache.streampark.console.flow.component.schedule.service;

import org.apache.streampark.console.flow.component.schedule.vo.ScheduleVo;

public interface IScheduleService {

    /**
     * Query getScheduleListPage (parameter space-time non-paging)
     *
     * @param isAdmin is admin
     * @param username username
     * @param offset Number of pages
     * @param limit Number each page
     * @param param Search content
     * @return json
     */
    public String getScheduleVoListPage(
                                        boolean isAdmin, String username, Integer offset, Integer limit, String param);

    /**
     * Add schedule
     *
     * @param username username
     * @param scheduleVo scheduleVo
     * @return json
     */
    public String addSchedule(String username, ScheduleVo scheduleVo);

    /**
     * get ScheduleVo by id
     *
     * @param isAdmin is admin
     * @param username username
     * @param id schedule id
     * @return json
     */
    public String getScheduleVoById(boolean isAdmin, String username, String id);

    /**
     * Update schedule
     *
     * @param isAdmin is admin
     * @param username username
     * @param scheduleVo scheduleVo
     * @return json
     */
    public String updateSchedule(boolean isAdmin, String username, ScheduleVo scheduleVo);

    /**
     * Delete schedule
     *
     * @param isAdmin is admin
     * @param username username
     * @param id schedule id
     * @return json
     */
    public String delSchedule(boolean isAdmin, String username, String id);

    /**
     * @param isAdmin is admin
     * @param username username
     * @param id schedule id
     * @return json
     */
    public String startSchedule(boolean isAdmin, String username, String id);

    /**
     * @param isAdmin is admin
     * @param username username
     * @param id schedule id
     * @return json
     */
    public String stopSchedule(boolean isAdmin, String username, String id);
}
