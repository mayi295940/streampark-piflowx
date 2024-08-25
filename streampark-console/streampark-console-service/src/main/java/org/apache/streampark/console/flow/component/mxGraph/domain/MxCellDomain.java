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

package org.apache.streampark.console.flow.component.mxGraph.domain;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;
import org.apache.streampark.console.flow.component.mxGraph.mapper.MxCellMapper;
import org.apache.streampark.console.flow.component.mxGraph.mapper.MxGeometryMapper;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class MxCellDomain {

    private final MxCellMapper mxCellMapper;
    private final MxGeometryMapper mxGeometryMapper;

    @Autowired
    public MxCellDomain(MxCellMapper mxCellMapper, MxGeometryMapper mxGeometryMapper) {
        this.mxCellMapper = mxCellMapper;
        this.mxGeometryMapper = mxGeometryMapper;
    }

    public int addMxCell(MxCell mxCell) throws Exception {
        if (null == mxCell) {
            return 0;
        }
        if (StringUtils.isBlank(mxCell.getId())) {
            mxCell.setId(UUIDUtils.getUUID32());
        }
        int addMxCellCount = mxCellMapper.addMxCell(mxCell);
        if (addMxCellCount <= 0) {
            throw new Exception("save failed");
        }
        MxGeometry mxGeometry = mxCell.getMxGeometry();
        if (null == mxGeometry) {
            return addMxCellCount;
        }
        mxGeometry.setMxCell(mxCell);
        int addMxGeometryCount = mxGeometryMapper.addMxGeometry(mxGeometry);
        if (addMxGeometryCount <= 0) {
            throw new Exception("save failed");
        }
        return addMxCellCount + addMxGeometryCount;
    }

    public int updateMxCell(MxCell mxCell) throws Exception {
        if (null == mxCell) {
            return 0;
        }
        if (StringUtils.isBlank(mxCell.getId())) {
            mxCell.setId(UUIDUtils.getUUID32());
        }
        int addMxCellCount = mxCellMapper.updateMxCell(mxCell);
        MxGeometry mxGeometry = mxCell.getMxGeometry();
        if (null == mxGeometry) {
            return addMxCellCount;
        }
        mxGeometry.setMxCell(mxCell);
        int addMxGeometryCount = updateMxGeometry(mxGeometry);
        return addMxCellCount + addMxGeometryCount;
    }

    public int updateMxGeometry(MxGeometry mxGeometry) throws Exception {
        if (null == mxGeometry) {
            return 0;
        }
        return mxGeometryMapper.updateMxGeometry(mxGeometry);
    }

    public Integer getMaxPageIdByMxGraphModelId(String mxGraphModelId) {
        return mxCellMapper.getMaxPageIdByMxGraphModelId(mxGraphModelId);
    }

    public MxCell getMxCellByMxGraphIdAndPageId(String mxGraphId, String pageId) {
        return mxCellMapper.getMxCellByMxGraphIdAndPageId(mxGraphId, pageId);
    }

    public MxCell getMeCellById(String id) {
        return mxCellMapper.getMeCellById(id);
    }
}
