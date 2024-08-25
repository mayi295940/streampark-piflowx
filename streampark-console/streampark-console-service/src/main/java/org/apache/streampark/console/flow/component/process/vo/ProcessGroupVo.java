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

package org.apache.streampark.console.flow.component.process.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ProcessGroupVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String viewXml;
    private String description;
    private String pageId;
    private String flowId;
    private String appId;
    private String parentProcessId;
    private String processId;
    private ProcessState state;
    private Date startTime;
    private Date endTime;
    private String progress;
    private RunModeType runModeType = RunModeType.RUN;
    private Date crtDttm;
    private MxGraphModelVo mxGraphModelVo;
    private ProcessGroupVo processGroupVo;

    private List<ProcessVo> processVoList = new ArrayList<>();
    private List<ProcessGroupPathVo> processGroupPathVoList = new ArrayList<>();
    private List<ProcessGroupVo> processGroupVoList = new ArrayList<>();

    public String getCrtDttmStr() {
        return DateUtils.dateTimesToStr(this.crtDttm);
    }

    public String getStartTimeStr() {
        return DateUtils.dateTimesToStr(this.startTime);
    }

    public String getEndTimeStr() {
        return DateUtils.dateTimesToStr(this.endTime);
    }
}
