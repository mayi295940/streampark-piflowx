package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentManage;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentManageMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface StopsComponentManageMapper {

  /**
   * Add stopsComponentManage.
   *
   * @param stopsComponentManage stopsComponentManage
   */
  @InsertProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "insertStopsComponentManage")
  int insertStopsComponentManage(StopsComponentManage stopsComponentManage);

  /**
   * update StopsComponentManage.
   *
   * @param stopsComponentManage stopsComponentManage
   */
  @InsertProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "updateStopsComponentManage")
  int updateStopsComponentManage(StopsComponentManage stopsComponentManage);

  /**
   * Query StopsComponentManage by bundle and stopsGroups
   *
   * @param bundle bundle
   * @param stopsGroups stopsGroups
   * @return StopsComponentManage
   */
  @SelectProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "getStopsComponentManageByBundleAndGroup")
  StopsComponentManage getStopsComponentManageByBundleAndGroup(String bundle, String stopsGroups);
}
