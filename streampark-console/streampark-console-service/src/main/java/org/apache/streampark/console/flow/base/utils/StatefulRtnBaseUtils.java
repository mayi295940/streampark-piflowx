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

import org.apache.streampark.console.flow.base.vo.StatefulRtnBase;

import org.slf4j.Logger;

public class StatefulRtnBaseUtils {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private static Logger logger = LoggerUtil.getLogger();

    /**
     * set Failure information
     *
     * @param errorMsg
     * @return
     */
    public static StatefulRtnBase setFailedMsg(String errorMsg) {
        StatefulRtnBase statefulRtnBase = new StatefulRtnBase();
        logger.info(errorMsg);
        statefulRtnBase.setReqRtnStatus(false);
        statefulRtnBase.setErrorCode(statefulRtnBase.ERRCODE_FAIL);
        statefulRtnBase.setErrorMsg(errorMsg);
        return statefulRtnBase;
    }

    /**
     * set Success message
     *
     * @param SuccessdMsg
     * @return
     */
    public static StatefulRtnBase setSuccessdMsg(String SuccessdMsg) {
        StatefulRtnBase statefulRtnBase = new StatefulRtnBase();
        statefulRtnBase.setReqRtnStatus(true);
        statefulRtnBase.setErrorCode(statefulRtnBase.ERRCODE_SUCCESS);
        statefulRtnBase.setErrorMsg(SuccessdMsg);
        return statefulRtnBase;
    }
}
