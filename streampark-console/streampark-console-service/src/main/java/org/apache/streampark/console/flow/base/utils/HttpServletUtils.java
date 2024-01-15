package org.apache.streampark.console.flow.base.utils;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

public class HttpServletUtils {

  private HttpServletUtils() {}

  private static Map<String, Object> getUser(HttpServletRequest request) {
    if (request == null || request.getSession() == null) {
      return null;
    }
    Object proxyUserObj = request.getSession().getAttribute("proxyUser");
    if (proxyUserObj != null) {
      return (Map<String, Object>) request.getSession().getAttribute("proxyUser");
    }
    return (Map<String, Object>) request.getSession().getAttribute("user");
  }

  public static Long getUserId(HttpServletRequest request) {
    Map<String, Object> user = getUser(request);
    if (user == null) {
      return null;
    }
    return (Long) user.get("userId");
  }

  public static String getUserName(HttpServletRequest request) {
    Map<String, Object> user = getUser(request);
    if (user == null) {
      return null;
    }
    return (String) user.get("username");
  }
}
