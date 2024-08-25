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

package org.apache.streampark.console.flow.base.utils;

import java.text.DecimalFormat;

public class DecimalFormatUtils {

    public static final String KEEP_TWO_DECIMAL_PLACES = "#.00";

    public static Double formatTwoDecimalPlaces(double num) {
        DecimalFormat df = new DecimalFormat(KEEP_TWO_DECIMAL_PLACES);
        String format = df.format(num);
        return Double.parseDouble(format);
    }
}
