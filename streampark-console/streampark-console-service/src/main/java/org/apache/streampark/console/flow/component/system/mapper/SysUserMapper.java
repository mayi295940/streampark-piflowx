package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysUser;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysUserMapperProvider;
import org.apache.streampark.console.flow.component.system.vo.SysUserVo;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface SysUserMapper {

  @InsertProvider(type = SysUserMapperProvider.class, method = "insertSysUser")
  int insertSysUser(SysUser sysUser);

  @InsertProvider(type = SysUserMapperProvider.class, method = "updateSysUser")
  int updateSysUser(SysUser user);

  @SelectProvider(type = SysUserMapperProvider.class, method = "getSysUserById")
  SysUser getSysUserById(boolean isAdmin, String username, String id);

  @SelectProvider(type = SysUserMapperProvider.class, method = "getSysUserById")
  SysUserVo getSysUserVoById(boolean isAdmin, String username, String id);

  @SelectProvider(type = SysUserMapperProvider.class, method = "getSysUserVoList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "role",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.system.mapper.SysRoleMapper.getSysRoleBySysUserId",
                fetchType = FetchType.EAGER))
  })
  List<SysUserVo> getSysUserVoList(boolean isAdmin, String username, String param);

  @SelectProvider(type = SysUserMapperProvider.class, method = "findUserByNameLike")
  List<SysUser> findUserByNameLike(String name);

  @SelectProvider(type = SysUserMapperProvider.class, method = "findUserByName")
  List<SysUser> findUserByName(String name);

  @SelectProvider(type = SysUserMapperProvider.class, method = "findUserByUserName")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "roles",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.system.mapper.SysRoleMapper.getSysRoleListBySysUserId",
                fetchType = FetchType.EAGER))
  })
  SysUser findUserByUserName(String userName);

  @Delete("DELETE FROM sys_init_records WHERE id=#{id}")
  int deleteUserById(@Param("id") String id);

  @Select("select username from sys_user where username = #{username}")
  String checkUsername(@Param("username") String username);
}
