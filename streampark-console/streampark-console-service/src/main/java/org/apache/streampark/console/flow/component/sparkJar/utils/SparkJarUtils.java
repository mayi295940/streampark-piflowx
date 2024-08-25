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

package org.apache.streampark.console.flow.component.sparkJar.utils;

import org.apache.streampark.console.flow.component.sparkJar.entity.SparkJarComponent;

import java.util.Date;

public class SparkJarUtils {

    public static SparkJarComponent sparkJarNewNoId(String username) {

        SparkJarComponent sparkJarComponent = new SparkJarComponent();
        // basic properties (required when creating)
        sparkJarComponent.setCrtDttm(new Date());
        sparkJarComponent.setCrtUser(username);
        // basic properties
        sparkJarComponent.setEnableFlag(true);
        sparkJarComponent.setLastUpdateUser(username);
        sparkJarComponent.setLastUpdateDttm(new Date());
        sparkJarComponent.setVersion(0L);
        return sparkJarComponent;
    }
}
