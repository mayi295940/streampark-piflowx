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

import org.apache.streampark.console.flow.component.process.vo.DebugDataResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HdfsUtils {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static final Logger logger = LoggerUtil.getLogger();

  public static void closedIO(InputStreamReader isr, BufferedReader br) {
    try {
      br.close();
      isr.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String readFile(String hdfs) {
    StringBuilder json = new StringBuilder();
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      logger.warn("start time : " + DateUtils.dateTimesToStr(new Date()));
      Configuration conf = new Configuration();
      FileSystem fs = FileSystem.get(URI.create(hdfs), conf);
      FSDataInputStream hdfsInStream = fs.open(new Path(hdfs));
      isr = new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
      br = new BufferedReader(isr);
      String line;
      // int k = 0;
      while ((line = br.readLine()) != null) {
        json.append(line);
        logger.debug(line);
      }
      logger.warn("end time : " + DateUtils.dateTimesToStr(new Date()));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closedIO(isr, br);
    }
    return json.toString();
  }

  public static DebugDataResponse readPath(
      String hdfsUrl, String startFileName, int startLine, Integer limit) {
    if (null == limit) {
      limit = 10;
    }
    // Return value
    DebugDataResponse debugDataResponse = new DebugDataResponse();
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      logger.info("start time : " + DateUtils.dateTimesToStr(new Date()));
      // Read file content
      List<String> HDFSDataList = new ArrayList<>();
      // Match the start file?
      boolean flagFile = false;
      // Match the start line?
      boolean flagLine = false;
      // Read the file name of the final data
      String lastReadFileName = "";
      int lastReadFileLine = startLine;
      Configuration conf = new Configuration();
      FileSystem fs = FileSystem.get(URI.create(hdfsUrl), conf);
      FileStatus[] status = fs.listStatus(new Path(hdfsUrl));
      for (int i = 0; i < status.length; i++) {
        FileStatus file = status[i];
        // file name
        String fileName = file.getPath().getName();
        // Judge whether it is "_SUCCESS", if it is, skip this loop, otherwise continue
        if (fileName.startsWith("_SUCCESS")) {
          continue;
        }
        // Determine if the incoming start file name is empty and the flagFile is "false"
        // Continue if the incoming start file name is empty or the flagFile is true
        if (StringUtils.isNotBlank(startFileName) && !flagFile) {
          // Determine if the current file name is the same as the name of the incoming start file
          // name
          // Change the flagFile if they are the same, otherwise skip this loop
          if (startFileName.equals(fileName)) {
            flagFile = true;
          } else {
            continue;
          }
        }
        lastReadFileName = fileName;
        FSDataInputStream hdfsInStream = fs.open(file.getPath());
        isr = new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
        br = new BufferedReader(isr);

        // first line data
        String next, line = br.readLine();
        // current line
        int currentLine = 1;
        for (boolean last = (line == null); !last; line = next) {
          // Determine if the current line is greater than or equal to the starting line
          if (!flagLine) {
            if (currentLine > startLine) {
              HDFSDataList.add(line);
              flagLine = true;
              lastReadFileLine = currentLine;
            }
          } else {
            HDFSDataList.add(line);
            lastReadFileLine = currentLine;
          }
          last = ((next = br.readLine()) == null);
          if (last && (i + 1) == status.length) {
            debugDataResponse.setEnd(true);
          }
          // After reaching the limit, jump out
          if (HDFSDataList.size() >= limit) {
            break;
          }
          currentLine++;
        }
        flagLine = true;
        if (HDFSDataList.size() >= limit) {
          break;
        }
      }
      debugDataResponse.setLastReadLine(lastReadFileLine);
      debugDataResponse.setLastFileName(lastReadFileName);

      debugDataResponse.setData(HDFSDataList);
      logger.info("end time : " + DateUtils.dateTimesToStr(new Date()));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != isr || null != br) {
        closedIO(isr, br);
      }
    }
    return debugDataResponse;
  }

  public static void writeData(String hdfsUrl, String data) throws IOException {
    InputStream in =
        org.apache.commons.io.IOUtils.toInputStream(data, StandardCharsets.UTF_8.name());
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(URI.create(hdfsUrl), conf);
    OutputStream out = fs.create(new Path(hdfsUrl), () -> System.out.print("."));

    IOUtils.copyBytes(in, out, 4096, true);
  }
}
