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

package org.apache.streampark.console.flow.component.livy.util;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.livy.entity.NoteBook;

import java.util.Date;

public class NoteBookUtils {

    public static NoteBook setNoteBookBasicInformation(
                                                       NoteBook noteBook, boolean isSetId, String username) {
        if (null == noteBook) {
            noteBook = new NoteBook();
        }
        if (isSetId) {
            noteBook.setId(UUIDUtils.getUUID32());
        }
        noteBook.setCrtDttm(new Date());
        noteBook.setCrtUser(username);
        noteBook.setLastUpdateDttm(new Date());
        noteBook.setLastUpdateUser(username);
        noteBook.setVersion(0L);
        return noteBook;
    }
}
