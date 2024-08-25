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

package org.apache.streampark.console.flow.third.livy.service.impl;

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.third.livy.service.ILivy;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LivyImpl implements ILivy {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private final Logger logger = LoggerUtil.getLogger();

    @Override
    public Map<String, Object> getAllSessions() {
        String doGet = HttpUtils.doGet(ApiConfig.getLivySessionsUrl(), null, null);
        logger.info("return msg: " + doGet);
        return ReturnMapUtils.setSucceededCustomParam("data", doGet);
    }

    @Override
    public Map<String, Object> startSessions() {
        String doPost = HttpUtils.doPost(ApiConfig.getLivySessionsUrl(), "{\"kind\":\"spark\"}", null);
        logger.info("return msg: " + doPost);
        if (StringUtils.isBlank(doPost)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doPost.startsWith(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            return ReturnMapUtils.setFailedMsg(doPost);
        }
        try {
            JSONObject obj = JSONObject.parseObject(doPost); // Convert a json string to a json object
            String sessionsId = obj.getString("id");
            if (StringUtils.isBlank(sessionsId)) {
                return ReturnMapUtils.setFailedMsg(
                    "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
            }
            return ReturnMapUtils.setSucceededCustomParam("sessionsId", sessionsId);
        } catch (Exception e) {
            logger.error("error: ", e);
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_CALL_SUCCEEDED_CONVERSION_ERROR_MSG());
        }
    }

    @Override
    public Map<String, Object> stopSessions(String sessionsId) {
        String url = ApiConfig.getLivySessionsUrl() + "/" + sessionsId;
        String doDelete = HttpUtils.doDelete(url, null);
        logger.info("return msg: " + doDelete);
        if (StringUtils.isBlank(doDelete)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doDelete.startsWith(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            return ReturnMapUtils.setFailedMsg(doDelete);
        }
        return ReturnMapUtils.setSucceededCustomParam("data", doDelete);
    }

    @Override
    public Map<String, Object> getSessionsState(String sessionsId) {
        String url = ApiConfig.getLivySessionsUrl() + "/" + sessionsId + "/state";
        String doGet = HttpUtils.doGet(url, null, null);
        logger.info("return msg: " + doGet);
        if (StringUtils.isBlank(doGet)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doGet.startsWith(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            return ReturnMapUtils.setFailedMsg(doGet);
        }
        return ReturnMapUtils.setSucceededCustomParam("data", doGet);
    }

    @Override
    public Map<String, Object> runStatements(String sessionsId, String code) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("kind", "spark");
        jsonMap.put("code", code);
        String json = ReturnMapUtils.toJson(jsonMap);
        String url = ApiConfig.getLivySessionsUrl() + "/" + sessionsId + "/statements";
        String doPost = HttpUtils.doPost(url, json, null);
        logger.info("return msg: " + doPost);
        if (StringUtils.isBlank(doPost)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doPost.startsWith(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            return ReturnMapUtils.setFailedMsg(doPost);
        }
        try {
            JSONObject obj = JSONObject.parseObject(doPost);
            String statementsId = obj.getString("id");
            if (StringUtils.isBlank(statementsId)) {
                return ReturnMapUtils.setFailedMsg(
                    "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
            }
            return ReturnMapUtils.setSucceededCustomParam("statementsId", statementsId);
        } catch (Exception e) {
            logger.error("error: ", e);
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_CALL_SUCCEEDED_CONVERSION_ERROR_MSG());
        }
    }

    @Override
    public Map<String, Object> getStatementsResult(String sessionsId, String statementsId) {
        String url = ApiConfig.getLivySessionsUrl() + "/" + sessionsId + "/statements/" + statementsId;
        String doGet = HttpUtils.doGet(url, null, null);
        logger.info("return msg: " + doGet);
        if (StringUtils.isBlank(doGet)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doGet.startsWith(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
            return ReturnMapUtils.setFailedMsg(doGet);
        }
        return ReturnMapUtils.setSucceededCustomParam("data", doGet);
    }
}
