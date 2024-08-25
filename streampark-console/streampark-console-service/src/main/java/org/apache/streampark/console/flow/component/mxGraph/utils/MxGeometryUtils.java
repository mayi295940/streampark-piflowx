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

package org.apache.streampark.console.flow.component.mxGraph.utils;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;

import java.util.Date;

public class MxGeometryUtils {

    public static MxGeometry mxGeometryNewNoId(String username) {

        MxGeometry mxGeometry = new MxGeometry();
        // basic properties (required when creating)
        mxGeometry.setCrtDttm(new Date());
        mxGeometry.setCrtUser(username);
        // basic properties
        mxGeometry.setEnableFlag(true);
        mxGeometry.setLastUpdateUser(username);
        mxGeometry.setLastUpdateDttm(new Date());
        mxGeometry.setVersion(0L);
        return mxGeometry;
    }

    public static MxGeometry initMxGeometryBasicPropertiesNoId(
                                                               MxGeometry mxGeometry, String username) {
        if (null == mxGeometry) {
            return mxGeometryNewNoId(username);
        }
        // basic properties (required when creating)
        mxGeometry.setCrtDttm(new Date());
        mxGeometry.setCrtUser(username);
        // basic properties
        mxGeometry.setEnableFlag(true);
        mxGeometry.setLastUpdateUser(username);
        mxGeometry.setLastUpdateDttm(new Date());
        mxGeometry.setVersion(0L);
        return mxGeometry;
    }
}
