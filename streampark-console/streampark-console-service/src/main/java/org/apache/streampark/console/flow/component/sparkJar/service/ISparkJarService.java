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

package org.apache.streampark.console.flow.component.sparkJar.service;

import org.springframework.web.multipart.MultipartFile;

public interface ISparkJarService {

    /**
     * Upload spark jar file and save
     *
     * @param username
     * @param file
     * @return
     */
    public String uploadSparkJarFile(String username, MultipartFile file);

    /**
     * mount spark jar
     *
     * @param username
     * @param id
     * @return
     */
    public String mountSparkJar(String username, Boolean isAdmin, String id);

    /**
     * unmount spark jar
     *
     * @param username
     * @param id
     * @return
     */
    public String unmountSparkJar(String username, Boolean isAdmin, String id);

    /**
     * spark jar list page
     *
     * @param username username
     * @param isAdmin is admin
     * @param page Number of pages
     * @param limit Number each page
     * @param param Search content
     * @return json
     */
    public String sparkJarListPage(
                                   String username, Boolean isAdmin, Integer page, Integer limit, String param);

    /**
     * del spark jar
     *
     * @param username username
     * @param id id
     * @return json
     */
    public String delSparkJar(String username, Boolean isAdmin, String id);
}
