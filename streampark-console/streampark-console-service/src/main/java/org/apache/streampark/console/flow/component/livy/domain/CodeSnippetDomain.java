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

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.livy.entity.CodeSnippet;
import org.apache.streampark.console.flow.component.livy.mapper.CodeSnippetMapper;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class CodeSnippetDomain {

    private final CodeSnippetMapper codeSnippetMapper;

    public CodeSnippetDomain(CodeSnippetMapper codeSnippetMapper) {
        this.codeSnippetMapper = codeSnippetMapper;
    }

    public int addCodeSnippet(CodeSnippet codeSnippet) throws Exception {
        if (null == codeSnippet) {
            throw new Exception("save failed");
        }
        String id = codeSnippet.getId();
        if (StringUtils.isBlank(id)) {
            codeSnippet.setId(UUIDUtils.getUUID32());
        }
        int affectedRows = codeSnippetMapper.addCodeSnippet(codeSnippet);
        if (affectedRows <= 0) {
            throw new Exception("save failed");
        }
        return affectedRows;
    }

    public int updateCodeSnippet(CodeSnippet codeSnippet) {
        if (null == codeSnippet) {
            return 0;
        }
        return codeSnippetMapper.updateCodeSnippet(codeSnippet);
    }

    public Integer delCodeSnippetById(boolean isAdmin, String username, String id) {
        return codeSnippetMapper.delCodeSnippetById(isAdmin, username, id);
    }

    public CodeSnippet getCodeSnippetById(String id) {
        return codeSnippetMapper.getCodeSnippetById(id);
    }

    public List<CodeSnippet> getCodeSnippetListByNoteBookId(String noteBookId) {
        return codeSnippetMapper.getCodeSnippetListByNoteBookId(noteBookId);
    }
}
