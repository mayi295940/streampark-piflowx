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

package org.apache.streampark.console.flow.component.stopsComponent.service;

import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsHubInfoVo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface IStopsHubService {

    /**
     * Upload jar file and save
     *
     * @param username
     * @param file
     * @param type Component type:Python/Scala
     * @param languageVersion
     * @return
     */
    public String uploadStopsHubFile(
                                     String username, MultipartFile file, String type, String languageVersion);

    /**
     * mount stopsHub
     *
     * @param username
     * @param id
     * @return
     */
    public String mountStopsHub(String username, Boolean isAdmin, String id);

    /**
     * unmount stopsHub
     *
     * @param username
     * @param id
     * @return
     */
    public String unmountStopsHub(String username, Boolean isAdmin, String id);

    /**
     * stopsHub list page
     *
     * @param username username
     * @param isAdmin is admin
     * @param page Number of pages
     * @param limit Number each page
     * @param param Search content
     * @return json
     */
    public String stopsHubListPage(
                                   String username, Boolean isAdmin, Integer page, Integer limit, String param);

    /**
     * del stopsHub
     *
     * @param username username
     * @param id id
     * @return json
     */
    public String delStopsHub(String username, Boolean isAdmin, String id);

    /**
     * stopsHub publishing
     *
     * @param username username
     * @param id id
     * @return json
     */
    public String stopsHubPublishing(String username, Boolean isAdmin, String id) throws JsonProcessingException;

    /**
     * @Description get stops hub info By stopHubId @Param username @Param isAdmin @Param
     * stopsHubId @Return java.lang.String @Author TY @Date 15:53 2023/4/3
     */
    String getStopsHubInfoByStopHubId(String username, Boolean isAdmin, String stopsHubId);

    /**
     * @Description update component info when save or remove a component except scala
     * component @Param stopsHubInfoVo @Param file @Param username @Param isAdmin @Return
     * java.lang.String @Author TY @Date 15:52 2023/4/3
     */
    String updateComponentInfo(
                               StopsHubInfoVo stopsHubInfoVo, MultipartFile file, String username, Boolean isAdmin);
}
