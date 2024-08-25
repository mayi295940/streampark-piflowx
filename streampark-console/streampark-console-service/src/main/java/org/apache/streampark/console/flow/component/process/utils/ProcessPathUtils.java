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

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.process.entity.ProcessPath;

import org.springframework.beans.BeanUtils;

import java.util.Date;

public class ProcessPathUtils {

    public static ProcessPath processPathNewNoId(String username) {
        ProcessPath processPath = new ProcessPath();
        // basic properties (required when creating)
        processPath.setCrtDttm(new Date());
        processPath.setCrtUser(username);
        // basic properties
        processPath.setEnableFlag(true);
        processPath.setLastUpdateUser(username);
        processPath.setLastUpdateDttm(new Date());
        processPath.setVersion(0L);
        return processPath;
    }

    public static ProcessPath initProcessPathBasicPropertiesNoId(
                                                                 ProcessPath processPath, String username) {
        if (null == processPath) {
            return processPathNewNoId(username);
        }
        // basic properties (required when creating)
        processPath.setCrtDttm(new Date());
        processPath.setCrtUser(username);
        // basic properties
        processPath.setEnableFlag(true);
        processPath.setLastUpdateUser(username);
        processPath.setLastUpdateDttm(new Date());
        processPath.setVersion(0L);
        return processPath;
    }

    public static ProcessPath copyProcessPathBasicPropertiesNoIdAndUnlink(
                                                                          ProcessPath processPath, String username) {
        if (null == processPath) {
            return null;
        }
        // ProcessPath
        ProcessPath copyProcessPath = new ProcessPath();
        BeanUtils.copyProperties(processPath, copyProcessPath);
        copyProcessPath = initProcessPathBasicPropertiesNoId(copyProcessPath, username);
        copyProcessPath.setProcess(null);
        return copyProcessPath;
    }

    public static ProcessPath pathsToProcessPath(Paths paths, String username, boolean isAddId) {

        // isEmpty
        if (null == paths) {
            return null;
        }
        ProcessPath processPath = new ProcessPath();
        // Copy paths information into processPath
        BeanUtils.copyProperties(paths, processPath);
        // Set basic information
        processPath = ProcessPathUtils.initProcessPathBasicPropertiesNoId(processPath, username);
        if (isAddId) {
            processPath.setId(UUIDUtils.getUUID32());
        } else {
            processPath.setId(null);
        }

        return processPath;
    }
}
