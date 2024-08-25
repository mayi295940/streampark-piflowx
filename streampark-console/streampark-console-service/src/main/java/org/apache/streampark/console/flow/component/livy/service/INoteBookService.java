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

package org.apache.streampark.console.flow.component.livy.service;

import org.apache.streampark.console.flow.controller.requestVo.NoteBookVoRequest;

public interface INoteBookService {

    /**
     * saveOrUpdateNoteBook
     *
     * @param username
     * @param isAdmin
     * @param noteBookVo
     * @return String
     * @throws Exception
     */
    public String saveOrUpdateNoteBook(
                                       String username, boolean isAdmin, NoteBookVoRequest noteBookVo,
                                       boolean flag) throws Exception;

    /**
     * checkNoteBookName
     *
     * @param username
     * @param isAdmin
     * @param noteBookName
     * @return String
     */
    public String checkNoteBookName(String username, boolean isAdmin, String noteBookName);

    /**
     * deleteNoteBook
     *
     * @param username
     * @param isAdmin
     * @param noteBookId
     * @return String
     */
    public String deleteNoteBook(String username, boolean isAdmin, String noteBookId);

    /**
     * getNoteBookList
     *
     * @param username
     * @param isAdmin
     * @param offset
     * @param limit
     * @param param
     * @return String
     */
    public String getNoteBookListPage(
                                      String username, boolean isAdmin, Integer offset, Integer limit, String param);

    /**
     * getNoteBookById
     *
     * @param username
     * @param isAdmin
     * @param id
     * @return String
     */
    public String getNoteBookById(String username, boolean isAdmin, String id);

    /**
     * startNoteBookSession
     *
     * @param username
     * @param isAdmin
     * @return String
     */
    public String startNoteBookSession(String username, boolean isAdmin, String noteBookId);

    /**
     * getNoteBookSessionState
     *
     * @param username
     * @param isAdmin
     * @return String
     */
    public String getNoteBookSessionState(String username, boolean isAdmin, String noteBookId);

    /**
     * delNoteBookSession
     *
     * @param username
     * @param isAdmin
     * @return String
     */
    public String delNoteBookSession(String username, boolean isAdmin, String noteBookId);

    /**
     * getAllNoteBookRunning
     *
     * @param username
     * @param isAdmin
     * @return String
     */
    public String getAllNoteBookRunning(String username, boolean isAdmin);
}
