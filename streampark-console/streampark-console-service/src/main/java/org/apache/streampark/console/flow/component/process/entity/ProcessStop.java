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

package org.apache.streampark.console.flow.component.process.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ComponentFileType;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.Eunm.StopState;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ProcessStop extends BaseModelUUIDNoCorpAgentId {

    private static final long serialVersionUID = 1L;

    private Process process;
    private String name;
    private String bundle;
    private String groups;
    private String owner;
    private String description;
    private String inports;
    private PortType inPortType;
    private String outports;
    private PortType outPortType;
    private StopState state = StopState.INIT;
    private Date startTime;
    private Date endTime;
    private String pageId;
    private List<ProcessStopProperty> processStopPropertyList = new ArrayList<>();
    private List<ProcessStopCustomizedProperty> processStopCustomizedPropertyList = new ArrayList<>();
    private Boolean isDataSource = false;

    private String dockerImagesName; // docker image name,not save in process_stop
    private ComponentFileType componentType; // component type,not save in process_stop
}
