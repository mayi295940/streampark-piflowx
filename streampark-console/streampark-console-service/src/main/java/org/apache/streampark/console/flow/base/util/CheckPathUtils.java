package org.apache.streampark.console.flow.base.util;

import java.io.File;
import org.slf4j.Logger;

public class CheckPathUtils {

  static Logger logger = LoggerUtil.getLogger();

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
