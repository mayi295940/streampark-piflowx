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

package org.apache.streampark.console.flow.third.market.service;

import org.apache.streampark.console.flow.component.stopsComponent.vo.PublishComponentVo;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.util.Map;

public interface IMarket {

    /**
     * Publish Components //TODO wait to delete
     *
     * @param bundle
     * @param category
     * @param description
     * @param logo
     * @param name
     * @param file
     * @return
     */
    public Map<String, Object> publishComponents(
                                                 String accessKey,
                                                 String bundle,
                                                 String category,
                                                 String description,
                                                 String logo,
                                                 String name,
                                                 File file);

    /**
     * @Description Publish Components new @Param accessKey @Param publishComponentVo @Param
     * file @Return java.util.Map<java.lang.String, java.lang.Object> @Author TY @Date 15:19 2023/4/3
     */
    public Map<String, Object> publishComponents(
                                                 String accessKey, PublishComponentVo publishComponentVo,
                                                 File file) throws JsonProcessingException;

    /**
     * Search Components
     *
     * @param page
     * @param pageSize
     * @param param
     * @param sort
     * @return
     */
    public String searchComponents(String page, String pageSize, String param, String sort);

    /**
     * Download Components
     *
     * @param jarName
     * @param bundle
     * @return
     */
    public String downloadComponents(String jarName, String bundle);
}
