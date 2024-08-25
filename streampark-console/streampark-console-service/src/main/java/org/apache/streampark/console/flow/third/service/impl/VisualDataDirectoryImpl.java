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
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.third.service.IVisualDataDirectory;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class VisualDataDirectoryImpl implements IVisualDataDirectory {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private Logger logger = LoggerUtil.getLogger();

    @Override
    public Map<String, Object> getVisualDataDirectoryData(String appId, String stopName) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appId);
        map.put("stopName", stopName);
        CloseableHttpResponse response =
            HttpUtils.doGetReturnResponse(ApiConfig.getVisualDataDirectoryData(), map, 30 * 1000);
        if (response == null) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG());
            return ReturnMapUtils.setFailedMsg(MessageConfig.INTERFACE_CALL_ERROR_MSG());
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            logger.error(
                MessageConfig.INTERFACE_CALL_ERROR_MSG()
                    + ": "
                    + response.getStatusLine().getStatusCode());
            return ReturnMapUtils.setFailedMsg(
                MessageConfig.INTERFACE_CALL_ERROR_MSG()
                    + ": "
                    + response.getStatusLine().getStatusCode());
        }
        try {
            InputStream inputStream = response.getEntity().getContent();
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = null;
            Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                // zipInputStream中有3个数据:两个日志流,一个flowName
                String key = zipEntry.getName();
                byte[] content = IOUtils.toByteArray(zipInputStream);
                rtnMap.put("fileContent", content);
            }
            return rtnMap;
        } catch (IOException e) {
            logger.error(MessageConfig.INTERFACE_CALL_ERROR_MSG(), e);
            return ReturnMapUtils.setFailedMsg(
                MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + e.getMessage());
        }
    }
}
