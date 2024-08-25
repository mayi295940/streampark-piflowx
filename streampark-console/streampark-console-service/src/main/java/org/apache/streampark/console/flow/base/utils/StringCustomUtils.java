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

import org.apache.commons.lang3.StringUtils;

public class StringCustomUtils {

    private static String EXCLUSIVE_STR = "[PiFlow]";

    public static String replaceSpecialSymbols(String sourceStr, boolean isSaveXml) {
        if (isSaveXml) {
            return replaceSpecialSymbolsXml(sourceStr);
        }
        return replaceSpecialSymbolsPage(sourceStr);
    }

    public static String recoverSpecialSymbols(String sourceStr, boolean isSaveXml) {
        if (isSaveXml) {
            return recoverSpecialSymbolsXml(sourceStr);
        }
        return recoverSpecialSymbolsPage(sourceStr);
    }

    /**
     * Translation of special symbols(< , > , & , " , ')
     *
     * @param sourceStr
     * @return
     */
    public static String replaceSpecialSymbolsXml(String sourceStr) {
        if (StringUtils.isNotBlank(sourceStr)) {
            String translation = sourceStr;
            translation = translation.replace(EXCLUSIVE_STR, EXCLUSIVE_STR + "^_^" + EXCLUSIVE_STR);
            translation = translation.replace("&", EXCLUSIVE_STR + "&amp;" + EXCLUSIVE_STR);
            translation = translation.replace("<", EXCLUSIVE_STR + "&lt;" + EXCLUSIVE_STR);
            translation = translation.replace(">", EXCLUSIVE_STR + "&gt;" + EXCLUSIVE_STR);
            translation = translation.replace("'", EXCLUSIVE_STR + "&apos;" + EXCLUSIVE_STR);
            translation = translation.replace("\"", EXCLUSIVE_STR + "&quot;" + EXCLUSIVE_STR);
            translation = translation.replace("\n", EXCLUSIVE_STR + "&quot;n" + EXCLUSIVE_STR);
            return translation;
        }
        return sourceStr;
    }

    public static String recoverSpecialSymbolsXml(String sourceStr) {
        if (StringUtils.isNotBlank(sourceStr)) {
            String translation = sourceStr;
            translation = translation.replace(EXCLUSIVE_STR + "&amp;" + EXCLUSIVE_STR, "&");
            translation = translation.replace(EXCLUSIVE_STR + "&lt;" + EXCLUSIVE_STR, "<");
            translation = translation.replace(EXCLUSIVE_STR + "&gt;" + EXCLUSIVE_STR, ">");
            translation = translation.replace(EXCLUSIVE_STR + "&apos;" + EXCLUSIVE_STR, "'");
            translation = translation.replace(EXCLUSIVE_STR + "&quot;" + EXCLUSIVE_STR, "\"");
            translation = translation.replace(EXCLUSIVE_STR + "^_^" + EXCLUSIVE_STR, EXCLUSIVE_STR);
            translation = translation.replace(EXCLUSIVE_STR + "&quot;n" + EXCLUSIVE_STR, "\n");
            return translation;
        }
        return sourceStr;
    }

    /**
     * Translation of special symbols(< , > , & , " , ')
     *
     * @param sourceStr
     * @return
     */
    public static String replaceSpecialSymbolsPage(String sourceStr) {
        if (StringUtils.isNotBlank(sourceStr)) {
            String translation = sourceStr;
            translation = translation.replace("&", "&amp;");
            translation = translation.replace("<", "&lt;");
            translation = translation.replace(">", "&gt;");
            translation = translation.replace("'", "&apos;");
            translation = translation.replace("\"", "&quot;");
            return translation;
        }
        return sourceStr;
    }

    public static String recoverSpecialSymbolsPage(String sourceStr) {
        if (StringUtils.isNotBlank(sourceStr)) {
            String translation = sourceStr;
            translation = translation.replace("&amp;", "&");
            translation = translation.replace("&lt;", "<");
            translation = translation.replace("&gt;", ">");
            translation = translation.replace("&apos;", "'");
            translation = translation.replace("&quot;", "\"");
            return translation;
        }
        return sourceStr;
    }
}
