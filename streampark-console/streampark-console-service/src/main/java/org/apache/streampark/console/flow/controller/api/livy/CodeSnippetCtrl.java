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

package org.apache.streampark.console.flow.controller.api.livy;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.livy.service.ICodeSnippetService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.apache.streampark.console.flow.controller.requestVo.CodeSnippetVoRequestAdd;
import org.apache.streampark.console.flow.controller.requestVo.CodeSnippetVoRequestUpdate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "noteBoot api", tags = "noteBoot api")
@RestController
@RequestMapping(value = "/codeSnippet")
public class CodeSnippetCtrl {

    private final ICodeSnippetService codeSnippetServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public CodeSnippetCtrl(
                           ICodeSnippetService codeSnippetServiceImpl, ILogHelperService logHelperServiceImpl) {
        this.codeSnippetServiceImpl = codeSnippetServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping(value = "/addCodeSnippet", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "addCodeSnippet", notes = "add code snippet")
    public String addCodeSnippet(CodeSnippetVoRequestAdd codeSnippetVo) throws Exception {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        // boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed(
            "addCodeSnippet" + codeSnippetVo.getNoteBookId(), currentUsername);
        return codeSnippetServiceImpl.addCodeSnippet(currentUsername, codeSnippetVo);
    }

    @RequestMapping(value = "/updateCodeSnippet", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "updateCodeSnippet", notes = "update code snippet")
    public String updateCodeSnippet(CodeSnippetVoRequestUpdate codeSnippetVo) throws Exception {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed(
            "updateCodeSnippet " + codeSnippetVo.getCodeSnippetSort(), currentUsername);
        return codeSnippetServiceImpl.updateCodeSnippet(currentUsername, codeSnippetVo);
    }

    @RequestMapping(value = "/deleteCodeSnippet", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "deleteCodeSnippet", notes = "delete code snippet")
    @ApiImplicitParam(name = "codeSnippetId", value = "codeSnippet id", required = true)
    public String delCodeSnippet(String codeSnippetId) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("deleteCodeSnippet " + codeSnippetId, username);
        return codeSnippetServiceImpl.delCodeSnippet(currentUsername, isAdmin, codeSnippetId);
    }

    @RequestMapping(value = "/codeSnippetList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "codeSnippetList", notes = "code snippet list")
    @ApiImplicitParam(name = "noteBookId", value = "noteBookId", required = true, paramType = "query")
    public String codeSnippetList(String noteBookId) {
        return codeSnippetServiceImpl.getCodeSnippetList(noteBookId);
    }

    @RequestMapping(value = "/runStatements", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "runStatements", notes = "run statements")
    @ApiImplicitParam(name = "codeSnippetId", value = "codeSnippet id", required = true, paramType = "query")
    public String runStatements(String codeSnippetId) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        return codeSnippetServiceImpl.runCodeSnippet(currentUsername, codeSnippetId);
    }

    @RequestMapping(value = "/getStatementsResult", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getStatementsResult", notes = "get statements result")
    @ApiImplicitParam(name = "codeSnippetId", value = "codeSnippet id", required = true, paramType = "query")
    public String getStatementsResult(String codeSnippetId) {
        return codeSnippetServiceImpl.getStatementsResult(codeSnippetId);
    }
}
