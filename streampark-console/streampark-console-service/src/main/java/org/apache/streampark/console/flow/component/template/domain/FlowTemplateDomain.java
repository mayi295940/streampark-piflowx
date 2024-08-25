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

package org.apache.streampark.console.flow.component.template.domain;

import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;
import org.apache.streampark.console.flow.component.template.mapper.FlowTemplateMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class FlowTemplateDomain {

    private final FlowTemplateMapper flowTemplateMapper;

    @Autowired
    public FlowTemplateDomain(FlowTemplateMapper flowTemplateMapper) {
        this.flowTemplateMapper = flowTemplateMapper;
    }

    public int insertFlowTemplate(FlowTemplate flowTemplate) {
        return flowTemplateMapper.insertFlowTemplate(flowTemplate);
    }

    public int updateEnableFlagById(String id, boolean enableFlag) {
        return flowTemplateMapper.updateEnableFlagById(id, enableFlag);
    }

    public FlowTemplate getFlowTemplateById(String id) {
        return flowTemplateMapper.getFlowTemplateById(id);
    }

    public List<FlowTemplate> getFlowTemplateList(String username, boolean isAdmin) {
        return flowTemplateMapper.getFlowTemplateList(username, isAdmin);
    }

    public List<FlowTemplate> getFlowTemplateListByParam(
                                                         String username, boolean isAdmin, String param) {
        return flowTemplateMapper.getFlowTemplateListByParam(username, isAdmin, param);
    }
}
