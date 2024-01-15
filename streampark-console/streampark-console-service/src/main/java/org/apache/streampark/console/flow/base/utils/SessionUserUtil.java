package org.apache.streampark.console.flow.base.utils;

import org.apache.streampark.console.system.entity.User;

import org.slf4j.Logger;

public class SessionUserUtil {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static final Logger logger = LoggerUtil.getLogger();

  public static User getCurrentUser() {
    return null;
  }

  public static boolean isAdmin() {
    return true;
  }

  public static String getCurrentUsername() {
    return "admin";
  }
}
