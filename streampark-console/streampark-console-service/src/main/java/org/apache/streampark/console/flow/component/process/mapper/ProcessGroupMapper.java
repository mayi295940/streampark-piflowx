package org.apache.streampark.console.flow.component.process.mapper;

import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.process.entity.ProcessGroup;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessGroupMapperProvider;
import org.apache.streampark.console.flow.component.process.vo.ProcessGroupVo;

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

import java.util.List;
import java.util.Map;

@Mapper
public interface ProcessGroupMapper {

  @InsertProvider(type = ProcessGroupMapperProvider.class, method = "addProcessGroup")
  int addProcessGroup(ProcessGroup processGroup);

  /**
   * update updateProcessGroup
   *
   * @param processGroup processGroup
   */
  @UpdateProvider(type = ProcessGroupMapperProvider.class, method = "updateProcessGroup")
  int updateProcessGroup(ProcessGroup processGroup);

  /**
   * Query processGroup by processGroup ID
   *
   * @param id id
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupByIdAndUser")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "fk_flow_process_group_id",
        property = "processGroup",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupById",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessMapper.getProcessByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper.getProcessPathByProcessGroupId",
                fetchType = FetchType.LAZY))
  })
  ProcessGroup getProcessGroupByIdAndUser(String username, boolean isAdmin, String id);

  /**
   * Query processGroup by processGroup ID
   *
   * @param id id
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "fk_flow_process_group_id",
        property = "processGroup",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupById",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessMapper.getProcessByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper.getProcessPathByProcessGroupId",
                fetchType = FetchType.LAZY))
  })
  ProcessGroup getProcessGroupById(String id);

  /**
   * Query processGroup by processGroup ID
   *
   * @param processGroupId processGroupId
   */
  @SelectProvider(
      type = ProcessGroupMapperProvider.class,
      method = "getProcessGroupByProcessGroupId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "fk_flow_process_group_id",
        property = "processGroup",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupById",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessMapper.getProcessByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper.getProcessPathByProcessGroupId",
                fetchType = FetchType.LAZY))
  })
  ProcessGroup getProcessGroupByProcessGroupId(String processGroupId);

  /**
   * getRunModeTypeById
   *
   * @param processGroupId processGroupId
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getRunModeTypeById")
  RunModeType getRunModeTypeById(String username, boolean isAdmin, String processGroupId);

  /**
   * Query process according to process appId
   *
   * @param appID appID
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupByAppId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "fk_flow_process_group_id",
        property = "processGroup",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupById",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessMapper.getProcessByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper.getProcessPathByProcessGroupId",
                fetchType = FetchType.LAZY))
  })
  ProcessGroup getProcessGroupByAppId(String appID);

  /**
   * Query process according to process appId
   *
   * @param appID appID
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupIdByAppId")
  List<String> getProcessGroupIdByAppId(String appID);

  /**
   * Query process list according to process appid array
   *
   * @param appIDs appIDs
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupListByAppIDs")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "processList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessMapper.getProcessByProcessGroupId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processGroupPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupPathMapper.getProcessPathByProcessGroupId",
                fetchType = FetchType.LAZY))
  })
  List<ProcessGroup> getProcessGroupListByAppIDs(String[] appIDs);

  /** getRunningProcessGroup */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getRunningProcessGroup")
  List<String> getRunningProcessGroup();

  /**
   * Query processGroup list according to param(processGroupList)
   *
   * @param param param
   */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupListByParam")
  @Results({
    @Result(id = true, column = "id", property = "id"),
  })
  List<ProcessGroupVo> getProcessGroupListByParam(String username, boolean isAdmin, String param);

  /** Query processGroup list */
  @SelectProvider(type = ProcessGroupMapperProvider.class, method = "getProcessGroupList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
  })
  List<ProcessGroup> getProcessGroupList(
      @Param("username") String username, @Param("isAdmin") boolean isAdmin);

  @UpdateProvider(type = ProcessGroupMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String id, String userName);

  @Select(
      "select app_id from flow_process_group "
          + "where enable_flag=1 "
          + "and app_id is not null "
          + "and ( (state!='COMPLETED' and state!='FINISHED' and state!='FAILED' and state!='KILLED') or state is null )")
  List<String> getRunningProcessGroupAppId();

  @SelectProvider(
      type = ProcessGroupMapperProvider.class,
      method = "getProcessGroupNamesAndPageIdsByPageIds")
  List<Map<String, Object>> getProcessGroupNamesAndPageIdsByPageIds(
      String fid, List<String> pageIds);

  @Select(
      "select s.id from flow_process_group s where s.enable_flag=1 and s.fk_flow_process_group_id=#{fid} and s.page_id=#{pageId}")
  String getProcessGroupIdByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

  @Select(
      "select * from flow_process_group s where s.enable_flag=1 and s.fk_flow_process_group_id=#{fid} and s.page_id=#{pageId}")
  ProcessGroup getProcessGroupByPageId(@Param("fid") String fid, @Param("pageId") String pageId);
}
