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

package org.apache.streampark.console.flow.component.flow.service;

import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;

import java.util.List;

public interface IPropertyService {

    /**
     * Querying group attribute information based on stopPageId
     *
     * @param stopPageId stopPageId
     */
    String queryAll(String fid, String stopPageId);

    /**
     * Modify stops attribute information
     *
     * @param content content
     */
    String updatePropertyList(String username, String[] content);

    /**
     * Modify stops attribute information
     *
     * @param id id
     * @param content content
     */
    String updateProperty(String username, String content, String id);

    /** query All StopsProperty List; */
    List<Property> getStopsPropertyList();

    /** delete StopsProperty according to ID; */
    int deleteStopsPropertyById(String id);

    /**
     * check stops template
     *
     * @param username username
     * @param stopsId stopsId
     */
    void checkStopTemplateUpdate(String username, String stopsId);

    String saveOrUpdateRoutePath(String username, UpdatePathRequest updatePathRequest);

    /**
     * deleteLastReloadDataByStopsId
     *
     * @param stopId stopId
     */
    String deleteLastReloadDataByStopsId(String stopId);

    String updateStopDisabled(String username, Boolean isAdmin, String id, Boolean enable);

    String previewCreateSql(String fid, String stopPageId);
}
