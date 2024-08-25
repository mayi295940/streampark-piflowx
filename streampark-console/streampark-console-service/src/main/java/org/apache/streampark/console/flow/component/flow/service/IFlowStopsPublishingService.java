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

public interface IFlowStopsPublishingService {

    /**
     * @param username
     * @param name
     * @param stopsIds
     * @return
     */
    public String addFlowStopsPublishing(String username, String name, String stopsIds);

    /**
     * update FlowStopsPublishing
     *
     * @param isAdmin
     * @param username
     * @param publishingId
     * @param name
     * @param stopsIds
     * @return
     */
    public String updateFlowStopsPublishing(
                                            boolean isAdmin, String username, String publishingId, String name,
                                            String stopsIds);

    /**
     * getStopByNameAndFlowId
     *
     * @param publishingId
     * @return
     */
    public String getFlowStopsPublishingVo(String publishingId);

    /**
     * getFlowStopsPublishingListByFlowId
     *
     * @param username
     * @param flowId
     * @return
     */
    public String getFlowStopsPublishingListByFlowId(String username, String flowId);

    /**
     * getFlowStopsPublishingList
     *
     * @param username
     * @return
     */
    public String getFlowStopsPublishingListPager(
                                                  String username, boolean isAdmin, Integer offset, Integer limit,
                                                  String param);

    /**
     * deleteFlowStopsPublishing
     *
     * @param username
     * @param publishingId
     * @return
     */
    public String deleteFlowStopsPublishing(String username, String publishingId);
}
