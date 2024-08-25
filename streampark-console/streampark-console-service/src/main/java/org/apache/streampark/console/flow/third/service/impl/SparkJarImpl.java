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
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.third.service.ISparkJar;
import org.apache.streampark.console.flow.third.vo.sparkJar.SparkJarVo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SparkJarImpl implements ISparkJar {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private final Logger logger = LoggerUtil.getLogger();

    @Override
    public String getSparkJarPath() {

        Map<String, String> map = new HashMap<>();
        // map.put("bundle", bundleStr);
        String sendGetData = HttpUtils.doGet(ApiConfig.getSparkJarPathUrl(), map, 30 * 1000);
        logger.info("return msg：" + sendGetData);
        if (StringUtils.isBlank(sendGetData)) {
            logger.warn("Interface return value is null");
            return null;
        }
        if (sendGetData.contains("Error")
            || sendGetData.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            logger.warn("return err: " + sendGetData);
            return null;
        }

        return JSONObject.parseObject(sendGetData).getString("sparkJarPath");
    }

    @Override
    public SparkJarVo mountSparkJar(String sparkjarName) {

        Map<String, String> map = new HashMap<>();
        map.put("sparkJar", sparkjarName);
        String json = JSONObject.toJSONString(map);
        String doPost = HttpUtils.doPost(ApiConfig.getSparkJarMountUrl(), json, 5 * 1000);
        if (StringUtils.isBlank(doPost)) {
            logger.warn("Interface return values is null");
            return null;
        }
        if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Fail")) {
            logger.warn("Interface return exception: " + doPost);
            return null;
        }
        logger.info("Interface return value: " + doPost);
        return constructSparkJarVo(JSONObject.parseObject(doPost));
    }

    @Override
    public SparkJarVo unmountSparkJar(String sparkJarMountId) {

        Map<String, String> map = new HashMap<>();
        map.put("sparkJarId", sparkJarMountId);
        String json = JSONObject.toJSONString(map);
        String doPost = HttpUtils.doPost(ApiConfig.getSparkJarUNMountUrl(), json, 5 * 1000);
        if (StringUtils.isBlank(doPost)) {
            logger.warn("Interface return values is null");
            return null;
        }
        if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Fail")) {
            logger.warn("Interface return exception : " + doPost);
            return null;
        }
        logger.info("Interface return value: " + doPost);

        return constructSparkJarVo(JSONObject.parseObject(doPost));
    }

    private SparkJarVo constructSparkJarVo(JSONObject jsonObject) {

        SparkJarVo sparkJarVo = new SparkJarVo();
        String sparkJarMountId = jsonObject.getJSONObject("sparkJar").getString("id");
        sparkJarVo.setMountId(sparkJarMountId);
        return sparkJarVo;
    }
}
