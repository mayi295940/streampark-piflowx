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

package org.apache.streampark.console.flow.component.mxGraph.utils;

import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphComponentVo;

import java.util.ArrayList;
import java.util.List;

public class MxGraphComponentVoUtils {

    public static List<MxGraphComponentVo> InitDefaultGroupMxGraphComponentList() {
        List<MxGraphComponentVo> mxGraphComponentVoList = new ArrayList<>();
        mxGraphComponentVoList.add(InitGroupMxGraphComponent());
        mxGraphComponentVoList.add(InitTaskMxGraphComponent());
        mxGraphComponentVoList.add(InitTextMxGraphComponent());
        return mxGraphComponentVoList;
    }

    public static MxGraphComponentVo InitGroupMxGraphComponent() {
        MxGraphComponentVo mxGraphComponentVo = new MxGraphComponentVo();
        mxGraphComponentVo.setAddImagePaletteId("general");
        mxGraphComponentVo.setComponent_prefix("/img/");
        mxGraphComponentVo.setComponent_name("Group");
        List<MxGraphComponentVo.COMPONENT_GROUP> component_group_list = new ArrayList<>();
        MxGraphComponentVo.COMPONENT_GROUP component_group = new MxGraphComponentVo.COMPONENT_GROUP();
        component_group.setImg_type(".png");
        component_group.setDescription("Group component");
        component_group.setImg_name("group");
        component_group.setName("group");
        component_group_list.add(component_group);
        mxGraphComponentVo.setComponent_group(component_group_list);
        return mxGraphComponentVo;
    }

    public static MxGraphComponentVo InitTaskMxGraphComponent() {
        MxGraphComponentVo mxGraphComponentVo = new MxGraphComponentVo();
        mxGraphComponentVo.setAddImagePaletteId("general");
        mxGraphComponentVo.setComponent_prefix("/img/");
        mxGraphComponentVo.setComponent_name("Task");
        List<MxGraphComponentVo.COMPONENT_GROUP> component_group_list = new ArrayList<>();
        MxGraphComponentVo.COMPONENT_GROUP component_group = new MxGraphComponentVo.COMPONENT_GROUP();
        component_group.setImg_type(".png");
        component_group.setDescription("Task component");
        component_group.setImg_name("flow");
        component_group.setName("flow");
        component_group_list.add(component_group);
        mxGraphComponentVo.setComponent_group(component_group_list);
        return mxGraphComponentVo;
    }

    public static MxGraphComponentVo InitTextMxGraphComponent() {
        MxGraphComponentVo mxGraphComponentVo = new MxGraphComponentVo();
        mxGraphComponentVo.setAddImagePaletteId("general");
        mxGraphComponentVo.setComponent_prefix("/img/");
        mxGraphComponentVo.setComponent_name("Text");
        List<MxGraphComponentVo.COMPONENT_GROUP> component_group_list = new ArrayList<>();
        MxGraphComponentVo.COMPONENT_GROUP component_group = new MxGraphComponentVo.COMPONENT_GROUP();
        component_group.setImg_type(".png");
        component_group.setDescription("Text component");
        component_group.setImg_name("text");
        component_group.setName("text");
        component_group_list.add(component_group);
        mxGraphComponentVo.setComponent_group(component_group_list);
        return mxGraphComponentVo;
    }
}
