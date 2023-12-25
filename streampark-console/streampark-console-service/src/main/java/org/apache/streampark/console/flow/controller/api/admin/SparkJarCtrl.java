package org.apache.streampark.console.flow.controller.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.streampark.console.flow.base.utils.SessionUserUtil;
import org.apache.streampark.console.flow.component.sparkJar.service.ISparkJarService;
import org.apache.streampark.console.flow.component.system.service.ILogHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "sparkJar api", tags = "sparkJar api")
@RestController
@RequestMapping("/sparkJar")
public class SparkJarCtrl {

  private final ISparkJarService sparkJarServiceImpl;
  private final ILogHelperService logHelperServiceImpl;

  @Autowired
  public SparkJarCtrl(
      ISparkJarService sparkJarServiceImpl, ILogHelperService logHelperServiceImpl) {
    this.sparkJarServiceImpl = sparkJarServiceImpl;
    this.logHelperServiceImpl = logHelperServiceImpl;
  }

  /** Query and enter the spark jar list */
  @RequestMapping(value = "/sparkJarListPage", method = RequestMethod.GET)
  @ResponseBody
  @ApiOperation(value = "sparkJarListPage", notes = "spark jar list")
  public String sparkJarListPage(Integer page, Integer limit, String param) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return sparkJarServiceImpl.sparkJarListPage(username, isAdmin, page, limit, param);
  }

  /** Upload spark jar file and save spark jar */
  @RequestMapping(value = "/uploadSparkJarFile", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "uploadSparkJarFile", notes = "update spark jar")
  public String uploadSparkJarFile(@RequestParam("file") MultipartFile file) {
    String username = SessionUserUtil.getCurrentUsername();
    logHelperServiceImpl.logAuthSucceed("uploadSparkJarFile " + file.getName(), username);
    return sparkJarServiceImpl.uploadSparkJarFile(username, file);
  }

  /** Mount spark jar */
  @RequestMapping(value = "/mountSparkJar", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "mountSparkJar", notes = "mount spark jar")
  public String mountSparkJar(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    return sparkJarServiceImpl.mountSparkJar(username, isAdmin, id);
  }

  /** unmount spark jar */
  @RequestMapping(value = "/unmountSparkJar", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "unmountSparkJar", notes = "mount spark jar")
  public String unmountSparkJar(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    return sparkJarServiceImpl.unmountSparkJar(username, isAdmin, id);
  }

  /** del spark jar */
  @RequestMapping(value = "/delSparkJar", method = RequestMethod.POST)
  @ResponseBody
  @ApiOperation(value = "delSparkJar", notes = "delete spark jar")
  public String delSparkJar(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    logHelperServiceImpl.logAuthSucceed("del SparkJar " + id, username);
    return sparkJarServiceImpl.delSparkJar(username, isAdmin, id);
  }
}
