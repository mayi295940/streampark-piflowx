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

import org.apache.streampark.console.flow.component.flow.service.IFlowGroupPathsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "flowGroupPath api", tags = "flowGroupPath api")
@RestController
@RequestMapping("/flowGroupPath/")
public class FlowGroupPathCtrl {

    private final IFlowGroupPathsService flowGroupPathsServiceImpl;

    @Autowired
    public FlowGroupPathCtrl(IFlowGroupPathsService flowGroupPathsServiceImpl) {
        this.flowGroupPathsServiceImpl = flowGroupPathsServiceImpl;
    }

    /**
     * Query'path'according to'flowId' and'pageId'
     *
     * @param fid
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryPathInfoFlowGroup", method = RequestMethod.POST)
    @ApiOperation(value = "queryPathInfoFlowGroup", notes = "query Path info FlowGroup")
    public String queryPathInfoFlowGroup(String fid, String id) {
        return flowGroupPathsServiceImpl.queryPathInfoFlowGroup(fid, id);
    }
}
