package org.apache.streampark.console.flow.component.flow.mapper;

import org.apache.streampark.console.flow.component.flow.entity.FlowStopsPublishing;
import org.apache.streampark.console.flow.component.flow.mapper.provider.FlowStopsPublishingMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.FlowStopsPublishingVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

/** Flow Stops Publishing table */
@Mapper
public interface FlowStopsPublishingMapper {

  /**
   * Add FlowStopsPublishing
   *
   * @param flowStopsPublishing flowStopsPublishing
   */
  @InsertProvider(type = FlowStopsPublishingMapperProvider.class, method = "addFlowStopsPublishing")
  int addFlowStopsPublishing(FlowStopsPublishing flowStopsPublishing);

  /**
   * update FlowStopsPublishing
   *
   * @param username username
   * @param publishingId publishingId
   * @param name name
   */
  @UpdateProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "updateFlowStopsPublishingName")
  int updateFlowStopsPublishingName(String username, String publishingId, String name);

  /**
   * update EnableFlag By Id
   *
   * @param username username
   * @param publishingId publishingId
   * @param stopsId stopsId
   */
  @UpdateProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "updateFlowStopsPublishingEnableFlagByPublishingIdAndStopId")
  int updateFlowStopsPublishingEnableFlagByPublishingIdAndStopId(
      String username, String publishingId, String stopsId);

  /**
   * update EnableFlag By Id
   *
   * @param username username
   * @param publishingId publishingId
   */
  @UpdateProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "updateFlowStopsPublishingEnableFlagByPublishingId")
  int updateFlowStopsPublishingEnableFlagByPublishingId(String username, String publishingId);

  /** Get FlowStopsPublishing List */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getFlowStopsPublishingList")
  @Results({@Result(id = true, column = "fk_flow_id", property = "flowId")})
  List<FlowStopsPublishing> getFlowStopsPublishingList(
      String username, boolean isAdmin, String param);

  /**
   * Get FlowStopsPublishing List By id
   *
   * @param publishingId publishingId
   */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getFlowStopsPublishingByPublishingId")
  @Results({
    @Result(
        column = "stops_id",
        property = "stopsVo",
        one =
            @One(
                select = "org.apache.streampark.console.flow.component.flow.mapper.StopsMapper.getStopsVoById",
                fetchType = FetchType.LAZY))
  })
  List<FlowStopsPublishingVo> getFlowStopsPublishingVoByPublishingId(String publishingId);

  /**
   * Get FlowStopsPublishing List By id
   *
   * @param publishingId publishingId
   */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getPublishingStopsIdsByPublishingId")
  List<String> getPublishingStopsIdsByPublishingId(String publishingId);

  /**
   * Get FlowStopsPublishing List By id
   *
   * @param publishingId publishingId
   */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getFlowStopsPublishingByPublishingIdAndCreateUser")
  List<String> getFlowStopsPublishingByPublishingIdAndCreateUser(
      String username, String publishingId);

  /** Get FlowStopsPublishing List */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getFlowStopsPublishingListByPublishingIdAndStopsId")
  List<FlowStopsPublishing> getFlowStopsPublishingListByPublishingIdAndStopsId(
      String publishingId, String stopsId);

  /** Get FlowStopsPublishing List by flowId */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getFlowStopsPublishingListByFlowId")
  List<FlowStopsPublishing> getFlowStopsPublishingListByFlowId(String username, String flowId);

  /**
   * Query PublishingName list by StopsIds
   *
   * @param stopsIds stopsIds
   */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getPublishingNameListByStopsIds")
  List<String> getPublishingNameListByStopsIds(List<String> stopsIds);

  /**
   * Query PublishingName list by flowId
   *
   * @param flowId flowId
   */
  @SelectProvider(
      type = FlowStopsPublishingMapperProvider.class,
      method = "getPublishingNameListByFlowId")
  List<String> getPublishingNameListByFlowId(String flowId);

  /** Get FlowStopsPublishing List by flowId */
  @Select(
      "SELECT DISTINCT fs.fk_flow_id "
          + "FROM flow_stops_publishing fsp "
          + "LEFT JOIN flow_stops fs "
          + "ON fsp.stops_id=fs.id "
          + "WHERE fsp.enable_flag=1 "
          + "AND fs.enable_flag=1 "
          + "AND fsp.publishing_id=#{publishingId}")
  List<String> getFlowIdByPublishingId(@Param("publishingId") String publishingId);

  /** Get publishingName List by stopsId */
  @Select(
      "SELECT f0.name FROM flow_stops_publishing f0 WHERE f0.enable_flag = 1 AND f0.stops_id = #{stopsId}")
  List<String> getPublishingNamesByStopsId(@Param("stopsId") String stopsId);

  /** Get FlowStopsPublishing id List by publishingName */
  @Select(
      "SELECT f0.id FROM flow_stops_publishing f0 WHERE f0.enable_flag=1 AND f0.name=#{publishingName}")
  List<String> getPublishingIdsByPublishingName(@Param("publishingName") String publishingName);
}
