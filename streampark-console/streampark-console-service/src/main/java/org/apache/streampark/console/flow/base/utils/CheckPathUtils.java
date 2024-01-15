package org.apache.streampark.console.flow.base.utils;

import org.slf4j.Logger;

import java.io.File;

public class CheckPathUtils {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static final Logger logger = LoggerUtil.getLogger();

  public static void isChartPathExist(String dirPath) {
    File file = new File(dirPath);
    if (!file.exists()) {
      boolean mkdirs = file.mkdirs();
      if (!mkdirs) {
        logger.warn("Create failed");
      }
    }
  }
}
