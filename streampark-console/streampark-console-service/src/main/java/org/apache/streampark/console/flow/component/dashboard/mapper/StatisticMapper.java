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

package org.apache.streampark.console.flow.component.dashboard.mapper;

import org.apache.streampark.console.flow.component.dashboard.mapper.provider.StatisticProvider;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticMapper {

    /**
     * query flow progress statistic info
     *
     * @return statistic info map
     */
    @SelectProvider(type = StatisticProvider.class, method = "getFlowProcessStatisticInfo")
    List<Map<String, String>> getFlowProcessStatisticInfo();

    /**
     * query flow count
     *
     * @return flowCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getFlowCount")
    int getFlowCount();

    /**
     * query group progress statistic info
     *
     * @return statistic info map
     */
    @SelectProvider(type = StatisticProvider.class, method = "getGroupProcessStatisticInfo")
    List<Map<String, String>> getGroupProcessStatisticInfo();

    /**
     * query group count
     *
     * @return groupCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getGroupCount")
    int getGroupCount();

    /**
     * query schedule statistic info
     *
     * @return statistic info map
     */
    @SelectProvider(type = StatisticProvider.class, method = "getScheduleStatisticInfo")
    List<Map<String, String>> getScheduleStatisticInfo();

    /**
     * query template count
     *
     * @return templateCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getTemplateCount")
    int getTemplateCount();

    /**
     * query datasource count
     *
     * @return datasourceCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getDataSourceCount")
    int getDataSourceCount();

    /**
     * query stops hub count
     *
     * @return stopsHubCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getStopsHubCount")
    int getStopsHubCount();

    /**
     * query stops count
     *
     * @return stopsCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getStopsCount")
    int getStopsCount();

    /**
     * query stops group count
     *
     * @return stopsGroupCount
     */
    @SelectProvider(type = StatisticProvider.class, method = "getStopsGroupCount")
    int getStopsGroupCount();
}
