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

package org.apache.streampark.console.flow.component.flow.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** stop component table */
@Getter
@Setter
public class Stops extends BaseModelUUIDNoCorpAgentId {

    private static final long serialVersionUID = 1L;

    private Flow flow;
    private String name;
    private String engineType;
    private String bundle;
    private String groups;
    private String owner;
    private String description;
    private String inports;
    private PortType inPortType;
    private String outports;
    private PortType outPortType;
    private String pageId;
    private String state;
    private Date startTime;
    private Date stopTime;
    private Boolean isCheckpoint;
    private Boolean isCustomized = false;
    private DataSource dataSource;
    private List<Property> properties = new ArrayList<>();
    private List<Property> oldProperties = new ArrayList<>();
    private List<CustomizedProperty> customizedPropertyList = new ArrayList<>();
    private Boolean isDataSource = false;
    private Boolean isDisabled = false;

    public String getStartTimes() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
        return startTime != null ? sdf.format(startTime) : "";
    }

    public String getStopTimes() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
        return stopTime != null ? sdf.format(stopTime) : "";
    }
}
