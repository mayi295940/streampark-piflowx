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

package org.apache.streampark.console.flow.third.service.impl;

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.third.service.IStop;
import org.apache.streampark.console.flow.third.utils.ThirdInterfaceReturnMsgUtils;
import org.apache.streampark.console.flow.third.utils.ThirdStopsComponentUtils;
import org.apache.streampark.console.flow.third.vo.stop.StopsHubVo;
import org.apache.streampark.console.flow.third.vo.stop.ThirdStopsComponentVo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StopImpl implements IStop {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private final Logger logger = LoggerUtil.getLogger();

    @Override
    public String[] getAllGroup() {
        String sendGetData = HttpUtils.doGet(ApiConfig.getStopsGroupsUrl(), null, 30 * 1000);
        logger.debug("Interface return value：" + sendGetData);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(sendGetData)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }
        String jsonResult = JSONObject.parseObject(sendGetData).getString("groups");
        if (StringUtils.isBlank(jsonResult)) {
            return null;
        }
        return jsonResult.split(",");
    }

    @Override
    public String[] getAllStops() {
        String[] stop = null;
        String sendGetData = HttpUtils.doGet(ApiConfig.getStopsListUrl(), null, 30 * 1000);
        logger.debug("Interface return value：" + sendGetData);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(sendGetData)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }
        String jsonResult = JSONObject.parseObject(sendGetData).getString("stops");
        if (StringUtils.isBlank(jsonResult)) {
            return null;
        }
        // Separate the tops from the array with the, sign
        stop = jsonResult.split(",");
        return stop;
    }

    @Override
    public Map<String, List<String>> getStopsListWithGroup(String engineType) {

        Map<String, String> param = new HashMap<>();
        if (StringUtils.isNotBlank(engineType)) {
            param.put("engineType", engineType);
        }

        String sendGetData = HttpUtils.doGet(ApiConfig.getStopsListWithGroupUrl(), param, 30 * 1000);
        logger.debug("Interface return value：" + sendGetData);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(sendGetData)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }
        Map<String, List<String>> stopsListWithGroup = new HashMap<>();
        String jsonResult = JSONObject.parseObject(sendGetData).getString("stopWithGroup");
        String[] bundleAndGroupArray = jsonResult.split(",");
        for (String str : bundleAndGroupArray) {
            if (StringUtils.isBlank(str)) {
                continue;
            }
            String[] split = str.split(":");
            List<String> bundleList = stopsListWithGroup.get(split[0]);
            if (null == bundleList) {
                bundleList = new ArrayList<>();
            }
            if (split.length == 2) {
                bundleList.add(split[1]);
            }
            stopsListWithGroup.put(split[0], bundleList);
        }
        return stopsListWithGroup;
    }

    @Override
    public ThirdStopsComponentVo getStopInfo(String bundleStr) {
        if (StringUtils.isBlank(bundleStr)) {
            logger.warn("bundleStr value is null");
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("bundle", bundleStr);
        String sendGetData = HttpUtils.doGet(ApiConfig.getStopsInfoUrl(), map, 30 * 1000);
        logger.info("Interface return value：" + sendGetData);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(sendGetData)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(sendGetData).getJSONObject("StopInfo");
        return ThirdStopsComponentUtils.constructThirdStopsComponentVo(jsonObject);
    }

    @Override
    public String getStopsHubPath() {

        Map<String, String> map = new HashMap<>();
        // map.put("bundle", bundleStr);
        String sendGetData = HttpUtils.doGet(ApiConfig.getStopsHubPathUrl(), map, 30 * 1000);
        logger.debug("Interface return value：" + sendGetData);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(sendGetData)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }

        return JSONObject.parseObject(sendGetData).getString("pluginPath");
    }

    @Override
    public StopsHubVo mountStopsHub(String stopsHubName) {
        Map<String, String> map = new HashMap<>();
        map.put("plugin", stopsHubName);
        String json = JSONObject.toJSONString(map);
        String doPost = HttpUtils.doPost(ApiConfig.getStopsHubMountUrl(), json, 20 * 60 * 1000);
        logger.debug("Interface return value: " + doPost);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(doPost)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }
        return ThirdStopsComponentUtils.constructStopsHubVo(JSONObject.parseObject(doPost));
    }

    @Override
    public StopsHubVo unmountStopsHub(String stopsHubMountId) {

        // String stopsHubMountId = "";
        Map<String, String> map = new HashMap<>();
        map.put("pluginId", stopsHubMountId);
        String json = JSONObject.toJSONString(map);
        String doPost = HttpUtils.doPost(ApiConfig.getStopsHubUNMountUrl(), json, 20 * 60 * 1000);
        logger.info("Interface return value: " + doPost);
        if (ThirdInterfaceReturnMsgUtils.THIRD_INTERFACE_IS_ERROR(doPost)
            .equals(ThirdInterfaceReturnMsgUtils.ERROR)) {
            return null;
        }
        return ThirdStopsComponentUtils.constructStopsHubVo(JSONObject.parseObject(doPost));
    }
}
