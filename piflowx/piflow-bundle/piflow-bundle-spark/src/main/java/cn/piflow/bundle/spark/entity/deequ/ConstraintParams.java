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

import java.util.List;

public class ConstraintParams {

    private String operator;
    private String value;
    private String min;
    private String max;
    private String column;
    private List<String> values;
    private String condition;
    private String name;
    private String pattern;

    public ConstraintParams() {
    }

    public ConstraintParams(
                            String operator,
                            String value,
                            String column,
                            List<String> values,
                            String condition,
                            String name,
                            String pattern) {
        this.operator = operator;
        this.value = value;
        this.column = column;
        this.values = values;
        this.condition = condition;
        this.name = name;
        this.pattern = pattern;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public String getColumn() {
        return column;
    }

    public List<String> getValues() {
        return values;
    }

    public String getCondition() {
        return condition;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
