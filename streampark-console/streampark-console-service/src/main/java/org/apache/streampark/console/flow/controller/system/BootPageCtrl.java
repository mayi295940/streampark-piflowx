package org.apache.streampark.console.flow.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.streampark.console.flow.base.util.ReturnMapUtils;
import org.apache.streampark.console.flow.base.util.SessionUserUtil;
import org.apache.streampark.console.flow.component.system.service.ISysInitRecordsService;

@Controller
@RequestMapping("/bootPage")
public class BootPageCtrl {

  @Autowired private ISysInitRecordsService sysInitRecordsServiceImpl;

  @RequestMapping("/isInBootPage")
  @ResponseBody
  public String isInBootPage() {
    boolean inBootPage = sysInitRecordsServiceImpl.isInBootPage();
    inBootPage = false;
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("isIn", inBootPage);
  }

  @RequestMapping("/initComponents")
  @ResponseBody
  public String initComponents() {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    return sysInitRecordsServiceImpl.initComponents(currentUsername);
  }

  @RequestMapping("/threadMonitoring")
  @ResponseBody
  public String threadMonitoring() {
    String currentUsername = SessionUserUtil.getCurrentUsername();
    return sysInitRecordsServiceImpl.threadMonitoring(currentUsername);
  }
}
