package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentManageMapperProvider;
import org.apache.streampark.console.flow.component.stopsComponent.model.StopsComponentManage;

@Mapper
public interface StopsComponentManageMapper {

  /**
   * Add stopsComponentManage.
   *
   * @param stopsComponentManage
   * @return
   */
  @InsertProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "insertStopsComponentManage")
  public int insertStopsComponentManage(StopsComponentManage stopsComponentManage);

  /**
   * update StopsComponentManage.
   *
   * @param stopsComponent
   * @return
   */
  @InsertProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "updateStopsComponentManage")
  public int updateStopsComponentManage(StopsComponentManage stopsComponentManage);

  /**
   * Query StopsComponentManage by bundle and stopsGroups
   *
   * @param id
   * @return
   */
  @SelectProvider(
      type = StopsComponentManageMapperProvider.class,
      method = "getStopsComponentManageByBundleAndGroup")
  public StopsComponentManage getStopsComponentManageByBundleAndGroup(
      String bundle, String stopsGroups);
}
