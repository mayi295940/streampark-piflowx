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

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchema;
import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaVoRequest;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;

import java.util.Date;

public class TestDataSchemaUtils {

    public static TestDataSchema setTestDataSchemaBasicInformation(
                                                                   TestDataSchema testDataSchema, boolean isSetId,
                                                                   String username) {
        if (null == testDataSchema) {
            testDataSchema = new TestDataSchema();
        }
        if (isSetId) {
            testDataSchema.setId(UUIDUtils.getUUID32());
        }
        // set MxGraphModel basic information
        testDataSchema.setCrtDttm(new Date());
        testDataSchema.setCrtUser(username);
        testDataSchema.setLastUpdateDttm(new Date());
        testDataSchema.setLastUpdateUser(username);
        testDataSchema.setVersion(0L);
        return testDataSchema;
    }

    /**
     * testDataSchemaVo data to testDataSchema
     *
     * @param testDataSchemaVo
     * @param testDataSchema
     * @return
     */
    public static TestDataSchema copyDataToTestDataSchema(
                                                          TestDataSchemaVoRequest testDataSchemaVo,
                                                          TestDataSchema testDataSchema, String username) {
        if (null == testDataSchemaVo || StringUtils.isBlank(username)) {
            return null;
        }
        if (null == testDataSchema) {
            return null;
        }
        // copy
        BeanUtils.copyProperties(testDataSchemaVo, testDataSchema);
        testDataSchema.setLastUpdateDttm(new Date());
        testDataSchema.setLastUpdateUser(username);
        return testDataSchema;
    }
}
