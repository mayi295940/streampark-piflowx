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

package org.apache.streampark.console.flow.base.vo;

import java.io.Serializable;

/** Stateful return value */
public class StatefulRtnBase implements Serializable {

    public final String ERRCODE_SUCCESS = "1";
    public final String ERRCODE_FAIL = "0";

    public final String ERRMSG_SUCCESS = "OK";

    private static final long serialVersionUID = 1L;

    /** Request processing response default success */
    private boolean reqRtnStatus = true;

    /** Request processing failure error code */
    private String errorCode = ERRCODE_SUCCESS;

    /** Request processing failure error message */
    private String errorMsg = ERRMSG_SUCCESS;

    public boolean isReqRtnStatus() {
        return reqRtnStatus;
    }

    public void setReqRtnStatus(boolean reqRtnStatus) {
        this.reqRtnStatus = reqRtnStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
