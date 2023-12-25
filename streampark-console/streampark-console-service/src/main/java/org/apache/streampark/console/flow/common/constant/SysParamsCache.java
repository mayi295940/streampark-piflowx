package org.apache.streampark.console.flow.common.constant;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * System parameter cache class
 *
 * <p>1. There are several system parameters that do not need to be configured. The system parameter
 * table does not need to be configured with related parameters. 2. The system parameters need to be
 * configured in the data table and must be exactly the same as the attribute names of the class. 3.
 * If the parameter value is more than one, it will take precedence; if the number cannot be
 * processed, then the number is
 *
 * <p>Note: The static properties of the cache class must be tested for cache value substitution,
 * ensuring that the cache is used correctly.
 *
 * <p>Set constant rules 1, the default set of constant values 2, "spring" gets the settings from
 * the "properties" file [static can not be injected, need "public" non-static "set" method]... 3,
 * database read settings
 */
@Component
public class SysParamsCache {

  public static ThreadPoolExecutor INIT_STOP_THREAD_POOL_EXECUTOR;

  public static ThreadPoolExecutor MONITOR_THREAD_POOL_EXECUTOR;

  public static Boolean IS_BOOT_COMPLETE = false;

  public static void setIsBootComplete(boolean isBootComplete) {
    IS_BOOT_COMPLETE = isBootComplete;
  }

  public static String ENGINE_FLINK_IMAGES_PATH;

  public static String ENGINE_SPARK_IMAGES_PATH;

  public static void setImagesPath(String imagesPath, String engineType) {
    if (Constants.ENGIN_FLINK.equalsIgnoreCase(engineType)) {
      ENGINE_FLINK_IMAGES_PATH = imagesPath;
    } else {
      ENGINE_SPARK_IMAGES_PATH = imagesPath;
    }
  }

  public static String ENGINE_FLINK_VIDEOS_PATH;

  public static String ENGINE_SPARK_VIDEOS_PATH;

  public static void setVideosPath(String videosPath, String engineType) {
    if (Constants.ENGIN_FLINK.equalsIgnoreCase(engineType)) {
      ENGINE_FLINK_VIDEOS_PATH = videosPath;
    } else {
      ENGINE_SPARK_VIDEOS_PATH = videosPath;
    }
  }

  // Xml file path (read in configuration file)
  public static String ENGINE_FLINK_XML_PATH;
  public static String ENGINE_SPARK_XML_PATH;

  public static void setXmlPath(String xmlPath, String engineType) {
    if (Constants.ENGIN_FLINK.equalsIgnoreCase(engineType)) {
      ENGINE_FLINK_XML_PATH = xmlPath;
    } else {
      ENGINE_SPARK_XML_PATH = xmlPath;
    }
  }

  // Xml file path (read in configuration file)
  public static String ENGINE_FLINK_CSV_PATH;
  public static String ENGINE_SPARK_CSV_PATH;

  public static void setCsvPath(String csvPath, String engineType) {
    if (Constants.ENGIN_FLINK.equalsIgnoreCase(engineType)) {
      ENGINE_FLINK_CSV_PATH = csvPath;
    } else {
      ENGINE_SPARK_CSV_PATH = csvPath;
    }
  }

  public static String SYS_CONTEXT_PATH;

  @Value("${server.servlet.context-path}")
  public void setSysContextPath(String sysContextPath) {
    SYS_CONTEXT_PATH = sysContextPath;
  }

  public static Boolean IS_IFRAME;

  @Value("${syspara.isIframe}")
  public void setIsIframe(String isIframe) {
    IS_IFRAME = Boolean.valueOf(isIframe);
  }

  // Interface ip and port
  public static String INTERFACE_URL_HEAD;

  @Value("${syspara.interfaceUrlHead}")
  public void setInterfaceUrlHead(String interfaceUrlHead) {
    INTERFACE_URL_HEAD = interfaceUrlHead;
  }

  public static String LIVY_SERVER;

  @Value("${syspara.livyServer}")
  public void setLivyServer(String livyServer) {
    LIVY_SERVER = livyServer;
  }

  public static String MARKET_SOFTWARE_FLAG;

  @Value("${market.software.flag}")
  public void setMarketSoftwareFlag(String marketSoftwareFlag) {
    MARKET_SOFTWARE_FLAG = marketSoftwareFlag;
  }
}
