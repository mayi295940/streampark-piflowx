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

package org.apache.streampark.console.flow.third.utils;

import org.apache.streampark.console.flow.third.vo.stop.StopsHubVo;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentPropertyVo;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentVo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdStopsComponentUtils {

    public static ThirdStopsComponentVo constructThirdStopsComponentVo(JSONObject jsonObject) {
        ThirdStopsComponentVo thirdStopsComponentVo = new ThirdStopsComponentVo();
        // Also convert the json string to a json object, and then convert the json object to a java
        // object, as shown below.
        // JSONObject jsonObject = JSONObject.fromObject(data).getJSONObject("StopInfo");// Convert a
        // json string to a json object
        // Needed when there is a List in jsonObj
        @SuppressWarnings("rawtypes")
        Map<String, Class> classMap = new HashMap<>();
        // Key is the name of the List in jsonObj, and the value is a generic class of list
        classMap.put("properties", ThirdStopsComponentPropertyVo.class);

        // Convert a json object to a java object
        thirdStopsComponentVo.setName(jsonObject.getString("name"));
        thirdStopsComponentVo.setBundle(jsonObject.getString("bundle"));
        thirdStopsComponentVo.setOwner(jsonObject.getString("owner"));
        thirdStopsComponentVo.setEngineType(jsonObject.getString("engineType"));
        thirdStopsComponentVo.setInports(jsonObject.getString("inports"));
        thirdStopsComponentVo.setOutports(jsonObject.getString("outports"));
        thirdStopsComponentVo.setGroups(jsonObject.getString("groups"));
        thirdStopsComponentVo.setCustomized(jsonObject.getBoolean("isCustomized"));
        // thirdStopsComponentVo.setDataSource(jsonObject.getBoolean("isDataSource"));
        thirdStopsComponentVo.setDescription(jsonObject.getString("description"));
        thirdStopsComponentVo.setIcon(jsonObject.getString("icon"));
        // thirdStopsComponentVo.setVisualizationType(jsonObject.getString("visualizationType"));
        JSONArray jsonArray = jsonObject.getJSONArray("properties");

        if (null != jsonArray && jsonArray.size() > 0) {
            List<ThirdStopsComponentPropertyVo> thirdStopsComponentPropertyVoList = new ArrayList<>();
            ThirdStopsComponentPropertyVo thirdStopsComponentPropertyVo;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject propertyJsonObject = jsonArray.getJSONObject(i);
                if (null == propertyJsonObject) {
                    continue;
                }
                thirdStopsComponentPropertyVo = new ThirdStopsComponentPropertyVo();
                thirdStopsComponentPropertyVo.setName(propertyJsonObject.getString("name"));
                thirdStopsComponentPropertyVo.setDisplayName(propertyJsonObject.getString("displayName"));
                thirdStopsComponentPropertyVo.setDescription(propertyJsonObject.getString("description"));
                thirdStopsComponentPropertyVo.setDefaultValue(propertyJsonObject.getString("defaultValue"));
                thirdStopsComponentPropertyVo.setAllowableValues(
                    propertyJsonObject.getString("allowableValues"));
                thirdStopsComponentPropertyVo.setRequired(propertyJsonObject.getString("required"));
                thirdStopsComponentPropertyVo.setSensitive(propertyJsonObject.getBoolean("sensitive"));
                thirdStopsComponentPropertyVo.setExample(propertyJsonObject.getString("example"));
                thirdStopsComponentPropertyVo.setLanguage(propertyJsonObject.getString("language"));
                thirdStopsComponentPropertyVoList.add(thirdStopsComponentPropertyVo);
            }
            thirdStopsComponentVo.setProperties(thirdStopsComponentPropertyVoList);
        }

        return thirdStopsComponentVo;
    }

    public static StopsHubVo constructStopsHubVo(JSONObject jsonObject) {

        StopsHubVo stopsHubVo = new StopsHubVo();
        String stopsHubMountId = jsonObject.getJSONObject("plugin").getString("id");
        stopsHubVo.setMountId(stopsHubMountId);

        // construct Stop Info
        List<ThirdStopsComponentVo> stops = new ArrayList<>();
        JSONArray stopsListJsonArray = jsonObject.getJSONArray("stopsInfo");
        for (int i = 0; i < stopsListJsonArray.size(); i++) {
            JSONObject stopInfoJsonObject = (JSONObject) stopsListJsonArray.get(i);
            ThirdStopsComponentVo thirdStopsComponentVo =
                constructThirdStopsComponentVo(stopInfoJsonObject.getJSONObject("StopInfo"));
            stops.add(thirdStopsComponentVo);
        }
        stopsHubVo.setStops(stops);
        return stopsHubVo;
    }
}
