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
import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.apache.streampark.console.flow.component.mxGraph.mapper.MxNodeImageMapper;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class MxNodeImageDomain {

    private final MxNodeImageMapper mxNodeImageMapper;

    @Autowired
    public MxNodeImageDomain(MxNodeImageMapper mxNodeImageMapper) {
        this.mxNodeImageMapper = mxNodeImageMapper;
    }

    public int addMxNodeImage(MxNodeImage mxNodeImage) throws Exception {
        if (null == mxNodeImage) {
            return 0;
        }
        if (StringUtils.isBlank(mxNodeImage.getId())) {
            mxNodeImage.setId(UUIDUtils.getUUID32());
        }
        int addMxNodeImage = mxNodeImageMapper.addMxNodeImage(mxNodeImage);
        if (addMxNodeImage <= 0) {
            throw new Exception("save failed");
        }
        return addMxNodeImage;
    }

    public List<MxNodeImage> userGetMxNodeImageListByImageType(String username, String imageType) {
        return mxNodeImageMapper.userGetMxNodeImageListByImageType(username, imageType);
    }
}
