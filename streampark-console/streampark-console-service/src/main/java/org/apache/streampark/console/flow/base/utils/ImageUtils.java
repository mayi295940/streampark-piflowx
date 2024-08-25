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

import sun.misc.BASE64Decoder;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("restriction")
public class ImageUtils {

    /**
     * Save image
     *
     * @param imgStr The base64 character of the image
     * @param name Store image name
     * @param type Store image type
     * @param pathUrl Storage address
     */
    public static boolean generateImage(String imgStr, String name, String type, String pathUrl) {
        // Base64 decoding of byte array strings and generating images
        if (imgStr == null) {
            return false;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64 decoding
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) { // Adjust abnormal data
                    b[i] += 256;
                }
            }
            CheckPathUtils.isChartPathExist(pathUrl);
            // Set the path to generate the image
            String path = pathUrl + name + "." + type;
            OutputStream out = Files.newOutputStream(Paths.get(path));
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
