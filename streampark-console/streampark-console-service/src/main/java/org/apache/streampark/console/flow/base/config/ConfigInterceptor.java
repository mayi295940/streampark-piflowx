package org.apache.streampark.console.flow.base.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Defining interceptors */
@Component
public class ConfigInterceptor implements HandlerInterceptor {

  Logger logger = LoggerUtil.getLogger();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String contextPath =
        (null == SysParamsCache.SYS_CONTEXT_PATH ? "" : SysParamsCache.SYS_CONTEXT_PATH);
    String requestURI = request.getRequestURI();
    if (requestURI.startsWith(contextPath + "/error")) {
      // response.sendRedirect(contextPath + "/page/error/errorPage.html"); // Redirect to the boot
      // page
      return false;
    }
    return true;
  }
}
