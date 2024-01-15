package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentPropertyMapperProvider;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface StopsComponentPropertyMapper {

  /**
   * Query all the attributes of the corresponding stops according to the stopsId
   *
   * @param stopsId stopsId
   */
  @SelectProvider(
      type = StopsComponentPropertyMapperProvider.class,
      method = "getStopsComponentPropertyByStopsId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<StopsComponentProperty> getStopsComponentPropertyByStopsId(String stopsId);

  /**
   * Add more than one FLOW_STOPS_PROPERTY_TEMPLATE List.
   *
   * @param stopsComponentPropertyList stopsComponentPropertyList
   */
  @InsertProvider(
      type = StopsComponentPropertyMapperProvider.class,
      method = "insertStopsComponentProperty")
  int insertStopsComponentProperty(
      @Param("stopsComponentPropertyList") List<StopsComponentProperty> stopsComponentPropertyList);

  @Delete("delete from flow_stops_property_template")
  int deleteStopsComponentProperty();

  @Delete("delete from flow_stops_property_template where fk_stops_id = #{fk_stops_id}")
  int deleteStopsComponentPropertyByStopId(@Param("fk_stops_id") String stopId);
}
