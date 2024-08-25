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

package cn.piflow.bundle.core.util;

/** Exception thrown when the user has requested a task cancellation. */
public class RemoteConnectionException extends Exception {

    /** */
    public RemoteConnectionException() {
        super();
    }

    /** @param message */
    public RemoteConnectionException(String message) {
        super(message);
    }

    /** @param cause */
    public RemoteConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RemoteConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static boolean isTaskCancellation(Throwable e) {
        if (e == null) {
            return false;
        } else if (e instanceof RemoteConnectionException) {
            return true;
        } else {
            return isTaskCancellation(e.getCause());
        }
    }
}
