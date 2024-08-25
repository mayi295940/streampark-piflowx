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

package org.apache.streampark.console.flow.third.vo.flow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThirdFlowVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String driverMemory;
    private String executorNumber;
    private String executorMemory;
    private String executorCores;
    private String name;
    private String uuid;
    private String checkpoint;
    private String checkpointParentProcessId;
    private List<ThirdStopVo> stops = new ArrayList<ThirdStopVo>();
    private List<ThirdPathVo> paths = new ArrayList<ThirdPathVo>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public String getExecutorNumber() {
        return executorNumber;
    }

    public void setExecutorNumber(String executorNumber) {
        this.executorNumber = executorNumber;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ThirdStopVo> getStops() {
        return stops;
    }

    public String getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
    }

    public String getCheckpointParentProcessId() {
        return checkpointParentProcessId;
    }

    public void setCheckpointParentProcessId(String checkpointParentProcessId) {
        this.checkpointParentProcessId = checkpointParentProcessId;
    }

    public void setStops(List<ThirdStopVo> stops) {
        this.stops = stops;
    }

    public List<ThirdPathVo> getPaths() {
        return paths;
    }

    public void setPaths(List<ThirdPathVo> paths) {
        this.paths = paths;
    }
}
