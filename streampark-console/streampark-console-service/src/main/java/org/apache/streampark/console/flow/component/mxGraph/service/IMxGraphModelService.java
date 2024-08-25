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

package org.apache.streampark.console.flow.component.mxGraph.service;

import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphVo;

public interface IMxGraphModelService {

    public String saveDataForTask(String username, String imageXML, String loadId, String operType) throws Exception;

    /**
     * save or add flowGroup
     *
     * @param imageXML
     * @param loadId
     * @param operType
     * @param flag
     * @return
     */
    public String saveDataForGroup(
                                   String username, String imageXML, String loadId, String operType,
                                   boolean flag) throws Exception;

    /**
     * addMxCellAndData
     *
     * @param mxGraphVo
     * @param username
     * @return
     * @throws Exception
     */
    public String addMxCellAndData(MxGraphVo mxGraphVo, String username) throws Exception;
}
