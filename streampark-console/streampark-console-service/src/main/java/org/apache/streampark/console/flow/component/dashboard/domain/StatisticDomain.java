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

package org.apache.streampark.console.flow.component.dashboard.domain;

import org.apache.streampark.console.flow.component.dashboard.mapper.StatisticMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class StatisticDomain {

    private final StatisticMapper statisticMapper;

    @Autowired
    public StatisticDomain(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    /**
     * query flow progress statistic info
     *
     * @return statistic info map
     */
    public List<Map<String, String>> getFlowProcessStatisticInfo() {
        return statisticMapper.getFlowProcessStatisticInfo();
    }

    /**
     * query flow count
     *
     * @return flowCount
     */
    public int getFlowCount() {
        return statisticMapper.getFlowCount();
    }

    /**
     * query group progress statistic info
     *
     * @return statistic info map
     */
    public List<Map<String, String>> getGroupProcessStatisticInfo() {
        return statisticMapper.getGroupProcessStatisticInfo();
    }

    /**
     * query group count
     *
     * @return groupCount
     */
    public int getGroupCount() {
        return statisticMapper.getGroupCount();
    }

    /**
     * query schedule statistic info
     *
     * @return statistic info map
     */
    public List<Map<String, String>> getScheduleStatisticInfo() {
        return statisticMapper.getScheduleStatisticInfo();
    }

    /**
     * query template count
     *
     * @return templateCount
     */
    public int getTemplateCount() {
        return statisticMapper.getTemplateCount();
    }

    /**
     * query datasource count
     *
     * @return datasourceCount
     */
    public int getDataSourceCount() {
        return statisticMapper.getDataSourceCount();
    }

    /**
     * query stops hub count
     *
     * @return stopsHubCount
     */
    public int getStopsHubCount() {
        return statisticMapper.getStopsHubCount();
    }

    /**
     * query stops count
     *
     * @return stopsCount
     */
    public int getStopsCount() {
        return statisticMapper.getStopsCount();
    }

    /**
     * query stops group count
     *
     * @return stopsGroupCount
     */
    public int getStopsGroupCount() {
        return statisticMapper.getStopsGroupCount();
    }
}
