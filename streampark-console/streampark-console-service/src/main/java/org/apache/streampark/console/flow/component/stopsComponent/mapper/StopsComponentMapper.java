package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentMapperProvider;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsComponentVo;
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
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface StopsComponentMapper {

  /** Query all stops templates */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentList")
  List<StopsComponent> getStopsComponentList();

  /**
   * Query template based on the stops template
   *
   * @param id id
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentById")
  StopsComponent getStopsComponentById(String id);

  /**
   * Query the stops template based on the id of the stops template (including the attribute list)
   *
   * @param id id
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        property = "properties",
        column = "id",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId"))
  })
  StopsComponent getStopsComponentAndPropertyById(String id);

  /**
   * Query the stops template according to the id of the stops group
   *
   * @param groupId groupId
   */
  @SelectProvider(
      type = StopsComponentMapperProvider.class,
      method = "getStopsComponentListByGroupId")
  List<StopsComponent> getStopsComponentListByGroupId(String groupId);

  /**
   * Query the stops template according to the id of the stops group
   *
   * @param groupId groupId
   */
  @SelectProvider(
      type = StopsComponentMapperProvider.class,
      method = "getManageStopsComponentListByGroupId")
  List<StopsComponentVo> getManageStopsComponentListByGroupId(String groupId);

  /**
   * Query the stops template according to the id of the stops group...
   *
   * @param stopsName stopsName
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentByName")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId",
                fetchType = FetchType.LAZY))
  })
  List<StopsComponent> getStopsComponentByName(String stopsName);

  /**
   * Add more than one FLOW_STOPS_TEMPLATE.
   *
   * @param stopsComponent stopsComponent
   */
  @InsertProvider(type = StopsComponentMapperProvider.class, method = "insertStopsComponent")
  int insertStopsComponent(StopsComponent stopsComponent);

  /**
   * getStopsComponentByBundle
   *
   * @param bundle bundle
   */
  @Select("select fst.* from flow_stops_template fst where fst.bundle=#{bundle} and enable_flag=1")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId",
                fetchType = FetchType.LAZY))
  })
  StopsComponent getStopsComponentByBundle(@Param("bundle") String bundle);

  @Delete("delete from flow_stops_template where engine_type = #{engineType}")
  int deleteStopsComponent(@Param("engineType") String engineType);

  @Delete("delete from flow_stops_template where id = #{id}")
  int deleteStopsComponentById(@Param("id") String id);

  @Select("select * from flow_stops_template where enable_flag = 1 and is_data_source = 1 ")
  List<StopsComponent> getDataSourceStopList();

  /**
   * getStopsComponentByBundle
   *
   * @param bundle bundle
   */
  @Select(
      "select fst.* from flow_stops_template fst "
          + "where fst.bundle=#{bundle} "
          + "and enable_flag=1 "
          + "and is_data_source =1 ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId",
                fetchType = FetchType.LAZY))
  })
  StopsComponent getDataSourceStopsComponentByBundle(@Param("bundle") String bundle);

  /**
   * get "stop" image by bundle
   *
   * @param bundle bundle
   */
  @Select("select image_url from flow_stops_template where bundle=#{bundle}")
  String getStopsComponentImageUrlByBundle(@Param("bundle") String bundle);

  @Select("select * from flow_stops_template where bundle=#{bundle}")
  StopsComponent getOnlyStopsComponentByBundle(@Param("bundle") String bundle);

  @Select("select * from flow_stops_template where stops_hub_id=#{stopsHubId}")
  List<StopsComponent> getStopsComponentByStopsHubId(@Param("stopsHubId") String stopsHubId);

  @UpdateProvider(type = StopsComponentMapperProvider.class, method = "updateStopsComponent")
  int updateStopsComponent(StopsComponent stopsComponent);

  /**
   * update component type
   *
   * @param stopsComponent stopsComponents
   */
  @UpdateProvider(
      type = StopsComponentMapperProvider.class,
      method = "updateComponentTypeByIdAndType")
  int updateComponentTypeByIdAndType(StopsComponent stopsComponent);

  /** get all default components @Return */
  @Select(
      "select * from flow_stops_template where component_type = 'DEFAULT' and ENGINE_TYPE  = #{engineType}")
  List<StopsComponent> getSystemDefaultStops(@Param("engineType") String engineType);

  @SelectProvider(
      type = StopsComponentMapperProvider.class,
      method = "getOnlyStopsComponentByBundles")
  List<StopsComponent> getOnlyStopsComponentByBundles(String[] bundles);
}
