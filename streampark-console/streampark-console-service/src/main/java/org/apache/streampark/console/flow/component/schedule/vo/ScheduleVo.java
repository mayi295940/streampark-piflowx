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

package org.apache.streampark.console.flow.component.schedule.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ScheduleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Date crtDttm;
    private String scheduleId;
    private String type;
    private ScheduleState status;
    private String cronExpression;
    private Date planStartTime;
    private Date planEndTime;
    private String scheduleProcessTemplateId;
    private String scheduleRunTemplateId;
    private String scheduleRunTemplateName;

    public String getCrtDttmStr() {
        return DateUtils.dateTimesToStr(this.crtDttm);
    }

    public String getPlanStartTimeStr() {
        return DateUtils.dateTimesToStr(this.planStartTime);
    }

    public void setPlanStartTimeStr(String planStartTimeStr) {
        this.planStartTime = DateUtils.strToTime(planStartTimeStr);
    }

    public String getPlanEndTimeStr() {
        return DateUtils.dateTimesToStr(this.planEndTime);
    }

    public void setPlanEndTimeStr(String planEndTimeStr) {
        this.planEndTime = DateUtils.strToTime(planEndTimeStr);
    }
}
