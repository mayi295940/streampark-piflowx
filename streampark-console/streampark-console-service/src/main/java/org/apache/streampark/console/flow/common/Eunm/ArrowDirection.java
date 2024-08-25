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

package org.apache.streampark.console.flow.common.Eunm;

import org.apache.streampark.console.flow.base.TextureEnumSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TextureEnumSerializer.class)
public enum ArrowDirection {

    UP_DIRECTION(3.5, 0, 3.5, 5, 0, 7, 7, 7),
    DOWN_DIRECTION(3.5, 7, 3.5, 2, 0, 0, 7, 0),
    RIGHT_DIRECTION(7, 3.5, 2, 3.5, 0, 0, 0, 7),
    LEFT_DIRECTION(0, 3.5, 5, 3.5, 7, 0, 7, 7);
    private final double upX;
    private final double upY;
    private final double downX;
    private final double downY;
    private final double rightX;
    private final double rightY;
    private final double lfetX;
    private final double lfetY;

    private ArrowDirection(
                           double upX,
                           double upY,
                           double downX,
                           double downY,
                           double rightX,
                           double rightY,
                           double lfetX,
                           double lfetY) {
        this.upX = upX;
        this.upY = upY;
        this.downX = downX;
        this.downY = downY;
        this.rightX = rightX;
        this.rightY = rightY;
        this.lfetX = lfetX;
        this.lfetY = lfetY;
    }

    public double getUpX() {
        return upX;
    }

    public double getUpY() {
        return upY;
    }

    public double getDownX() {
        return downX;
    }

    public double getDownY() {
        return downY;
    }

    public double getRightX() {
        return rightX;
    }

    public double getRightY() {
        return rightY;
    }

    public double getLfetX() {
        return lfetX;
    }

    public double getLfetY() {
        return lfetY;
    }
}
