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

package org.apache.streampark.console.flow.component.sparkJar.domain;

import org.apache.streampark.console.flow.component.sparkJar.entity.SparkJarComponent;
import org.apache.streampark.console.flow.component.sparkJar.mapper.SparkJarMapper;
import org.apache.streampark.console.flow.component.sparkJar.mapper.provider.SparkJarMapperProvider;

import org.apache.ibatis.annotations.SelectProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class SparkJarDomain {

    private final SparkJarMapper sparkJarMapper;

    @Autowired
    public SparkJarDomain(SparkJarMapper sparkJarMapper) {
        this.sparkJarMapper = sparkJarMapper;
    }

    /**
     * add SparkJarComponent
     *
     * @param sparkJarComponent
     * @return
     */
    public int addSparkJarComponent(SparkJarComponent sparkJarComponent) {
        return sparkJarMapper.addSparkJarComponent(sparkJarComponent);
    }

    /**
     * update SparkJarComponent
     *
     * @param sparkJarComponent
     * @return
     */
    public int updateSparkJarComponent(SparkJarComponent sparkJarComponent) {
        return sparkJarMapper.updateSparkJarComponent(sparkJarComponent);
    }

    /**
     * query all SparkJarComponent
     *
     * @return
     */
    public List<SparkJarComponent> getSparkJarList(String username, boolean isAdmin) {
        return sparkJarMapper.getSparkJarList(username, isAdmin);
    }

    public List<SparkJarComponent> getSparkJarListByName(
                                                         String username, boolean isAdmin, String jarName) {
        return sparkJarMapper.getSparkJarListByName(username, isAdmin, jarName);
    }

    @SelectProvider(type = SparkJarMapperProvider.class, method = "getSparkJarById")
    public SparkJarComponent getSparkJarById(String username, boolean isAdmin, String id) {
        return sparkJarMapper.getSparkJarById(username, isAdmin, id);
    }

    public int deleteSparkJarById(String username, String id) {
        return sparkJarMapper.deleteSparkJarById(username, id);
    }

    public List<SparkJarComponent> getSparkJarListParam(
                                                        String username, boolean isAdmin, String param) {
        return sparkJarMapper.getSparkJarListParam(username, isAdmin, param);
    }
}
