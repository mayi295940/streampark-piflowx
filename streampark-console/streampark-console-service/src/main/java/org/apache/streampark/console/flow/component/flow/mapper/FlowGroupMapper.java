package org.apache.streampark.console.flow.component.flow.mapper;

import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.mapper.provider.FlowGroupMapperProvider;
import org.apache.streampark.console.flow.component.flow.vo.FlowGroupVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface FlowGroupMapper {

  @InsertProvider(type = FlowGroupMapperProvider.class, method = "addFlowGroup")
  int addFlowGroup(FlowGroup flowGroup);

  @UpdateProvider(type = FlowGroupMapperProvider.class, method = "updateFlowGroup")
  int updateFlowGroup(FlowGroup flowGroup);

  @UpdateProvider(type = FlowGroupMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String username, String id, boolean enableFlag);

  @SelectProvider(type = FlowGroupMapperProvider.class, method = "getFlowGroupListParam")
  List<FlowGroupVo> getFlowGroupListParam(String username, boolean isAdmin, String param);

  /**
   * Query FlowGroup based on FlowGroup Id
   *
   * @param id FlowGroup Id
   */
  @SelectProvider(type = FlowGroupMapperProvider.class, method = "getFlowGroupById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByFlowGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowList",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowListGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper.getFlowGroupListByFkGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowGroupPathsList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupPathsMapper.getFlowGroupPathsByFlowGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "fk_flow_group_id",
        property = "flowGroup",
        one =
            @One(
                select = "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper.getFlowGroupById",
                fetchType = FetchType.LAZY))
  })
  FlowGroup getFlowGroupById(String id);

  /**
   * Query flow by flowGroupId
   *
   * @param fkFlowGroupId fkFlowGroupId
   */
  @SelectProvider(type = FlowGroupMapperProvider.class, method = "getFlowGroupListByFkGroupId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "corg.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByFlowGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowList",
        many =
            @Many(
                select = "org.apache.streampark.console.flow.component.flow.mapper.FlowMapper.getFlowListGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupMapper.getFlowGroupListByFkGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowGroupPathsList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.FlowGroupPathsMapper.getFlowGroupPathsByFlowGroupId",
                fetchType = FetchType.LAZY))
  })
  List<FlowGroup> getFlowGroupListByFkGroupId(String fkFlowGroupId);

  /**
   * Query FlowGroup based on FlowGroup Id
   *
   * @param id FlowGroup Id
   */
  @SelectProvider(type = FlowGroupMapperProvider.class, method = "getFlowGroupById")
  FlowGroup getFlowGroupBaseInfoById(String id);

  @Select(
      "select * from flow_group s where s.enable_flag=1 and s.fk_flow_group_id=#{fid} and s.page_id=#{pageId}")
  FlowGroup getFlowGroupByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

  @Select(
      "select name from flow_group s where s.enable_flag=1 and s.fk_flow_group_id=#{fid} and s.page_id=#{pageId}")
  String getFlowGroupNameByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

  @Select(
      "select s.id from flow_group s where s.enable_flag=1 and s.fk_flow_group_id=#{fid} and s.page_id=#{pageId}")
  String getFlowGroupIdByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

  @Select(
      "select MAX(page_id+0) from flow_group where enable_flag=1 and fk_flow_group_id=#{flowGroupId} ")
  Integer getMaxFlowGroupPageIdByFlowGroupId(@Param("flowGroupId") String flowGroupId);

  @Select(
      value =
          "select s.id from flow_group s where s.enable_flag=1 and s.fk_flow_group_id =#{fid} and s.name=#{flowGroupName}")
  String getFlowGroupIdByNameAndFid(
      @Param("fid") String fid, @Param("flowGroupName") String flowGroupName);

  @Select(
      "select c.name from  flow_group c where c.enable_flag=1 and c.name=#{name} and c.fk_flow_group_id=#{flowGroupId}")
  String[] getFlowGroupNamesByNameAndEnableFlagInGroup(
      @Param("flowGroupId") String flowGroupId, @Param("name") String name);

  /**
   * query flowGroup name by flowGroup name
   *
   * @param flowGroupName flowGroup name
   */
  @Select(
      "SELECT name FROM flow_group WHERE enable_flag=1 AND fk_flow_group_id IS NULL AND is_example=0 AND name=#{flowGroupName} ")
  String getFlowGroupName(@Param("flowGroupName") String flowGroupName);

}
