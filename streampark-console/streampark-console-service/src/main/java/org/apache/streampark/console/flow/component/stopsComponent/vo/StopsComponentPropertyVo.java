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

import lombok.Getter;
import lombok.Setter;

/**
 * TODO 组件属性(leader端接收follow端数据使用)
 *
 * @author leilei
 * @date 2022-08-02
 */
@Setter
@Getter
public class StopsComponentPropertyVo {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String displayName;
    private String description;
    private String defaultValue;
    private String allowableValues;
    private Boolean required;
    private Boolean sensitive;
    private Long propertySort;
    private String example;
    private String language;
}
