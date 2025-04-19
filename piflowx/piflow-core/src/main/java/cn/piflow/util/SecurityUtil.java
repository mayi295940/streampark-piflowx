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

package cn.piflow.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private static final String ENCODING = "UTF-8";

    private static final String PASSWORD = "46EBA22EF5204DD5B110A1F730513965";

    public static String encryptAES(String content) {

        byte[] encryptResult = encrypt(content, PASSWORD);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        encryptResultStr = ebotongEncrypto(encryptResultStr);
        return encryptResultStr;
    }

    public static String decryptAES(String encryptResultStr) {

        try {
            String decrypt = ebotongDecrypto(encryptResultStr);
            byte[] decryptFrom = parseHexStr2Byte(decrypt);
            byte[] decryptResult = decrypt(decryptFrom, PASSWORD);
            return new String(decryptResult);
        } catch (Exception e) {
            return null;
        }
    }

    private static String ebotongEncrypto(String str) {
        java.util.Base64.Encoder base64encoder = java.util.Base64.getEncoder();
        String result = str;
        if (str != null && !str.isEmpty()) {
            try {
                byte[] encodeByte = str.getBytes(ENCODING);
                result = base64encoder.encodeToString(encodeByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
    }

    private static String ebotongDecrypto(String str) {
        java.util.Base64.Decoder base64decoder = java.util.Base64.getDecoder();
        try {
            byte[] encodeByte = base64decoder.decode(str);
            return new String(encodeByte);
        } catch (Exception e) {
            logger.error("IO 异Exception", e);
            return str;
        }
    }

    private static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);

            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(byteContent);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);

            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.isEmpty()) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
