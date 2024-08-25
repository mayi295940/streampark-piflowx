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

package org.apache.streampark.console.flow.component.livy.domain;

import org.apache.streampark.console.flow.component.livy.entity.NoteBook;
import org.apache.streampark.console.flow.component.livy.mapper.NoteBookMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class NoteBookDomain {

    private final NoteBookMapper noteBookMapper;

    @Autowired
    public NoteBookDomain(NoteBookMapper noteBookMapper) {
        this.noteBookMapper = noteBookMapper;
    }

    public int addNoteBook(NoteBook noteBook) {
        return noteBookMapper.addNoteBook(noteBook);
    }

    public int updateNoteBook(NoteBook noteBook) {
        return noteBookMapper.updateNoteBook(noteBook);
    }

    public List<NoteBook> getNoteBookList(boolean isAdmin, String username, String param) {
        return noteBookMapper.getNoteBookList(isAdmin, username, param);
    }

    public NoteBook getNoteBookById(boolean isAdmin, String username, String id) {
        return noteBookMapper.getNoteBookById(isAdmin, username, id);
    }

    public Integer checkNoteBookByName(boolean isAdmin, String username, String name) {
        return noteBookMapper.checkNoteBookByName(isAdmin, username, name);
    }

    public Integer deleteNoteBookById(boolean isAdmin, String username, String noteBookId) {
        return noteBookMapper.deleteNoteBookById(isAdmin, username, noteBookId);
    }
}
