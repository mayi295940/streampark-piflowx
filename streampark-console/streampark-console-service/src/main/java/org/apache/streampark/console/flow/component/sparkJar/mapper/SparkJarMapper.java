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

package org.apache.streampark.console.flow.component.sparkJar.mapper;

import org.apache.streampark.console.flow.component.sparkJar.entity.SparkJarComponent;
import org.apache.streampark.console.flow.component.sparkJar.mapper.provider.SparkJarMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface SparkJarMapper {

    /**
     * add SparkJarComponent
     *
     * @param sparkJarComponent sparkJarComponent
     */
    @InsertProvider(type = SparkJarMapperProvider.class, method = "addSparkJarComponent")
    int addSparkJarComponent(SparkJarComponent sparkJarComponent);

    /**
     * update SparkJarComponent
     *
     * @param sparkJarComponent sparkJarComponent
     */
    @UpdateProvider(type = SparkJarMapperProvider.class, method = "updateSparkJarComponent")
    int updateSparkJarComponent(SparkJarComponent sparkJarComponent);

    /** query all SparkJarComponent */
    @SelectProvider(type = SparkJarMapperProvider.class, method = "getSparkJarList")
    List<SparkJarComponent> getSparkJarList(String username, boolean isAdmin);

    @SelectProvider(type = SparkJarMapperProvider.class, method = "getSparkJarListByName")
    List<SparkJarComponent> getSparkJarListByName(String username, boolean isAdmin, String jarName);

    @SelectProvider(type = SparkJarMapperProvider.class, method = "getSparkJarById")
    SparkJarComponent getSparkJarById(String username, boolean isAdmin, String id);

    @UpdateProvider(type = SparkJarMapperProvider.class, method = "updateEnableFlagById")
    int deleteSparkJarById(String username, String id);

    @SelectProvider(type = SparkJarMapperProvider.class, method = "getSparkJarListParam")
    List<SparkJarComponent> getSparkJarListParam(String username, boolean isAdmin, String param);
}
