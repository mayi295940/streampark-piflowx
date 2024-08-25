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

package org.apache.streampark.console.flow.component.livy.util;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.livy.entity.CodeSnippet;

import java.util.Date;

public class CodeSnippetUtils {

    public static CodeSnippet setCodeSnippetBasicInformation(
                                                             CodeSnippet codeSnippet, boolean isSetId,
                                                             String username) {
        if (null == codeSnippet) {
            codeSnippet = new CodeSnippet();
        }
        if (isSetId) {
            codeSnippet.setId(UUIDUtils.getUUID32());
        } else {
            codeSnippet.setId(null);
        }
        // set MxGraphModel basic information
        codeSnippet.setCrtDttm(new Date());
        codeSnippet.setCrtUser(username);
        codeSnippet.setLastUpdateDttm(new Date());
        codeSnippet.setLastUpdateUser(username);
        codeSnippet.setVersion(0L);
        return codeSnippet;
    }
}
