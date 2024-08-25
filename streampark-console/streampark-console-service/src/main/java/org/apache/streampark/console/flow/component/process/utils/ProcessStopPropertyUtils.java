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

import org.apache.streampark.console.flow.component.process.entity.ProcessStopProperty;

import java.util.Date;

public class ProcessStopPropertyUtils {

    public static ProcessStopProperty processStopPropertyNewNoId(String username) {
        ProcessStopProperty processStopProperty = new ProcessStopProperty();
        // basic properties (required when creating)
        processStopProperty.setCrtDttm(new Date());
        processStopProperty.setCrtUser(username);
        // basic properties
        processStopProperty.setEnableFlag(true);
        processStopProperty.setLastUpdateUser(username);
        processStopProperty.setLastUpdateDttm(new Date());
        processStopProperty.setVersion(0L);
        return processStopProperty;
    }

    public static ProcessStopProperty initProcessStopPropertyBasicPropertiesNoId(
                                                                                 ProcessStopProperty processStopProperty,
                                                                                 String username) {
        if (null == processStopProperty) {
            return processStopPropertyNewNoId(username);
        }
        // basic properties (required when creating)
        processStopProperty.setCrtDttm(new Date());
        processStopProperty.setCrtUser(username);
        // basic properties
        processStopProperty.setEnableFlag(true);
        processStopProperty.setLastUpdateUser(username);
        processStopProperty.setLastUpdateDttm(new Date());
        processStopProperty.setVersion(0L);
        return processStopProperty;
    }
}
