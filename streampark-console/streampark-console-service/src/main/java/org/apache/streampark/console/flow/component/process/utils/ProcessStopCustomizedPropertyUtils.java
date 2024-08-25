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

package org.apache.streampark.console.flow.component.process.utils;

import org.apache.streampark.console.flow.component.process.entity.ProcessStopCustomizedProperty;

import java.util.Date;

public class ProcessStopCustomizedPropertyUtils {

    public static ProcessStopCustomizedProperty processStopCustomizedPropertyNewNoId(
                                                                                     String username) {
        ProcessStopCustomizedProperty processStopCustomizedProperty =
            new ProcessStopCustomizedProperty();
        // basic properties (required when creating)
        processStopCustomizedProperty.setCrtDttm(new Date());
        processStopCustomizedProperty.setCrtUser(username);
        // basic properties
        processStopCustomizedProperty.setEnableFlag(true);
        processStopCustomizedProperty.setLastUpdateUser(username);
        processStopCustomizedProperty.setLastUpdateDttm(new Date());
        processStopCustomizedProperty.setVersion(0L);
        return processStopCustomizedProperty;
    }

    public static ProcessStopCustomizedProperty initProcessStopCustomizedPropertyBasicPropertiesNoId(
                                                                                                     ProcessStopCustomizedProperty processStopCustomizedProperty,
                                                                                                     String username) {
        if (null == processStopCustomizedProperty) {
            return processStopCustomizedPropertyNewNoId(username);
        }
        // basic properties (required when creating)
        processStopCustomizedProperty.setCrtDttm(new Date());
        processStopCustomizedProperty.setCrtUser(username);
        // basic properties
        processStopCustomizedProperty.setEnableFlag(true);
        processStopCustomizedProperty.setLastUpdateUser(username);
        processStopCustomizedProperty.setLastUpdateDttm(new Date());
        processStopCustomizedProperty.setVersion(0L);
        return processStopCustomizedProperty;
    }
}
