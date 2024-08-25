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
import org.apache.streampark.console.flow.component.dataSource.service.IDataSource;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;
import org.apache.streampark.console.flow.component.flow.service.IStopsService;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsComponentService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "datasource api", tags = "datasource api")
@Controller
@RequestMapping("/datasource")
public class DataSourceCtrl {

    private final IDataSource dataSourceImpl;
    private final IStopsService stopsServiceImpl;
    private final ILogHelperService logHelperServiceImpl;
    private final IStopsComponentService stopsComponentServiceImpl;

    @Autowired
    public DataSourceCtrl(
                          IDataSource dataSourceImpl,
                          IStopsService stopsServiceImpl,
                          ILogHelperService logHelperServiceImpl,
                          IStopsComponentService stopsComponentServiceImpl) {
        this.dataSourceImpl = dataSourceImpl;
        this.stopsServiceImpl = stopsServiceImpl;
        this.logHelperServiceImpl = logHelperServiceImpl;
        this.stopsComponentServiceImpl = stopsComponentServiceImpl;
    }

    @RequestMapping(value = "/getDatasourceList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDatasourceList", notes = "Get Datasource list")
    public String getDatasourceList() {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return dataSourceImpl.getDataSourceVoList(currentUsername, isAdmin);
    }

    @RequestMapping(value = "/getDataSourceListPagination", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "getDataSourceListPagination", notes = "Get Datasource list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "limit", required = true, paramType = "query"),
            @ApiImplicitParam(name = "param", value = "param", paramType = "query")
    })
    public String getDataSourceListPagination(Integer page, Integer limit, String param) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return dataSourceImpl.getDataSourceVoListPage(currentUsername, isAdmin, page, limit, param);
    }

    @RequestMapping(value = "/getDatasourceById", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDatasourceById", notes = "Get Datasource by id")
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query")
    public String getDatasourceById(String id) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return dataSourceImpl.getDataSourceVoById(currentUsername, isAdmin, id);
    }

    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "saveOrUpdate", notes = "save or update DataSource")
    public String saveOrUpdate(DataSourceVo dataSourceVo, boolean isSynchronize) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed(
            "saveOrUpdate" + dataSourceVo.getDataSourceName(), currentUsername);
        return dataSourceImpl.saveOrUpdate(currentUsername, isAdmin, dataSourceVo, isSynchronize);
    }

    @RequestMapping(value = "/getDataSourceInputData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDataSourceInputData", notes = "get DataSource input data")
    @ApiImplicitParam(name = "dataSourceId", value = "dataSourceId", required = true, paramType = "query")
    public String getDataSourceInputPageData(String dataSourceId) {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return dataSourceImpl.getDataSourceInputPageData(username, isAdmin, dataSourceId);
    }

    @RequestMapping(value = "/deleteDataSource", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "deleteDataSource", notes = "delete DataSource")
    @ApiImplicitParam(name = "dataSourceId", value = "dataSourceId", required = true, paramType = "query")
    public String deleteDataSource(String dataSourceId) {
        String currentUsername = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        logHelperServiceImpl.logAuthSucceed("deleteDataSource" + dataSourceId, currentUsername);
        return dataSourceImpl.deleteDataSourceById(currentUsername, isAdmin, dataSourceId);
    }

    @RequestMapping(value = "/fillDatasource", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "fillDatasource", notes = "fill DataSource")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataSourceId", value = "dataSourceId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stopId", value = "stopId", required = true, paramType = "query")
    })
    public String fillDatasource(String dataSourceId, String stopId) throws Exception {
        String username = SessionUserUtil.getCurrentUsername();
        boolean isAdmin = SessionUserUtil.isAdmin();
        return stopsServiceImpl.fillDatasource(username, isAdmin, dataSourceId, stopId);
    }

    @RequestMapping(value = "/checkDatasourceLinked", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "checkDatasourceLinked", notes = "check DataSource linked")
    @ApiImplicitParam(name = "dataSourceId", value = "dataSourceId", required = true, paramType = "query")
    public String checkDatasourceLinked(String dataSourceId) throws Exception {
        return stopsServiceImpl.checkDatasourceLinked(dataSourceId);
    }

    /** stopsList for isDataSource is true */
    @RequestMapping(value = "/getDataSourceStopList", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDataSourceStopList", notes = "get DataSource Stop list")
    public String getDataSourceStopList() {
        return stopsComponentServiceImpl.getDataSourceStopList();
    }

    @RequestMapping(value = "/getDataSourceStopProperty", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "getDataSourceStopProperty", notes = "get DataSource Stop Property list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stopsTemplateBundle", value = "stopsTemplateBundle", required = true, paramType = "query"),
    })
    public String getDataSourceStopProperty(String stopsTemplateBundle) {
        return stopsComponentServiceImpl.getStopsComponentPropertyByStopsId(stopsTemplateBundle);
    }
}
