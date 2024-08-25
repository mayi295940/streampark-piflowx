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

package org.apache.streampark.console.flow.component.stopsComponent.domain;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHubFileRecord;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsHubFileRecordMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 算法包的具体文件记录 事务层 */
@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class StopsHubFileRecordDomain {

    @Autowired
    private StopsHubFileRecordMapper stopsHubFileRecordMapper;

    /**
     * 插入算法包的具体文件记录
     *
     * @param record
     * @return
     */
    public int addStopsHubFileRecord(StopsHubFileRecord record) {
        return stopsHubFileRecordMapper.addStopsHubFileRecord(record);
    }

    public List<StopsHubFileRecord> getStopsHubFileRecordByHubId(String hubId) {
        return stopsHubFileRecordMapper.getStopsHubFileRecordByHubId(hubId);
    }

    /**
     * 根据id查询算法包的具体文件记录
     *
     * @param id
     * @return
     */
    public StopsHubFileRecord getStopsHubFileRecordById(String id) {
        return stopsHubFileRecordMapper.getStopsHubFileRecordById(id);
    }

    /**
     * 根据bundle查询算法包的具体文件记录
     *
     * @param stopBundle bundle值(文件记录的filePath字段值)
     * @return
     */
    public StopsHubFileRecord getStopsHubFileRecordByBundle(String stopBundle) {
        return stopsHubFileRecordMapper.getStopsHubFileRecordByBundle(stopBundle);
    }

    /**
     * 根据id删除算法包的具体文件记录
     *
     * @param id
     * @return
     */
    public int deleteStopsHubFileRecord(String id) {
        return stopsHubFileRecordMapper.deleteStopsHubFileRecord(id);
    }
}
