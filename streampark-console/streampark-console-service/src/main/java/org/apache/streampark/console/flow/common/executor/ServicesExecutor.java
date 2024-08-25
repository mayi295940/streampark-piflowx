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

package org.apache.streampark.console.flow.common.executor;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class ServicesExecutor {

    /** Introducing logs, note that they are all packaged under "org.slf4j" */
    private static Logger logger = LoggerUtil.getLogger();

    /* Define an executor of the execution thread for the service */
    private static ExecutorService servicesExecutorService;

    /* Define an executor of the execution thread for the log */
    private static ExecutorService logExecutorService;

    private static ScheduledExecutorService scheduledThreadPool;

    public static Map<String, Future<?>> TASK_FUTURE = new HashMap<>();

    /* Initialize the thread pool */
    static {
        initServicesExecutorService();
        // initLogExecutorService();
        // initScheduledThreadPool();
    }

    private static synchronized void initServicesExecutorService() {
        /**
         * Create a thread pool with a fixed number of fixed threads. Each time a task is submitted, a
         * thread is created until the thread reaches the maximum size of the thread pool. The size of
         * the thread pool will remain the same once it reaches its maximum value. If a thread ends
         * because of an exception, the thread pool will be replenished with a new thread. Create a
         * fixed-length thread pool that controls the maximum number of concurrent threads, and the
         * excess threads wait in the queue. The size of the fixed-length thread pool is best set
         * according to system resources. Such as "Runtime.getRuntime().availableProcessors()"
         */
        servicesExecutorService = null;
        servicesExecutorService = Executors.newFixedThreadPool(2);
        logger.info("Asynchronous synchronization thread pool is initialized...");
    }

    private static synchronized void initLogExecutorService() {
        /**
         * Create a thread pool with a fixed number of fixed threads. Each time a task is submitted, a
         * thread is created until the thread reaches the maximum size of the thread pool. The size of
         * the thread pool will remain the same once it reaches its maximum value. If a thread ends
         * because of an exception, the thread pool will be replenished with a new thread.
         */
        logExecutorService = null;
        logExecutorService = Executors.newFixedThreadPool(4);
        logger.info("Asynchronous log processing thread pool initialization completed...");

        // Create a cacheable thread pool. If the thread pool length exceeds the processing
        // requirements, you can flexibly reclaim idle threads. If there is no reclaimable, create a new
        // thread.
        // ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    }

    private static synchronized void initScheduledThreadPool() {
        /** Create a fixed-length thread pool that supports scheduled and periodic task execution */
        scheduledThreadPool = null;
        scheduledThreadPool =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 1);
    }

    /**
     * Get an executor instance that is called when the executor is closed or called elsewhere
     *
     * @return ExecutorService
     */
    public static ExecutorService getServicesExecutorServiceService() {
        if (null == servicesExecutorService || servicesExecutorService.isShutdown()) {
            initServicesExecutorService();
        }
        return servicesExecutorService;
    }

    public static ExecutorService getLogExecutorService() {
        if (null == logExecutorService || logExecutorService.isShutdown()) {
            initLogExecutorService();
        }
        return logExecutorService;
    }

    public static ScheduledExecutorService getScheduledServicesExecutorServiceService() {
        if (null == scheduledThreadPool || scheduledThreadPool.isShutdown()) {
            initScheduledThreadPool();
        }
        return scheduledThreadPool;
    }
}
