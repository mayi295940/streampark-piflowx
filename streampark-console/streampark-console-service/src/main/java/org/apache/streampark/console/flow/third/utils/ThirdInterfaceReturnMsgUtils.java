package org.apache.streampark.console.flow.third.utils;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class ThirdInterfaceReturnMsgUtils {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static Logger logger = LoggerUtil.getLogger();

  public static String ERROR = "Error";
  public static String SUCCEEDED = "Succeeded";

  public static String THIRD_INTERFACE_IS_ERROR(String returnMsg) {
    if (StringUtils.isBlank(returnMsg)) {
      logger.warn(MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
      return ERROR;
    }
    if (returnMsg.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())
        || returnMsg.contains("Exception")
        || returnMsg.contains("Error")
        || returnMsg.contains("Fail")) {
      logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + " : " + returnMsg);
      return ERROR;
    }

    return SUCCEEDED;
  }
}
