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

package org.apache.streampark.console.flow.component.stopsComponent.utils;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;

import java.util.Date;

public class StopsComponentGroupUtils {

    public static StopsComponentGroup stopsComponentGroupNewNoId(String username) {

        StopsComponentGroup stopsComponentGroup = new StopsComponentGroup();
        // basic properties (required when creating)
        stopsComponentGroup.setCrtDttm(new Date());
        stopsComponentGroup.setCrtUser(username);
        // basic properties
        stopsComponentGroup.setEnableFlag(true);
        stopsComponentGroup.setLastUpdateUser(username);
        stopsComponentGroup.setLastUpdateDttm(new Date());
        stopsComponentGroup.setVersion(0L);
        return stopsComponentGroup;
    }

    public static StopsComponentGroup initStopsComponentGroupBasicPropertiesNoId(
                                                                                 StopsComponentGroup stopsComponentGroup,
                                                                                 String username) {
        if (null == stopsComponentGroup) {
            return stopsComponentGroupNewNoId(username);
        }
        // basic properties (required when creating)
        stopsComponentGroup.setCrtDttm(new Date());
        stopsComponentGroup.setCrtUser(username);
        // basic properties
        stopsComponentGroup.setEnableFlag(true);
        stopsComponentGroup.setLastUpdateUser(username);
        stopsComponentGroup.setLastUpdateDttm(new Date());
        stopsComponentGroup.setVersion(0L);
        return stopsComponentGroup;
    }
}
