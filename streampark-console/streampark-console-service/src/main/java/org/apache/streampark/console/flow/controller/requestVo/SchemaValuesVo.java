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
@ApiModel(description = "save TestData")
public class SchemaValuesVo implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "schema name")
    private String schemaName;

    @ApiModelProperty(value = "schemaValues id")
    private String schemaValueId;

    @ApiModelProperty(value = "schema value")
    private String schemaValue;

    @ApiModelProperty(value = "schema value row", example = "0")
    private int dataRow;

    @ApiModelProperty(value = "delete or not")
    private boolean isDelete;
}
