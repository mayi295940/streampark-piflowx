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

package org.apache.streampark.console.flow.component.flow.utils;

import org.apache.streampark.console.flow.component.flow.entity.FlowStopsPublishing;

import java.util.Date;

public class FlowStopsPublishingUtils {

    public static FlowStopsPublishing flowStopsPublishingNewNoId(String username) {

        FlowStopsPublishing flowStopsPublishing = new FlowStopsPublishing();
        // basic properties (required when creating)
        flowStopsPublishing.setCrtDttm(new Date());
        flowStopsPublishing.setCrtUser(username);
        // basic properties
        flowStopsPublishing.setEnableFlag(true);
        flowStopsPublishing.setLastUpdateUser(username);
        flowStopsPublishing.setLastUpdateDttm(new Date());
        flowStopsPublishing.setVersion(0L);
        return flowStopsPublishing;
    }

    public static FlowStopsPublishing initFlowStopsPublishingBasicPropertiesNoId(
                                                                                 FlowStopsPublishing flowStopsPublishing,
                                                                                 String username) {
        if (null == flowStopsPublishing) {
            return flowStopsPublishingNewNoId(username);
        }
        // basic properties (required when creating)
        flowStopsPublishing.setCrtDttm(new Date());
        flowStopsPublishing.setCrtUser(username);
        // basic properties
        flowStopsPublishing.setEnableFlag(true);
        flowStopsPublishing.setLastUpdateUser(username);
        flowStopsPublishing.setLastUpdateDttm(new Date());
        flowStopsPublishing.setVersion(0L);
        return flowStopsPublishing;
    }
}
