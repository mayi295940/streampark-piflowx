package org.apache.streampark.console.flow.component.system.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.Map;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.system.domain.AdminLogDomain;
import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.service.AdminLogService;
import org.apache.streampark.console.flow.component.system.vo.SysLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminLogServiceImpl implements AdminLogService {

  private final AdminLogDomain adminLogDomain;

  @Autowired
  public AdminLogServiceImpl(AdminLogDomain adminLogDomain) {
    this.adminLogDomain = adminLogDomain;
  }

  @Override
  public String getLogListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Page<SysLogVo> page = PageHelper.startPage(offset, limit, "crt_dttm desc");
    adminLogDomain.getLogList(isAdmin, username, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  @Override
  public void add(SysLog log) {
    adminLogDomain.insertSelective(log);
  }
}
