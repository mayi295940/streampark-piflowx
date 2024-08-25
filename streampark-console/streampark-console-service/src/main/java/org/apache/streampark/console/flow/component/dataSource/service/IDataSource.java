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

package org.apache.streampark.console.flow.component.dataSource.service;

import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;

import java.util.List;

public interface IDataSource {

    /**
     * save or update DataSource
     *
     * @param dataSourceVo
     * @return
     */
    public String saveOrUpdate(
                               String username, boolean isAdmin, DataSourceVo dataSourceVo, boolean isSynchronize);

    /**
     * Query DataSourceVo according to ID (query contains its subtable)
     *
     * @param id
     * @return
     */
    public DataSourceVo dataSourceVoById(String username, boolean isAdmin, String id);

    /**
     * Query DataSourceVo according to ID (query contains its subtable)
     *
     * @param id
     * @return
     */
    public String getDataSourceVoById(String username, boolean isAdmin, String id);

    /**
     * getDataSourceVoList
     *
     * @param isAdmin
     * @param username
     * @return
     */
    public String getDataSourceVoList(String username, boolean isAdmin);

    /**
     * getDataSourceTemplateList
     *
     * @param
     * @return
     */
    public List<DataSourceVo> getDataSourceTemplateList();

    /**
     * Query dataSourceVoList (parameter space-time non-paging)
     *
     * @param offset
     * @param limit
     * @param param
     * @return
     */
    public String getDataSourceVoListPage(
                                          String username, boolean isAdmin, Integer offset, Integer limit,
                                          String param);

    /**
     * delete DataSource By Id
     *
     * @param id
     * @return
     */
    public String deleteDataSourceById(String username, boolean isAdmin, String id);

    public String getDataSourceInputPageData(String username, boolean isAdmin, String dataSourceId);

    /**
     * checkLinked
     *
     * @param datasourceId
     * @return
     */
    public String checkLinked(String datasourceId);
}
