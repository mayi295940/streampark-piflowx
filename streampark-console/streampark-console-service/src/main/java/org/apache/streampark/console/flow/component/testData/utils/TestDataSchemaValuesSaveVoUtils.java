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

package org.apache.streampark.console.flow.component.testData.utils;

import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaValuesSaveVo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONObject;

public class TestDataSchemaValuesSaveVoUtils {

    public static TestDataSchemaValuesSaveVo StringToTestDataSchemaValuesSaveVo2(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        // Also convert the json string to a json object, and then convert the json object to a java
        // object, as shown below.
        // Convert a json string to a json object
        JSONObject obj = JSONObject.parseObject(json);
        // Needed when there is a List in jsonObj
        // Convert a json object to a java object
        return obj.toJavaObject(TestDataSchemaValuesSaveVo.class);
    }
}
