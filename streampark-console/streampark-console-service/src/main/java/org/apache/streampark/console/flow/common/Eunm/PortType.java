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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum PortType {

    ANY("Any", "Any", "Any quantity"),
    DEFAULT("Default", "Default", "Default number 1"),
    USER_DEFAULT("UserDefault", "UserDefault", " 'stop' defines ports"),
    NONE("None", "None", "prohibit"),
    ROUTE("Route", "Route", "Routing port");

    private final String value;
    private final String text;
    private final String desc;

    private PortType(String text, String value, String desc) {
        this.text = text;
        this.value = value;
        this.desc = desc;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static PortType selectGender(String name) {
        if (StringUtils.isBlank(name)) {
            return PortType.DEFAULT;
        }
        for (PortType portType : PortType.values()) {
            if (name.equalsIgnoreCase(portType.name())) {
                return portType;
            }
        }
        return null;
    }

    public static PortType selectGenderByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return PortType.DEFAULT;
        }
        for (PortType portType : PortType.values()) {
            if (value.equalsIgnoreCase(portType.value)) {
                return portType;
            }
        }
        return null;
    }
}
