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

package org.apache.streampark.console.flow.component.testData.service;

import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaValuesSaveVo;
import org.apache.streampark.console.flow.controller.requestVo.TestDataVoRequest;

import org.springframework.web.multipart.MultipartFile;

public interface ITestDataService {

    /**
     * saveOrUpdateTestDataSchema
     *
     * @param username
     * @param isAdmin
     * @param testDataVo
     * @return String
     * @throws Exception
     */
    public String saveOrUpdateTestDataAndSchema(
                                                String username, boolean isAdmin, TestDataVoRequest testDataVo,
                                                boolean flag) throws Exception;

    /**
     * saveOrUpdateTestDataSchemaValues
     *
     * @param username
     * @param isAdmin
     * @param schemaValuesVo
     * @return String
     * @throws Exception
     */
    public String saveOrUpdateTestDataSchemaValues(
                                                   String username, boolean isAdmin,
                                                   TestDataSchemaValuesSaveVo schemaValuesVo) throws Exception;

    /**
     * checkTestDataName
     *
     * @param username
     * @param isAdmin
     * @param testDataName
     * @return String
     */
    public String checkTestDataName(String username, boolean isAdmin, String testDataName);

    /**
     * delTestData
     *
     * @param username
     * @param isAdmin
     * @param testDataId
     * @return String
     */
    public String delTestData(String username, boolean isAdmin, String testDataId);

    /**
     * getTestDataList
     *
     * @param username
     * @param isAdmin
     * @param offset
     * @param limit
     * @param param
     * @return String
     */
    public String getTestDataListPage(
                                      String username, boolean isAdmin, Integer offset, Integer limit, String param);

    /**
     * getTestDataSchemaListPage
     *
     * @param username
     * @param isAdmin
     * @param offset
     * @param limit
     * @param param
     * @param testDataId
     * @return String
     */
    public String getTestDataSchemaListPage(
                                            String username,
                                            boolean isAdmin,
                                            Integer offset,
                                            Integer limit,
                                            String param,
                                            String testDataId);

    /**
     * getTestDataSchemaList
     *
     * @param username
     * @param isAdmin
     * @param param
     * @param testDataId
     * @return String
     */
    public String getTestDataSchemaList(
                                        String username, boolean isAdmin, String param, String testDataId);

    /**
     * getTestDataSchemaValuesCustomListPage
     *
     * @param username
     * @param isAdmin
     * @param offset
     * @param limit
     * @param param
     * @param testDataId
     * @return
     */
    public String getTestDataSchemaValuesCustomListPage(
                                                        String username,
                                                        boolean isAdmin,
                                                        Integer offset,
                                                        Integer limit,
                                                        String param,
                                                        String testDataId);

    /**
     * getTestDataSchemaValuesCustomList
     *
     * @param username
     * @param isAdmin
     * @param param
     * @param testDataId
     * @return String
     */
    public String getTestDataSchemaValuesCustomList(
                                                    String username, boolean isAdmin, String param, String testDataId);

    /**
     * Upload csv file and save flowTemplate
     *
     * @param username
     * @param testDataId
     * @param header
     * @param schema
     * @param delimiter
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadCsvFile(
                                String username,
                                String testDataId,
                                boolean header,
                                String schema,
                                String delimiter,
                                MultipartFile file) throws Exception;
}
