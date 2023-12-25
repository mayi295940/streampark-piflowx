package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHub;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsHubMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
public interface StopsHubMapper {

  /**
   * add StopsHub
   *
   * @param stopsHub stopsHub
   */
  @InsertProvider(type = StopsHubMapperProvider.class, method = "addStopsHub")
  int addStopHub(StopsHub stopsHub);

  /**
   * update StopsHub
   *
   * @param stopsHub stopsHub
   */
  @UpdateProvider(type = StopsHubMapperProvider.class, method = "updateStopsHub")
  int updateStopHub(StopsHub stopsHub);

  /** query all StopsHub */
  @SelectProvider(type = StopsHubMapperProvider.class, method = "getStopsHubList")
  List<StopsHub> getStopsHubList(String username, boolean isAdmin);

  @SelectProvider(type = StopsHubMapperProvider.class, method = "getStopsHubListByName")
  List<StopsHub> getStopsHubByName(String username, boolean isAdmin, String jarName);

  @SelectProvider(type = StopsHubMapperProvider.class, method = "getStopsHubById")
  StopsHub getStopsHubById(String username, boolean isAdmin, String id);

  @UpdateProvider(type = StopsHubMapperProvider.class, method = "updateEnableFlagById")
  int deleteStopsHubById(String username, String id);

  @SelectProvider(type = StopsHubMapperProvider.class, method = "getStopsHubListParam")
  List<StopsHub> getStopsHubListParam(String username, boolean isAdmin, String param);

  @SelectProvider(type = StopsHubMapperProvider.class, method = "getAllStopsHub")
  List<StopsHub> getAllStopsHub();

  @UpdateProvider(type = StopsHubMapperProvider.class, method = "updateStopHubType")
  int updateStopHubType(StopsHub scalaStopsHub);

  @SelectProvider(type = StopsHubMapperProvider.class, method = "getStopsHubByJarName")
  List<StopsHub> getStopsHubByJarName(String username, boolean isAdmin, String jarName);
}
