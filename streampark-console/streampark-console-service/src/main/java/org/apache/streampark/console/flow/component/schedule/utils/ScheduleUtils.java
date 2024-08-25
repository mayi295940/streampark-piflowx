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

package org.apache.streampark.console.flow.component.schedule.utils;

import org.apache.streampark.console.flow.component.schedule.entity.Schedule;

import java.util.Date;

public class ScheduleUtils {

    public static Schedule newScheduleNoId(String username) {

        Schedule schedule = new Schedule();
        // basic properties (required when creating)
        schedule.setCrtDttm(new Date());
        schedule.setCrtUser(username);
        // basic properties
        schedule.setEnableFlag(true);
        schedule.setLastUpdateUser(username);
        schedule.setLastUpdateDttm(new Date());
        schedule.setVersion(0L);
        return schedule;
    }
}
