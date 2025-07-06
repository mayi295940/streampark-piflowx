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

package cn.piflow.bundle.spark.entity.deequ;

import com.alibaba.fastjson2.JSON;

import java.util.List;

public class DeequRuleConfig {

    private String checkLevel;
    private String description;
    private List<ConstraintConfig> constraints;

    public DeequRuleConfig() {
    }

    public DeequRuleConfig(
                           String checkLevel, String description, List<ConstraintConfig> constraints) {
        this.checkLevel = checkLevel;
        this.description = description;
        this.constraints = constraints;
    }

    public String getCheckLevel() {
        return checkLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setCheckLevel(String checkLevel) {
        this.checkLevel = checkLevel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConstraints(List<ConstraintConfig> constraints) {
        this.constraints = constraints;
    }

    public List<ConstraintConfig> getConstraints() {
        return constraints;
    }

    public static DeequRuleConfig get(String str) {
        return JSON.parseObject(str, DeequRuleConfig.class);
    }
}
