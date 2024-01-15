package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentGroup;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsComponentGroupProvider;
import org.apache.streampark.console.flow.component.stopsComponent.vo.StopsComponentGroupVo;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface StopsComponentGroupMapper {

  /** Query all groups */
  @SelectProvider(type = StopsComponentGroupProvider.class, method = "getStopGroupList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "stopsComponentList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getStopsComponentListByGroupId"))
  })
  List<StopsComponentGroup> getStopGroupList(String engineType);

  /** Query all groups */
  @SelectProvider(type = StopsComponentGroupProvider.class, method = "getStopGroupList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "stopsComponentVoList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getManageStopsComponentListByGroupId"))
  })
  List<StopsComponentGroupVo> getManageStopGroupList();

  /**
   * Query the stops template group based on the group id
   *
   * @param stopsId stopsId
   */
  StopsComponentGroup getStopGroupById(String stopsId);

  /**
   * add flow_stops_groups
   *
   * @param stopGroup stopGroup
   */
  @Insert(
      "INSERT INTO flow_stops_groups(id,crt_dttm,crt_user,enable_flag,last_update_dttm,"
          + "last_update_user,version,group_name,engine_type) "
          + "VALUES (#{id},#{crtDttm},#{crtUser},#{enableFlag},#{lastUpdateDttm},"
          + "#{lastUpdateUser},#{version},#{groupName},#{engineType})")
  @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
  int insertStopGroup(StopsComponentGroup stopGroup);

  @Insert(
      "INSERT INTO association_groups_stops_template(groups_id,stops_template_id,engine_type)"
          + " VALUES (#{groups_id},#{stops_template_id}, #{engineType})")
  int insertAssociationGroupsStopsTemplate(
      @Param("groups_id") String stopGroupId,
      @Param("stops_template_id") String stopsTemplateId,
      @Param("engineType") String engineType);

  /**
   * Query flow_stops_groups based on groupName
   *
   * @param groupNameList group name list
   * @param engineType engineType
   */
  @Select(
      "<script>"
          + "select id, group_name from flow_stops_groups where enable_flag = 1 "
          + "and engine_type = #{engineType} "
          + "and group_name in "
          + "<foreach item='groupName' index='index' collection='groupNameList' open='(' separator=', ' close=')'>"
          + "#{groupName}"
          + "</foreach>"
          + "</script>")
  List<StopsComponentGroup> getStopGroupByNameList(
      @Param("groupNameList") List<String> groupNameList, @Param("engineType") String engineType);

  @SelectProvider(type = StopsComponentGroupProvider.class, method = "getStopGroupByGroupNameList")
  List<StopsComponentGroup> getStopGroupByGroupNameList(
      @Param("groupName") List<String> groupName, @Param("engineType") String engineType);

  /**
   * Query flow_stops_groups based on groupName
   *
   * @param groupName groupName
   */
  @Select("select id from flow_stops_groups where enable_flag = 1 and group_name = #{groupName}")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "stopsComponentList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getStopsComponentListByGroupId"))
  })
  List<StopsComponentGroup> getStopGroupByName(@Param("groupName") String groupName);

  @Delete("delete from association_groups_stops_template where engine_type = #{engineType}")
  int deleteGroupCorrelation(@Param("engineType") String engineType);

  @Delete(
      "delete from association_groups_stops_template "
          + "where groups_id =#{groups_id} "
          + "and stops_template_id = #{stops_template_id}")
  int deleteGroupCorrelationByGroupIdAndStopId(
      @Param("groups_id") String stopGroupId, @Param("stops_template_id") String stopsTemplateId);

  @Delete(
      "delete from association_groups_stops_template where stops_template_id = #{stops_template_id}")
  int deleteGroupCorrelationByStopId(@Param("stops_template_id") String stopsTemplateId);

  @Delete("delete from flow_stops_groups where engine_type = #{engineType}")
  int deleteGroup(@Param("engineType") String engineType);

  @Delete("delete from flow_stops_groups where id =#{groups_id}")
  int deleteGroupById(@Param("groups_id") String groupsId);

  @Select("select count(*) from association_groups_stops_template where groups_id =#{groups_id}")
  int getGroupStopCount(@Param("groups_id") String groupsId);
}
