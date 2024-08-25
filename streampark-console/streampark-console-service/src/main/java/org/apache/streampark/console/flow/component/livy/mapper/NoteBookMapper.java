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

package org.apache.streampark.console.flow.component.livy.mapper;

import org.apache.streampark.console.flow.component.livy.entity.NoteBook;
import org.apache.streampark.console.flow.component.livy.mapper.provider.NoteBookMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface NoteBookMapper {

    /**
     * add NoteBook
     *
     * @param noteBook noteBook
     * @return Integer
     */
    @InsertProvider(type = NoteBookMapperProvider.class, method = "addNoteBook")
    Integer addNoteBook(NoteBook noteBook);

    /**
     * update NoteBook
     *
     * @param noteBook noteBook
     * @return Integer
     */
    @UpdateProvider(type = NoteBookMapperProvider.class, method = "updateNoteBook")
    Integer updateNoteBook(NoteBook noteBook);

    /**
     * update NoteBook enable_flag
     *
     * @param isAdmin isAdmin
     * @param username username
     * @param id id
     * @return Integer
     */
    @UpdateProvider(type = NoteBookMapperProvider.class, method = "deleteNoteBookById")
    Integer deleteNoteBookById(boolean isAdmin, String username, String id);

    /**
     * get NoteBook by name
     *
     * @param isAdmin isAdmin
     * @param username username
     * @param name name
     * @return Integer
     */
    @SelectProvider(type = NoteBookMapperProvider.class, method = "checkNoteBookByName")
    Integer checkNoteBookByName(boolean isAdmin, String username, String name);

    /**
     * get NoteBook by id
     *
     * @param isAdmin isAdmin
     * @param username username
     * @param id id
     * @return Integer
     */
    @SelectProvider(type = NoteBookMapperProvider.class, method = "getNoteBookById")
    NoteBook getNoteBookById(boolean isAdmin, String username, String id);

    /**
     * get NoteBook by id
     *
     * @param id id
     * @return Integer
     */
    @SelectProvider(type = NoteBookMapperProvider.class, method = "adminGetNoteBookById")
    NoteBook adminGetNoteBookById(String id);

    /**
     * search NoteBook List
     *
     * @param isAdmin isAdmin
     * @param username username
     * @param param param
     */
    @SelectProvider(type = NoteBookMapperProvider.class, method = "getNoteBookList")
    List<NoteBook> getNoteBookList(boolean isAdmin, String username, String param);
}
