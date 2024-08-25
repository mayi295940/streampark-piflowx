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

package org.apache.streampark.console.flow.component.stopsComponent.vo;

import org.apache.streampark.console.flow.common.Eunm.PortType;

import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Getter
@Setter
public class StopsComponentVo {

    private String id;
    private String name;
    private String bundle;
    private String groups;
    private String owner;
    private String description;
    private String inports;
    private PortType inPortType;
    private String outports;
    private PortType outPortType;
    private String stopGroup;
    private Boolean isCustomized = false;
    private String visualizationType;
    private Boolean isShow = true;
    private Boolean isDataSource = false;
}
