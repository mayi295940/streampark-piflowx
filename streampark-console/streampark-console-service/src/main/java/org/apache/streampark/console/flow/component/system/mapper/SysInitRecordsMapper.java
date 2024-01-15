package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysInitRecords;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysInitRecordsMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysInitRecordsMapper {

  @InsertProvider(type = SysInitRecordsMapperProvider.class, method = "insertSysInitRecords")
  Integer insertSysInitRecords(SysInitRecords sysInitRecords);

  @Select("select * from sys_init_records")
  List<SysInitRecords> getSysInitRecordsList();

  @Select("select * from sys_init_records where id=#{id}")
  SysInitRecords getSysInitRecordsById(@Param("id") String id);

  @Select("select * from sys_init_records order by init_date desc limit #{limit}")
  SysInitRecords getSysInitRecordsLastNew(@Param("limit") int limit);
}
