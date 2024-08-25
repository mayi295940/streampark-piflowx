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

import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.FlowGlobalParamsVoRequestAdd;

public interface IFlowGlobalParamsService {

    /**
     * add flow(Contains drawing board information)
     *
     * @param username
     * @param flowVo
     * @return
     * @throws Exception
     */
    public String addFlowGlobalParams(String username, FlowGlobalParamsVoRequestAdd globalParamsVo) throws Exception;

    public String updateFlowGlobalParams(
                                         String username, boolean isAdmin,
                                         FlowGlobalParamsVoRequest globalParamsVo) throws Exception;

    public String deleteFlowGlobalParamsById(String username, boolean isAdmin, String id);

    /**
     * Paging query FlowGlobalParams
     *
     * @param username
     * @param isAdmin
     * @param offset Number of pages
     * @param limit Number of pages per page
     * @param param search for the keyword
     * @return
     */
    public String getFlowGlobalParamsListPage(
                                              String username, boolean isAdmin, Integer offset, Integer limit,
                                              String param);

    /**
     * Query FlowGlobalParams
     *
     * @param username
     * @param isAdmin
     * @param param search for the keyword
     * @return
     */
    public String getFlowGlobalParamsList(String username, boolean isAdmin, String param);

    public String getFlowGlobalParamsById(String username, boolean isAdmin, String id);
}
