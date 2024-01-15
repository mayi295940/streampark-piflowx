package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysRole;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysRoleMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysRoleMapper {

  @Select("select Max(id) from sys_role")
  long getMaxId();

  /**
   * getSysRoleListBySysUserId
   *
   * @param sysUserId sysUserId
   */
  @SelectProvider(type = SysRoleMapperProvider.class, method = "getSysRoleListBySysUserId")
  List<SysRole> getSysRoleListBySysUserId(String sysUserId);

  @InsertProvider(type = SysRoleMapperProvider.class, method = "insertSysRoleList")
  int insertSysRoleList(@Param("userId") String userId, @Param("roles") List<SysRole> roles);

  @SelectProvider(type = SysRoleMapperProvider.class, method = "getSysRoleBySysUserId")
  SysRole getSysRoleBySysUserId(String sysUserId);
}
