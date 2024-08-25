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

package org.apache.streampark.console.flow.controller.api.other;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.component.testData.service.ITestDataService;
import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaValuesSaveVo;
import org.apache.streampark.console.flow.controller.requestVo.TestDataVoRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "testData api", tags = "testData api")
@RestController
@RequestMapping(value = "/testData")
public class TestDataCtrl {

    private final ITestDataService testDataServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public TestDataCtrl(
                        ITestDataService testDataServiceImpl, ILogHelperService logHelperServiceImpl) {
        this.testDataServiceImpl = testDataServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping(value = "/saveOrUpdateTestDataSchema", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "saveOrUpdateTestDataSchema", notes = "save or update TestDataSchema")
    public String saveOrUpdateTestDataSchema(TestDataVoRequest testDataVo) throws Exception {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.saveOrUpdateTestDataAndSchema(
            currentUsername, isAdmin, testDataVo, false);
    }

    @RequestMapping(value = "/checkTestDataName", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "checkTestDataName", notes = "check TestData name")
    @ApiImplicitParam(name = "testDataName", value = "testDataName", required = true)
    public String checkTestDataName(String testDataName) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.checkTestDataName(currentUsername, isAdmin, testDataName);
    }

    @RequestMapping(value = "/delTestData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "delTestData", notes = "delete TestData")
    @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true)
    public String delTestData(String testDataId) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("delTestData " + testDataId, currentUsername);
        return testDataServiceImpl.delTestData(currentUsername, isAdmin, testDataId);
    }

    @RequestMapping(value = "/saveOrUpdateTestDataSchemaValues", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "saveOrUpdateTestDataSchemaValues", notes = "save or update TestDataSchemaValues")
    public String saveOrUpdateTestDataSchemaValues(TestDataSchemaValuesSaveVo schemaValuesVo) throws Exception {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed(
            "saveOrUpdateTestDataSchemaValues " + schemaValuesVo.getTestDataId(), currentUsername);
        return testDataServiceImpl.saveOrUpdateTestDataSchemaValues(
            currentUsername, isAdmin, schemaValuesVo);
    }

    @RequestMapping(value = "/testDataListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "testDataListPage", notes = "get TestData list page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", required = true),
            @ApiImplicitParam(name = "limit", value = "limit", required = true),
            @ApiImplicitParam(name = "param", value = "param")
    })
    public String testDataListPage(Integer page, Integer limit, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.getTestDataListPage(currentUsername, isAdmin, page, limit, param);
    }

    @RequestMapping(value = "/testDataSchemaListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "testDataSchemaListPage", notes = "get TestDataSchema list page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true),
            @ApiImplicitParam(name = "page", value = "page", required = true),
            @ApiImplicitParam(name = "limit", value = "limit", required = true),
            @ApiImplicitParam(name = "param", value = "param")
    })
    public String testDataSchemaLListPage(
                                          String testDataId, Integer page, Integer limit, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.getTestDataSchemaListPage(
            currentUsername, isAdmin, page, limit, param, testDataId);
    }

    @RequestMapping(value = "/testDataSchemaList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "testDataSchemaList", notes = "get TestDataSchema list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true),
            @ApiImplicitParam(name = "param", value = "param")
    })
    public String testDataSchemaList(String testDataId, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.getTestDataSchemaList(currentUsername, isAdmin, param, testDataId);
    }

    @RequestMapping(value = "/testDataSchemaValuesListPage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "testDataSchemaValuesListPage", notes = "get TestDataSchemaValues list page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true),
            @ApiImplicitParam(name = "page", value = "page", required = true),
            @ApiImplicitParam(name = "limit", value = "limit", required = true),
            @ApiImplicitParam(name = "param", value = "param")
    })
    public String testDataSchemaValuesListPage(
                                               String testDataId, Integer page, Integer limit, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.getTestDataSchemaValuesCustomListPage(
            currentUsername, isAdmin, page, limit, param, testDataId);
    }

    @RequestMapping(value = "/testDataSchemaValuesList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "testDataSchemaValuesList", notes = "get TestDataSchemaValues list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true),
            @ApiImplicitParam(name = "param", value = "param")
    })
    public String testDataSchemaValuesList(String testDataId, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return testDataServiceImpl.getTestDataSchemaValuesCustomList(
            currentUsername, isAdmin, param, testDataId);
    }

    @RequestMapping(value = "/uploadCsvFile", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "uploadCsvFile", notes = "upload csv file")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testDataId", value = "testDataId", required = true),
            @ApiImplicitParam(name = "header", value = "header", required = true),
            @ApiImplicitParam(name = "schema", value = "schema"),
            @ApiImplicitParam(name = "delimiter", value = "delimiter", required = true),
            @ApiImplicitParam(name = "file", value = "file", required = true, paramType = "formData", dataType = "file")
    })
    public String uploadCsvFile(
                                String testDataId,
                                boolean header,
                                String schema,
                                String delimiter,
                                @RequestParam("file") MultipartFile file) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("uploadCsvFile" + file.getName(), username);
        return testDataServiceImpl.uploadCsvFile(username, testDataId, header, schema, delimiter, file);
    }
}
