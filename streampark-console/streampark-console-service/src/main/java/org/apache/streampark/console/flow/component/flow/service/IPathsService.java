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

import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.vo.PathsVo;

import java.util.List;

public interface IPathsService {

    public int deletePathsByFlowId(String username, String id);

    /**
     * Query connection information according to flowId and pageid
     *
     * @param flowId
     * @param pageId
     * @return
     */
    public String getPathsByFlowIdAndPageId(String flowId, String pageId);

    /**
     * Query connection information
     *
     * @param flowId
     * @param from
     * @param to
     * @return
     */
    public List<PathsVo> getPaths(String flowId, String from, String to);

    /**
     * Query the number of connections
     *
     * @param flowId
     * @param from
     * @param to
     * @return
     */
    public Integer getPathsCounts(String flowId, String from, String to);

    /**
     * Save update connection information
     *
     * @param pathsVo
     * @return
     */
    public int upDatePathsVo(String username, PathsVo pathsVo);

    /**
     * Insert list<Paths>
     *
     * @param pathsList
     * @return
     */
    public int addPathsList(String username, List<Paths> pathsList, Flow flow);
}
