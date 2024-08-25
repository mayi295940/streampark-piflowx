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

package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 组件类型枚举，用于区分上传的算法包是什么类型和系统自带组件
 *
 * @author leilei
 * @date 2023-2-2
 */
@JsonSerialize(using = TextureEnumSerializer.class)
public enum ComponentFileType {

    DEFAULT("DEFAULT", "DEFAULT"), // 默认类型(系统自带组件)
    SCALA("SCALA", "SCALA"), // 上传的算法包为scala类型
    PYTHON("PYTHON", "PYTHON"); // 上传的算法包为python类型

    private final String value;
    private final String text;

    private ComponentFileType(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public static ComponentFileType selectGender(String name) {
        for (ComponentFileType portType : ComponentFileType.values()) {
            if (name.equalsIgnoreCase(portType.name())) {
                return portType;
            }
        }
        return null;
    }
}
