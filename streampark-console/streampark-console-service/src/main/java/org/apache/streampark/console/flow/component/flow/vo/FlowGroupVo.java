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

package org.apache.streampark.console.flow.component.flow.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FlowGroupVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private String pageId;
    private int flowQuantity;
    private int flowGroupQuantity;
    private Boolean isExample = false;
    private MxGraphModelVo mxGraphModelVo;
    private FlowProjectVo flowProjectVo;
    private Date crtDttm;
    private FlowGroupVo flowGroupVo;
    private List<FlowVo> flowVoList = new ArrayList<>();
    private List<FlowGroupPathsVo> flowGroupPathsVoList = new ArrayList<>();

    public String getCrtDttmString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
        return crtDttm != null ? sdf.format(crtDttm) : "";
    }
}