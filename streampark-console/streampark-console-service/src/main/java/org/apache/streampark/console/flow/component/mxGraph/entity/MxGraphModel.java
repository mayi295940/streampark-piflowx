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

package org.apache.streampark.console.flow.component.mxGraph.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MxGraphModel extends BaseModelUUIDNoCorpAgentId {

    /** */
    private static final long serialVersionUID = 1L;

    private Flow flow;
    private FlowGroup flowGroup;
    private Process process;
    private ProcessGroup processGroup;
    private String dx;
    private String dy;
    private String grid;
    private String gridSize;
    private String guides;
    private String tooltips;
    private String connect;
    private String arrows;
    private String fold;
    private String page;
    private String pageScale;
    private String pageWidth;
    private String pageHeight;
    private String background;
    private List<MxCell> root = new ArrayList<>();
}
