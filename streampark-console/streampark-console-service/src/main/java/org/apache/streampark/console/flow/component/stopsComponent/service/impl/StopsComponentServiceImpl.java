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
import org.apache.streampark.console.flow.component.flow.vo.StopsVo;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.component.stopsComponent.service.IStopsComponentService;
import org.apache.streampark.console.flow.component.stopsComponent.vo.PropertyTemplateVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StopsComponentServiceImpl implements IStopsComponentService {

    private final StopsComponentDomain stopsComponentDomain;

    @Autowired
    public StopsComponentServiceImpl(StopsComponentDomain stopsComponentDomain) {
        this.stopsComponentDomain = stopsComponentDomain;
    }

    @Override
    public StopsComponent getStopsTemplateById(String id) {
        return stopsComponentDomain.getStopsComponentById(id);
    }

    @Override
    public StopsComponent getStopsPropertyById(String id) {
        return stopsComponentDomain.getStopsComponentAndPropertyById(id);
    }

    @Override
    public String getDataSourceStopList() {
        List<StopsComponent> stopsComponentList = stopsComponentDomain.getDataSourceStopList();
        List<StopsVo> stopsVoList = new ArrayList<>();
        if (stopsComponentList == null || stopsComponentList.size() == 0) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        StopsVo stopsVo = null;
        for (StopsComponent stopsComponent : stopsComponentList) {
            stopsVo = new StopsVo();
            BeanUtils.copyProperties(stopsComponent, stopsVo);
            stopsVoList.add(stopsVo);
        }
        Map<String, Object> rtnMap =
            ReturnMapUtils.setSucceededCustomParam("dataSourceStopList", stopsVoList);
        return ReturnMapUtils.toFormatJson(rtnMap);
    }

    @Override
    public String getStopsComponentPropertyByStopsId(String stopsTemplateBundle) {
        List<StopsComponentProperty> properties =
            stopsComponentDomain.getDataSourceStopsComponentByBundle(stopsTemplateBundle);
        if (properties == null || properties.size() == 0) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        } else {
            List<PropertyTemplateVo> propertiesVo = new ArrayList<PropertyTemplateVo>();
            for (StopsComponentProperty stopsComponentProperty : properties) {
                PropertyTemplateVo propertyTemplateVo = new PropertyTemplateVo();
                BeanUtils.copyProperties(stopsComponentProperty, propertyTemplateVo);
                propertiesVo.add(propertyTemplateVo);
            }
            Map<String, Object> rtnMap =
                ReturnMapUtils.setSucceededCustomParam("dataSourceStopPropertyList", propertiesVo);
            return ReturnMapUtils.toFormatJson(rtnMap);
        }
    }
}
