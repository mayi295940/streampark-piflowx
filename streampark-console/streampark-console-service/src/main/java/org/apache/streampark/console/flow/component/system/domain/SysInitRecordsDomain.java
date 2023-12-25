package org.apache.streampark.console.flow.component.system.domain;

import org.apache.streampark.console.flow.component.system.entity.SysInitRecords;
import org.apache.streampark.console.flow.component.system.mapper.SysInitRecordsMapper;
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
public class SysInitRecordsDomain {

  private final SysInitRecordsMapper sysInitRecordsMapper;

  @Autowired
  public SysInitRecordsDomain(SysInitRecordsMapper sysInitRecordsMapper) {
    this.sysInitRecordsMapper = sysInitRecordsMapper;
  }

  public Integer insertSysInitRecords(SysInitRecords sysInitRecords) {
    return sysInitRecordsMapper.insertSysInitRecords(sysInitRecords);
  }

  public List<SysInitRecords> getSysInitRecordsList() {
    return sysInitRecordsMapper.getSysInitRecordsList();
  }

  public SysInitRecords getSysInitRecordsById(String id) {
    return sysInitRecordsMapper.getSysInitRecordsById(id);
  }

  public SysInitRecords getSysInitRecordsLastNew(int limit) {
    return sysInitRecordsMapper.getSysInitRecordsLastNew(limit);
  }
}
