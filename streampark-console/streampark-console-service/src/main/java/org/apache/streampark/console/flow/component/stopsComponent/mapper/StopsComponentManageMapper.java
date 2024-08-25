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

package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentManage;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentManageMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface StopsComponentManageMapper {

    /**
     * Add stopsComponentManage.
     *
     * @param stopsComponentManage stopsComponentManage
     */
    @InsertProvider(type = StopsComponentManageMapperProvider.class, method = "insertStopsComponentManage")
    int insertStopsComponentManage(StopsComponentManage stopsComponentManage);

    /**
     * update StopsComponentManage.
     *
     * @param stopsComponentManage stopsComponentManage
     */
    @InsertProvider(type = StopsComponentManageMapperProvider.class, method = "updateStopsComponentManage")
    int updateStopsComponentManage(StopsComponentManage stopsComponentManage);

    /**
     * Query StopsComponentManage by bundle and stopsGroups
     *
     * @param bundle bundle
     * @param stopsGroups stopsGroups
     * @return StopsComponentManage
     */
    @SelectProvider(type = StopsComponentManageMapperProvider.class, method = "getStopsComponentManageByBundleAndGroup")
    StopsComponentManage getStopsComponentManageByBundleAndGroup(String bundle, String stopsGroups);
}
