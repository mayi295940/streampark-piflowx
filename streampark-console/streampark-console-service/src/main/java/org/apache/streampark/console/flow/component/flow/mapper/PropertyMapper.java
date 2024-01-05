package org.apache.streampark.console.flow.component.flow.mapper;

import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.mapper.provider.PropertyMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.StopsPropertyVo;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface PropertyMapper {

  /**
   * Insert Property
   *
   * @param property property
   */
  @InsertProvider(type = PropertyMapperProvider.class, method = "addProperty")
  int addProperty(Property property);

  /**
   * Insert list<Property> Note that the method of spelling sql must use Map to connect Param
   * content to key value.
   *
   * @param propertyList (Content: The key is propertyList and the value is List<Property>)
   */
  @InsertProvider(type = PropertyMapperProvider.class, method = "addPropertyList")
  int addPropertyList(List<Property> propertyList);

  /** Querying group attribute information based on ID */
  @Select(
      "select fs.* from flow_stops fs "
          + "left join flow f on f.id = fs.fk_flow_id "
          + "where f.id = #{fid} "
          + "and fs.page_id = #{stopPageId} "
          + "and fs.enable_flag = 1  limit 1 ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        property = "dataSource",
        column = "fk_data_source_id",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper.getDataSourceById",
                fetchType = FetchType.LAZY)),
    @Result(
        property = "properties",
        column = "id",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.flow.mapper.PropertyMapper.getPropertyByStopsId",
                fetchType = FetchType.LAZY)),
    @Result(
        property = "customizedPropertyList",
        column = "id",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.CustomizedPropertyMapper.getCustomizedPropertyListByStopsId",
                fetchType = FetchType.LAZY))
  })
  Stops getStopGroupList(@Param("fid") String fid, @Param("stopPageId") String stopPageId);

  /** Modify stops attribute information */
  @UpdateProvider(type = PropertyMapperProvider.class, method = "updatePropertyCustomValue")
  int updatePropertyCustomValue(String username, String content, String id);

  /**
   * update property Method
   *
   * @param property property
   */
  @UpdateProvider(type = PropertyMapperProvider.class, method = "updateStopsProperty")
  int updateStopsProperty(Property property);

  /** query All StopsProperty List; */
  @Select("select * from flow_stops_property where enable_flag = 1 ")
  List<Property> getStopsPropertyList();

  @Select(
      "select * from flow_stops_property where fk_stops_id = #{stopsId} and enable_flag = 1 ORDER BY property_sort desc ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<Property> getPropertyListByStopsId(@Param("stopsId") String stopsId);

  /**
   * Query through ID flow_stops_property.
   *
   * @param id id
   */
  @Select(
      "select fsp.id, fsp.name, fsp.description,fsp.display_name,fsp.custom_value,fsp.version,"
          + "fsp.allowable_values,fsp.property_required,fsp.is_select,fsp.is_locked "
          + " from flow_stops_property fsp "
          + "where fsp.fk_stops_id = #{id}  "
          + "ORDER BY fsp.property_sort desc")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive"),
    @Result(column = "is_select", property = "isSelect")
  })
  List<Property> getPropertyByStopsId(@Param("id") String id);

  /**
   * delete StopsProperty according to ID;
   *
   * @param id id
   */
  @Delete("delete from flow_stops_property where id=#{id}")
  int deleteStopsPropertyById(@Param("id") String id);

  /**
   * delete StopsProperty by StopId;
   *
   * @param username username
   * @param stopId stopId
   */
  @UpdateProvider(
      type = PropertyMapperProvider.class,
      method = "updateStopPropertyEnableFlagByStopId")
  int updateStopPropertyEnableFlagByStopId(String username, String stopId);

  /**
   * Delete 'StopsProperty' by 'IsOldData' and 'StopsId'
   *
   * @param stopId stopId
   */
  @Update(
      "update flow_stops_property fsp set fsp.enable_flag=0 where fsp.is_old_data=1 and fsp.fk_stops_id = #{stopId}")
  int deletePropertiesByIsOldDataAndStopsId(String stopId);

  @Select(
      "select fsp.id, fsp.name, fsp.description,fsp.display_name,fsp.custom_value,"
          + "fsp.version,fsp.allowable_values,fsp.property_required,fsp.is_select,fsp.is_locked "
          + " from flow_stops_property fsp "
          + "where fsp.is_old_data=1 "
          + "and fsp.fk_stops_id = #{id}  "
          + "ORDER BY fsp.property_sort desc")
  List<Property> getOldPropertyByStopsId(@Param("stopId") String stopId);

  @Select(
      "select * from flow_stops_property "
          + "where is_old_data=1 "
          + "and fk_stops_id = #{stopsId} "
          + "and enable_flag = 1 "
          + "ORDER BY property_sort desc ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<Property> getOldPropertyListByStopsId(@Param("stopsId") String stopsId);

  @Select(
      "select * from flow_stops_property "
          + "where fk_stops_id = #{stopsId} "
          + "and enable_flag = 1 "
          + "ORDER BY property_sort desc ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<StopsPropertyVo> getPropertyVoListByStopsId(@Param("stopsId") String stopsId);

  @Select(
      "select * from flow_stops_property "
          + "where is_old_data=1 "
          + "and fk_stops_id = #{stopsId} "
          + "and enable_flag = 1 "
          + "ORDER BY property_sort desc ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<StopsPropertyVo> getOldPropertyVoListByStopsId(@Param("stopsId") String stopsId);
}
