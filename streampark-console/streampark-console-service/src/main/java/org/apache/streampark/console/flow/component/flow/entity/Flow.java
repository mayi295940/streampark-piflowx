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
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Flow extends BaseModelUUIDNoCorpAgentId {

    private static final long serialVersionUID = 1L;

    private String name;
    private String engineType;
    private String uuid;
    /** 保存执行参数 */
    private String environment;

    private String description;
    private String pageId;
    private Boolean isExample = false;
    private FlowGroup flowGroup;
    private MxGraphModel mxGraphModel;
    private List<Stops> stopsList = new ArrayList<>();
    private List<Paths> pathsList = new ArrayList<>();
    List<FlowGlobalParams> flowGlobalParamsList;
    private List<DataSource> dataSourceList = new ArrayList<>();
}
