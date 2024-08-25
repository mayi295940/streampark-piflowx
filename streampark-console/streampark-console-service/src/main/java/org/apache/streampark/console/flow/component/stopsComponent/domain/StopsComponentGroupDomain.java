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

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentGroupMapper;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsComponentGroupVo;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class StopsComponentGroupDomain {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private final Logger logger = LoggerUtil.getLogger();

    private final StopsComponentGroupMapper stopsComponentGroupMapper;
    private final StopsComponentDomain stopsComponentDomain;

    @Autowired
    public StopsComponentGroupDomain(
                                     StopsComponentGroupMapper stopsComponentGroupMapper,
                                     StopsComponentDomain stopsComponentDomain) {
        this.stopsComponentGroupMapper = stopsComponentGroupMapper;
        this.stopsComponentDomain = stopsComponentDomain;
    }

    public int addStopsComponentGroupAndChildren(StopsComponentGroup stopsComponentGroup) {
        if (null == stopsComponentGroup) {
            return 0;
        }
        int insertStopsComponentGroupRows =
            stopsComponentGroupMapper.insertStopGroup(stopsComponentGroup);
        int affectedRows = insertStopsComponentGroupRows;
        if (insertStopsComponentGroupRows > 0) {
            List<StopsComponent> stopsComponentList = stopsComponentGroup.getStopsComponentList();
            if (null == stopsComponentList || stopsComponentList.size() == 0) {
                return affectedRows;
            }
            int insertStopsTemplateRows =
                stopsComponentDomain.addListStopsComponentAndChildren(stopsComponentList);
            affectedRows = insertStopsComponentGroupRows + insertStopsTemplateRows;
        }
        return affectedRows;
    }

    public int addStopsComponentGroup(StopsComponentGroup stopsComponentGroup) {
        if (null == stopsComponentGroup) {
            return 0;
        }
        return stopsComponentGroupMapper.insertStopGroup(stopsComponentGroup);
    }

    public List<StopsComponentGroup> getStopGroupByNameList(
                                                            List<String> groupNameList, String engineType) {
        return stopsComponentGroupMapper.getStopGroupByNameList(groupNameList, engineType);
    }

    public StopsComponentGroup getStopsComponentGroupByGroupName(String groupName) {
        if (StringUtils.isBlank(groupName)) {
            return null;
        }
        List<StopsComponentGroup> stopGroupByName =
            stopsComponentGroupMapper.getStopGroupByName(groupName);
        if (null == stopGroupByName || stopGroupByName.size() == 0) {
            return null;
        }
        return stopGroupByName.get(0);
    }

    public int deleteStopsComponentGroup(String engineType) {
        // the group table information is cleared
        // The call is successful, the group table information is cleared and then inserted.
        stopsComponentGroupMapper.deleteGroupCorrelation(engineType);
        int deleteRows = stopsComponentGroupMapper.deleteGroup(engineType);
        logger.debug("Successful deletion Group" + deleteRows + "piece of data!!!");
        return deleteRows;
    }

    public List<StopsComponentGroup> getStopGroupList(String engineType) {
        return stopsComponentGroupMapper.getStopGroupList(engineType);
    }

    public List<StopsComponentGroupVo> getManageStopGroupList() {
        return stopsComponentGroupMapper.getManageStopGroupList();
    }
}
