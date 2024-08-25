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

package org.apache.streampark.console.flow.controller.requestVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FlowInfoVoRequestUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "flow id", required = true)
    private String id;

    @ApiModelProperty(value = "flow pageId")
    private String pageId;

    @ApiModelProperty(value = "flow name", required = true)
    private String name;

    @ApiModelProperty(value = "flow description")
    private String description; // description

    @ApiModelProperty(value = "flow driverMemory", required = true)
    private String driverMemory;

    @ApiModelProperty(value = "flow executorNumber", required = true)
    private String executorNumber;

    @ApiModelProperty(value = "flow executorMemory", required = true)
    private String executorMemory;

    @ApiModelProperty(value = "flow executorCores", required = true)
    private String executorCores;

    @ApiModelProperty(value = "flow globalParams ids")
    private String[] globalParamsIds;
}
