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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel(description = "testDataSchema")
public class TestDataSchemaVoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "testDataSchema id")
    private String id;

    @ApiModelProperty(value = "testDataSchema fieldName")
    private String fieldName;

    @ApiModelProperty(value = "testDataSchema fieldType")
    private String fieldType;

    @ApiModelProperty(value = "testDataSchema fieldDescription")
    private String fieldDescription;

    @ApiModelProperty(value = "testDataSchema fieldSoft", example = "0")
    private int fieldSoft;

    @ApiModelProperty(value = "delete or not")
    private boolean isDelete;
}
