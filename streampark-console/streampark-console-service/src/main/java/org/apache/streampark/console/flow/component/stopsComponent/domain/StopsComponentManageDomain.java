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

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentManage;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentManageMapper;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class StopsComponentManageDomain {

    private final StopsComponentManageMapper stopsComponentManageMapper;

    @Autowired
    public StopsComponentManageDomain(StopsComponentManageMapper stopsComponentManageMapper) {
        this.stopsComponentManageMapper = stopsComponentManageMapper;
    }

    public int saveOrUpdeate(StopsComponentManage stopsComponentManage) throws Exception {
        if (null == stopsComponentManage) {
            return 0;
        }
        String id = stopsComponentManage.getId();
        int insertRows = 0;
        if (StringUtils.isBlank(id)) {
            stopsComponentManage.setId(UUIDUtils.getUUID32());
            insertRows = stopsComponentManageMapper.insertStopsComponentManage(stopsComponentManage);
        } else {
            insertRows = stopsComponentManageMapper.updateStopsComponentManage(stopsComponentManage);
        }
        return insertRows;
    }

    public int addStopsComponentManageMapper(StopsComponentManage stopsComponentManage) throws Exception {
        if (null == stopsComponentManage) {
            return 0;
        }
        int insertRows = stopsComponentManageMapper.insertStopsComponentManage(stopsComponentManage);
        if (insertRows <= 0) {
            throw new Exception("insert failed");
        }
        return insertRows;
    }

    public int updateStopsComponentManageMapper(StopsComponentManage stopsComponentManage) throws Exception {
        if (null == stopsComponentManage) {
            return 0;
        }
        return stopsComponentManageMapper.updateStopsComponentManage(stopsComponentManage);
    }

    public StopsComponentManage getStopsComponentManageByBundleAndGroup(
                                                                        String bundle, String stopsGroups) {
        if (StringUtils.isBlank(bundle)) {
            return null;
        }
        if (StringUtils.isBlank(stopsGroups)) {
            return null;
        }
        return stopsComponentManageMapper.getStopsComponentManageByBundleAndGroup(bundle, stopsGroups);
    }
}
