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

package org.apache.streampark.console.flow.base.utils;

import org.apache.commons.lang3.StringUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PageHelperUtils {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private static Logger logger = LoggerUtil.getLogger();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, Object> setDataTableParam(Page page, Map<String, Object> rtnMap) {
        if (null == rtnMap) {
            rtnMap = new HashMap<>();
        }
        if (null != page) {
            PageInfo info = new PageInfo(page.getResult());
            rtnMap.put("iTotalDisplayRecords", info.getTotal());
            rtnMap.put("iTotalRecords", info.getTotal());
            // Data collection
            rtnMap.put("pageData", info.getList());
            logger.debug("success");
        }
        return rtnMap;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, Object> setLayTableParam(Page page, Map<String, Object> rtnMap) {
        if (null == rtnMap) {
            rtnMap = new HashMap<>();
        }
        if (null == page) {
            return rtnMap;
        }
        PageInfo info = new PageInfo(page.getResult());
        rtnMap.put("msg", "success");
        rtnMap.put("count", info.getTotal());
        // Data collection
        rtnMap.put("data", info.getList());
        logger.debug("success");
        return rtnMap;
    }

    public static String setLayTableParamRtnStr(Page page, Map<String, Object> rtnMap) {
        return JsonUtils.toJsonNoException(setLayTableParam(page, rtnMap));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, Object> setCustomDataKey(
                                                       Page page, String key1, String key2,
                                                       Map<String, Object> rtnMap) {
        if (null == rtnMap) {
            rtnMap = new HashMap<>();
        }
        if (null == page) {
            return rtnMap;
        }
        if (StringUtils.isBlank(key1)) {
            key1 = "count";
        }
        if (StringUtils.isBlank(key2)) {
            key2 = "data";
        }
        PageInfo info = new PageInfo(page.getResult());
        if (null == rtnMap.get(key1)) {
            rtnMap.put(key1, info.getTotal());
        }
        // Data collection
        rtnMap.put(key2, info.getList());
        logger.debug("success");
        return rtnMap;
    }
}
