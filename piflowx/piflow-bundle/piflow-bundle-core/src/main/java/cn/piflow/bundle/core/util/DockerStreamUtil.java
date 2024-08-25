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

package cn.piflow.bundle.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DockerStreamUtil {

    public static void execRuntime(String cmd) {
        Process process = null;
        try {
            // execute
            // process = Runtime.getRuntime().exec(cmd);
            process =
                (new ProcessBuilder(new String[]{"/bin/sh", "-c", cmd}))
                    .redirectErrorStream(true)
                    .start();

            // get process inputStream
            InputStream inputStream;
            InputStreamReader isr;
            BufferedReader br;
            String line = null;

            // if(exitVal == 0){
            inputStream = process.getInputStream();
            // } else {
            // inputStream = process.getErrorStream();
            // }
            // create a new inputStreamReader
            isr = new InputStreamReader(inputStream, Charset.forName("utf-8"));
            // create a new bufferedReader to read stream
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.contains("# Fatal error,")) {
                    throw new Exception("Error " + line);
                }
            }
            // get child process exitValue
            process.waitFor();
            int exitVal = process.exitValue();
            System.out.println(exitVal == 0 ? "execute success!" : "execute error!");
            if (exitVal != 0) {
                System.out.println("execute error!-----" + exitVal);
                inputStream = process.getErrorStream();
                isr = new InputStreamReader(inputStream, Charset.forName("utf-8"));
                br = new BufferedReader(isr);
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    throw new Exception("Error " + line);
                }
            }

        } catch (Exception e) {
            if (process != null) {
                // stop process
                process.destroy();
            }
            e.printStackTrace();
        }
    }
}
