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

package org.apache.streampark.console.flow.controller.api.flow;

import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;
import org.apache.streampark.console.flow.component.flow.service.IPathsService;
import org.apache.streampark.console.flow.component.flow.service.IPropertyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "path api", tags = "path api")
@RestController
@RequestMapping("/path")
public class PathCtrl {

    private final IPropertyService propertyServiceImpl;
    private final IPathsService pathsServiceImpl;

    @Autowired
    public PathCtrl(IPropertyService propertyServiceImpl, IPathsService pathsServiceImpl) {
        this.propertyServiceImpl = propertyServiceImpl;
        this.pathsServiceImpl = pathsServiceImpl;
    }

    @RequestMapping(value = "/queryPathInfo", method = RequestMethod.POST)
    @ApiOperation(value = "queryPathInfo", notes = "query Path info")
    public String getStopGroup(String fid, String id) {
        return pathsServiceImpl.getPathsByFlowIdAndPageId(fid, id);
    }

    @RequestMapping(value = "/savePathsPort", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "savePathsPort", notes = "save Paths port")
    public String savePathsPort(UpdatePathRequest updatePathRequest) {
        String username = SessionUserUtil.getCurrentUsername();
        return propertyServiceImpl.saveOrUpdateRoutePath(username, updatePathRequest);
    }
}
