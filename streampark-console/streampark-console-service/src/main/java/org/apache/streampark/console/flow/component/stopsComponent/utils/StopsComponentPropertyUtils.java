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

package org.apache.streampark.console.flow.component.stopsComponent.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentPropertyVo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopsComponentPropertyUtils {

    public static StopsComponentProperty stopsComponentPropertyNewNoId(String username) {

        StopsComponentProperty stopsComponentProperty = new StopsComponentProperty();
        // basic properties (required when creating)
        stopsComponentProperty.setCrtDttm(new Date());
        stopsComponentProperty.setCrtUser(username);
        // basic properties
        stopsComponentProperty.setEnableFlag(true);
        stopsComponentProperty.setLastUpdateUser(username);
        stopsComponentProperty.setLastUpdateDttm(new Date());
        stopsComponentProperty.setVersion(0L);
        return stopsComponentProperty;
    }

    public static StopsComponentProperty initStopsComponentPropertyBasicPropertiesNoId(
                                                                                       StopsComponentProperty stopsComponentProperty,
                                                                                       String username) {
        if (null == stopsComponentProperty) {
            return stopsComponentPropertyNewNoId(username);
        }
        // basic properties (required when creating)
        stopsComponentProperty.setCrtDttm(new Date());
        stopsComponentProperty.setCrtUser(username);
        // basic properties
        stopsComponentProperty.setEnableFlag(true);
        stopsComponentProperty.setLastUpdateUser(username);
        stopsComponentProperty.setLastUpdateDttm(new Date());
        stopsComponentProperty.setVersion(0L);
        return stopsComponentProperty;
    }

    public static List<StopsComponentProperty> thirdStopsComponentPropertyVoListToStopsComponentProperty(
                                                                                                         String username,
                                                                                                         List<ThirdStopsComponentPropertyVo> properties,
                                                                                                         StopsComponent stopsComponent) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        if (null == properties || properties.size() <= 0) {
            return null;
        }
        List<StopsComponentProperty> stopsComponentPropertyList = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++) {
            StopsComponentProperty stopsComponentProperty =
                thirdStopsComponentPropertyVoToStopsComponentProperty(
                    username, properties.get(i), stopsComponent);
            if (null == stopsComponentProperty) {
                continue;
            }
            stopsComponentProperty.setPropertySort((long) i);
            stopsComponentPropertyList.add(stopsComponentProperty);
        }
        return stopsComponentPropertyList;
    }

    public static StopsComponentProperty thirdStopsComponentPropertyVoToStopsComponentProperty(
                                                                                               String username,
                                                                                               ThirdStopsComponentPropertyVo thirdStopsComponentPropertyVo,
                                                                                               StopsComponent stopsComponent) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        if (null == thirdStopsComponentPropertyVo) {
            return null;
        }
        String stopsTemplateId = (null != stopsComponent) ? stopsComponent.getId() : null;
        StopsComponentProperty stopsComponentProperty = stopsComponentPropertyNewNoId(username);
        stopsComponentProperty.setId(UUIDUtils.getUUID32());
        stopsComponentProperty.setDefaultValue(thirdStopsComponentPropertyVo.getDefaultValue());
        stopsComponentProperty.setAllowableValues(thirdStopsComponentPropertyVo.getAllowableValues());
        stopsComponentProperty.setDescription(thirdStopsComponentPropertyVo.getDescription());
        stopsComponentProperty.setDisplayName(thirdStopsComponentPropertyVo.getDisplayName());
        stopsComponentProperty.setName(thirdStopsComponentPropertyVo.getName());
        stopsComponentProperty.setRequired(thirdStopsComponentPropertyVo.getRequired().equals("true"));
        stopsComponentProperty.setSensitive(thirdStopsComponentPropertyVo.isSensitive());
        stopsComponentProperty.setExample(thirdStopsComponentPropertyVo.getExample());
        stopsComponentProperty.setLanguage(thirdStopsComponentPropertyVo.getLanguage());
        stopsComponentProperty.setStopsTemplate(stopsTemplateId);
        return stopsComponentProperty;
    }
}
