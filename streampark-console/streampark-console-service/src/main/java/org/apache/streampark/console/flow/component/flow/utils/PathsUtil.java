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

package org.apache.streampark.console.flow.component.flow.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.vo.PathsVo;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PathsUtil {

    /**
     * pathsList Po To Vo
     *
     * @param pathsList
     * @return
     */
    public static List<PathsVo> pathsListPoToVo(List<Paths> pathsList) {
        List<PathsVo> pathsVoList = null;
        if (null != pathsList && pathsList.size() > 0) {
            pathsVoList = new ArrayList<PathsVo>();
            for (Paths paths : pathsList) {
                if (null != paths) {
                    PathsVo pathsVo = new PathsVo();
                    BeanUtils.copyProperties(paths, pathsVo);
                    pathsVoList.add(pathsVo);
                }
            }
        }
        return pathsVoList;
    }

    /**
     * pathsVoList Vo To Po
     *
     * @param pathsVoList
     * @return
     */
    public static List<Paths> pathsListVoToPo(String username, List<PathsVo> pathsVoList) {
        List<Paths> pathsList = null;
        if (null != pathsVoList && pathsVoList.size() > 0) {
            pathsList = new ArrayList<Paths>();
            for (PathsVo pathsVo : pathsVoList) {
                if (null != pathsVo) {
                    Paths paths = new Paths();
                    BeanUtils.copyProperties(pathsVo, paths);
                    paths.setId(UUIDUtils.getUUID32());
                    paths.setCrtDttm(new Date());
                    paths.setCrtUser(username);
                    paths.setLastUpdateDttm(new Date());
                    paths.setLastUpdateUser(username);
                    paths.setEnableFlag(true);
                    pathsList.add(paths);
                }
            }
        }
        return pathsList;
    }
}
