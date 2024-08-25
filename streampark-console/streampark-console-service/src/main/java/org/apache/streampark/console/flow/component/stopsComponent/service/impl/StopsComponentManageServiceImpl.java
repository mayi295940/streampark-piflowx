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

package org.apache.streampark.console.flow.component.stopsComponent.service.impl;

import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentManageDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentManage;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsComponentManageService;
import org.apache.streampark.console.flow.component.stopsComponent.utils.StopsComponentManageUtils;
import org.apache.streampark.console.flow.controller.requestVo.UpdatestopsComponentIsShow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopsComponentManageServiceImpl implements IStopsComponentManageService {

    private final StopsComponentManageDomain stopsComponentManageDomain;

    @Autowired
    public StopsComponentManageServiceImpl(StopsComponentManageDomain stopsComponentManageDomain) {
        this.stopsComponentManageDomain = stopsComponentManageDomain;
    }

    /**
     * updateStopsComponentsIsShow
     *
     * @param username
     * @param isAdmin
     * @param stopsManage
     * @return
     * @throws Exception
     */
    @Override
    public String updateStopsComponentIsShow(
                                             String username, boolean isAdmin,
                                             UpdatestopsComponentIsShow stopsManage) throws Exception {
        if (!isAdmin) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("Permission error");
        }
        if (null == stopsManage) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("stopsManageList is null");
        }
        String[] bundleArr = stopsManage.getBundle();
        String[] stopsGroupsArr = stopsManage.getStopsGroups();
        if (null == bundleArr
            || null == stopsGroupsArr
            || bundleArr.length <= 0
            || stopsGroupsArr.length <= 0) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("param is error");
        }
        if (bundleArr.length != stopsGroupsArr.length) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("param is error");
        }
        for (int i = 0; i < stopsGroupsArr.length; i++) {
            String stopsGroup_i = stopsGroupsArr[i];
            String bundle_i = bundleArr[i];
            StopsComponentManage stopsComponentManage =
                stopsComponentManageDomain.getStopsComponentManageByBundleAndGroup(
                    bundle_i, stopsGroup_i);
            if (null == stopsComponentManage) {
                stopsComponentManage = StopsComponentManageUtils.stopsComponentManageNewNoId(username);
                stopsComponentManage.setBundle(bundle_i);
                stopsComponentManage.setStopsGroups(stopsGroup_i);
            }
            stopsComponentManage.setIsShow(stopsManage.getIsShow());
            stopsComponentManageDomain.saveOrUpdeate(stopsComponentManage);
        }
        return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }
}
