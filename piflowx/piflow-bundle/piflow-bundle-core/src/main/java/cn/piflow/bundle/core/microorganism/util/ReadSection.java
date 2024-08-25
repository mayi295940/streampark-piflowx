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

package cn.piflow.bundle.core.microorganism.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSection {

    public static List readSection(
                                   BufferedReader br, Pattern sectp, String start_seq_tag, String end_seq_tag) {
        List section = new ArrayList();
        String line = "";
        String currKey = null;
        StringBuffer currVal = new StringBuffer();
        boolean done = false;
        int linecount = 0;

        try {
            while (!done) {
                br.mark(10000);
                line = br.readLine();
                String firstSecKey = section.isEmpty() ? "" : ((String[]) section.get(0))[0];
                if (line != null && line.matches("\\p{Space}*")) {
                    // regular expression \p{Space}* will match line
                    // having only white space characters
                    continue;
                }
                if (line == null
                    || (!line.startsWith(" ")
                        && linecount++ > 0
                        && (!firstSecKey.equals(start_seq_tag) || line.startsWith(end_seq_tag)))) {
                    // dump out last part of section
                    section.add(new String[]{currKey, currVal.toString()});
                    br.reset();
                    done = true;
                } else {
                    Matcher m = sectp.matcher(line);
                    if (m.matches()) {
                        // new key
                        if (currKey != null)
                            section.add(new String[]{currKey, currVal.toString()});
                        // key = group(2) or group(4) or group(6) - whichever is not null
                        currKey = m.group(2) == null ? m.group(4) : m.group(2);
                        currVal = new StringBuffer();
                        // val = group(3) if group(2) not null, group(5) if group(4) not null, "" otherwise,
                        // trimmed
                        currVal.append(m.group(3) == null ? m.group(5) : m.group(3));
                    } else {
                        currVal.append("\n"); // newline in between lines - can be removed later
                        currVal.append(line.substring(12).trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return section;
    }
}
