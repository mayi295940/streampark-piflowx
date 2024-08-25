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
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchemaValues;

import java.util.Date;

public class TestDataSchemaValuesUtils {

    /**
     * set TestDataSchemaValues baseInfo
     *
     * @param testDataSchemaValues
     * @param isSetId
     * @param username
     * @return
     */
    public static TestDataSchemaValues setTestDataSchemaBasicInformation(
                                                                         TestDataSchemaValues testDataSchemaValues,
                                                                         boolean isSetId, String username) {
        if (null == testDataSchemaValues) {
            testDataSchemaValues = new TestDataSchemaValues();
        }
        if (isSetId) {
            testDataSchemaValues.setId(UUIDUtils.getUUID32());
        }
        // set MxGraphModel basic information
        testDataSchemaValues.setCrtDttm(new Date());
        testDataSchemaValues.setCrtUser(username);
        testDataSchemaValues.setLastUpdateDttm(new Date());
        testDataSchemaValues.setLastUpdateUser(username);
        testDataSchemaValues.setVersion(0L);
        return testDataSchemaValues;
    }
}
