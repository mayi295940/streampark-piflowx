package org.apache.streampark.console.flow.component.system.mapper;

import org.apache.streampark.console.flow.component.system.entity.SysMenu;
import org.apache.streampark.console.flow.component.system.mapper.provider.SysMenuMapperProvider;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysMenuMapper {

  /**
   * getSysMenuList
   *
   * @param role role
   */
  @SelectProvider(type = SysMenuMapperProvider.class, method = "getSysMenuList")
  List<SysMenu> getSysMenuList(String role);

  @SelectProvider(type = SysMenuMapperProvider.class, method = "getSampleMenuList")
  List<SysMenu> getSampleMenuList();

  @SelectProvider(type = SysMenuMapperProvider.class, method = "deleteSampleMenuListByIds")
  List<SysMenu> deleteSampleMenuListByIds(String[] ids);
}
