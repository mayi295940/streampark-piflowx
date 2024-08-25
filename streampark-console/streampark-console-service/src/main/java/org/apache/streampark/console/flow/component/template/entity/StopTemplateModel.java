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

package org.apache.streampark.console.flow.component.template.entity;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StopTemplateModel implements Serializable {

    /** stop template */
    private static final long serialVersionUID = 1L;

    private FlowTemplate flowTemplate;

    private String id;
    private String pageId;
    private String name;
    private String bundle;
    private String owner;
    private String description;
    private String inports;
    private Boolean enableFlag = Boolean.TRUE;
    private PortType inPortType;
    private String outports;
    private PortType outPortType;
    private Boolean isCheckpoint;
    private String groups;
    private Date crtDttm = new Date();
    private Long version;
    private String crtUser;

    private List<PropertyTemplateModel> properties = new ArrayList<>();

    public String getCrtDttmString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
        return crtDttm != null ? sdf.format(crtDttm) : "";
    }
}
