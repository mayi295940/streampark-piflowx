package org.apache.streampark.console.flow.component.system.domain;

import org.apache.streampark.console.flow.component.system.entity.SysLog;
import org.apache.streampark.console.flow.component.system.mapper.AdminLogMapper;
import org.apache.streampark.console.flow.component.system.vo.SysLogVo;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class AdminLogDomain {

  private final AdminLogMapper adminLogMapper;

  @Autowired
  public AdminLogDomain(AdminLogMapper adminLogMapper) {
    this.adminLogMapper = adminLogMapper;
  }

  public List<SysLogVo> getLogList(boolean isAdmin, String username, String param) {
    return adminLogMapper.getLogList(isAdmin, username, param);
  }

  public int insertSelective(SysLog record) {
    return adminLogMapper.insertSelective(record);
  }
}
