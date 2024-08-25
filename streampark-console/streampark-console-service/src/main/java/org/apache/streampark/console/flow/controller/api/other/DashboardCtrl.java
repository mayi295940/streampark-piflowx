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

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.component.dashboard.service.IFlowResourceService;
import org.apache.streampark.console.flow.component.dashboard.service.IStatisticService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(value = "dashboard api", tags = "dashboard api")
@RestController
@RequestMapping("/dashboard")
public class DashboardCtrl {

    private final IFlowResourceService resourceServiceImpl;
    private final IStatisticService statisticServiceImpl;

    @Autowired
    public DashboardCtrl(
                         IFlowResourceService resourceServiceImpl, IStatisticService statisticServiceImpl) {
        this.resourceServiceImpl = resourceServiceImpl;
        this.statisticServiceImpl = statisticServiceImpl;
    }

    /**
     * resource info,include cpu,memory,disk
     *
     * @return
     */
    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "resource", notes = "resource info")
    public String getResourceInfo() {
        String resourceInfo = resourceServiceImpl.getResourceInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("resourceInfo", resourceInfo);
    }

    /**
     * static
     *
     * @return
     */
    @RequestMapping(value = "/flowStatistic", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "flowStatistic", notes = "flow Statistic")
    public String getFlowStatisticInfo() {
        Map<String, String> flowResourceInfo = statisticServiceImpl.getFlowStatisticInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("flowResourceInfo", flowResourceInfo);
    }

    @RequestMapping(value = "/groupStatistic", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "groupStatistic", notes = "group Statistic")
    public String getGroupStatisticInfo() {
        Map<String, String> groupResourceInfo = statisticServiceImpl.getGroupStatisticInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("groupResourceInfo", groupResourceInfo);
    }

    @RequestMapping(value = "/scheduleStatistic", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "scheduleStatistic", notes = "schedule Statistic")
    public String getScheduleStatisticInfo() {
        Map<String, String> scheduleResourceInfo = statisticServiceImpl.getScheduleStatisticInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr(
            "scheduleResourceInfo", scheduleResourceInfo);
    }

    @RequestMapping(value = "/templateAndDataSourceStatistic", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "scheduleStatistic", notes = "schedule Statistic")
    public String getTemplateAndDataSourceStatisticInfo() {
        Map<String, String> templateAndDataSourceResourceInfo =
            statisticServiceImpl.getTemplateAndDataSourceStatisticInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr(
            "templateAndDataSourceResourceInfo", templateAndDataSourceResourceInfo);
    }

    @RequestMapping(value = "/stopStatistic", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "stopStatistic", notes = "stop Statistic")
    public String getStopStatisticInfo() {
        Map<String, String> stopResourceInfo = statisticServiceImpl.getStopStatisticInfo();
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("stopResourceInfo", stopResourceInfo);
    }
}
