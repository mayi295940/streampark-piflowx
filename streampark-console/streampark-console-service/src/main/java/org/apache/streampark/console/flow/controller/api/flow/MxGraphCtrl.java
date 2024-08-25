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

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.flow.service.IFlowGroupService;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxGraphModelService;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxNodeImageService;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphVo;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "mxGraph api", tags = "mxGraph api")
@Controller
@RequestMapping("/mxGraph")
public class MxGraphCtrl {

    private final IFlowGroupService flowGroupServiceImpl;
    private final IMxGraphModelService mxGraphModelServiceImpl;
    private final IMxNodeImageService mxNodeImageServiceImpl;
    private final ILogHelperService logHelperServiceImpl;

    @Autowired
    public MxGraphCtrl(
                       IFlowGroupService flowGroupServiceImpl,
                       IMxGraphModelService mxGraphModelServiceImpl,
                       IMxNodeImageService mxNodeImageServiceImpl,
                       ILogHelperService logHelperServiceImpl) {
        this.flowGroupServiceImpl = flowGroupServiceImpl;
        this.mxGraphModelServiceImpl = mxGraphModelServiceImpl;
        this.mxNodeImageServiceImpl = mxNodeImageServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
    }

    @RequestMapping(value = "/saveDataForTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "saveDataForTask", notes = "save Data for task")
    public String saveDataForTask(String imageXML, String load, String operType) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("saveDataForTask" + load, username);
        return mxGraphModelServiceImpl.saveDataForTask(username, imageXML, load, operType);
    }

    @RequestMapping(value = "/saveDataForGroup", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "saveDataForGroup", notes = "save Data for group")
    public String saveDataForGroup(String imageXML, String load, String operType) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("saveDataForGroup " + load, username);
        return mxGraphModelServiceImpl.saveDataForGroup(username, imageXML, load, operType, true);
    }

    @RequestMapping(value = "/addMxCellAndData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "addMxCellAndData", notes = "add MxCell and data")
    public String addMxCellAndData(@RequestBody MxGraphVo mxGraphVo) throws Exception {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed(
            "addMxCellAndData" + mxGraphVo.getLoadId(), currentUsername);
        return mxGraphModelServiceImpl.addMxCellAndData(mxGraphVo, currentUsername);
    }

    @RequestMapping(value = "/uploadNodeImage", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "uploadNodeImage", notes = "upload NodeImage")
    public String uploadNodeImage(
                                  @RequestParam("file") MultipartFile file, String imageType,
                                  String nodeEngineType) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        logHelperServiceImpl.logAuthSucceed("uploadNodeImage " + file.getName(), username);
        return mxNodeImageServiceImpl.uploadNodeImage(username, file, imageType, nodeEngineType);
    }

    @RequestMapping(value = "/nodeImageList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "nodeImageList", notes = "NodeImage list")
    public String nodeImageList(String imageType) {
        String username = SessionUserUtil.getCurrentUsername();
        return mxNodeImageServiceImpl.getMxNodeImageList(username, imageType);
    }

    @RequestMapping(value = "/groupRightRun", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "groupRightRun", notes = "group right run")
    public String groupRightRun(String pId, String nodeId, String nodeType) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("groupRightRun" + nodeId, username);
        return flowGroupServiceImpl.rightRun(username, isAdmin, pId, nodeId, nodeType);
    }

    @RequestMapping(value = "/eraseRecord", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "eraseRecord", notes = "eraseRecord")
    public String eraseRecord() {
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("flag", true);
    }
}
