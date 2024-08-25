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

package org.apache.streampark.console.flow.third.livy.service;

import java.util.Map;

public interface ILivy {

    /**
     * getAllSessions
     *
     * @return
     */
    public Map<String, Object> getAllSessions();

    /**
     * startSessions
     *
     * @return
     */
    public Map<String, Object> startSessions();

    /**
     * stopFlow
     *
     * @param sessionsId
     * @return
     */
    public Map<String, Object> stopSessions(String sessionsId);

    /**
     * stopSessionsState
     *
     * @param sessionsId
     * @return
     */
    public Map<String, Object> getSessionsState(String sessionsId);

    /**
     * getFlowProgress
     *
     * @param sessionsId
     * @param code
     * @return
     */
    public Map<String, Object> runStatements(String sessionsId, String code);

    /**
     * getStatementsResult
     *
     * @param sessionsId
     * @param statementsId
     * @return
     */
    public Map<String, Object> getStatementsResult(String sessionsId, String statementsId);
}
