package org.apache.streampark.console.flow.component.process.mapper;

import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
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
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.mapper.provider.ProcessMapperProvider;

@Mapper
public interface ProcessMapper {

  /**
   * addProcess
   *
   * @param process process
   */
  @InsertProvider(type = ProcessMapperProvider.class, method = "addProcess")
  int addProcess(Process process);

  /**
   * update process
   *
   * @param process process
   */
  @UpdateProvider(type = ProcessMapperProvider.class, method = "updateProcess")
  int updateProcess(Process process);

  /**
   * Query process by process ID
   *
   * @param id id
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "fk_flow_process_group_id",
        property = "processGroup",
        one =
            @One(
                select = "org.apache.streampark.console.flow.component.process.mapper.ProcessGroupMapper.getProcessGroupById",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "flowGlobalParamsList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.flow.mapper.FlowGlobalParamsMapper.getFlowGlobalParamsByProcessId",
                fetchType = FetchType.LAZY))
  })
  Process getProcessById(String username, boolean isAdmin, String id);

  /**
   * Query process by processGroup ID
   *
   * @param processGroupId processGroupId
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessByProcessGroupId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  List<Process> getProcessByProcessGroupId(String processGroupId);

  /** Query process List(processList) */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  List<Process> getProcessList();

  /**
   * Query process list according to param(processList)
   *
   * @param param param
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessListByParam")
  @Results({
    @Result(id = true, column = "id", property = "id"),
  })
  List<Process> getProcessListByParam(String username, boolean isAdmin, String param);

  /** Query processGroup list according to param(processList) */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessGroupListByParam")
  @Results({
    @Result(id = true, column = "id", property = "id"),
  })
  List<Process> getProcessGroupListByParam(String username, boolean isAdmin, String param);

  /** Query the running process list according to the flowid(processList) */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getRunningProcessList")
  List<Process> getRunningProcessList(String flowId);

  /**
   * Query process according to process app id
   *
   * @param appID appID
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessByAppId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  Process getProcessByAppId(String appID);

  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessIdByAppId")
  String getProcessIdByAppId(String appID);

  /**
   * Query process according to process app id
   *
   * @param appID appID
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessNoGroupByAppId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  List<Process> getProcessNoGroupByAppId(String appID);

  /**
   * Query process list according to process appid array
   *
   * @param appIDs appIDs
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessListByAppIDs")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  List<Process> getProcessListByAppIDs(@Param("appIDs") String[] appIDs);

  /**
   * update process EnableFlag
   *
   * @param id id
   * @param username username
   */
  @UpdateProvider(type = ProcessMapperProvider.class, method = "updateEnableFlag")
  int updateEnableFlag(String id, String username);

  /** Query tasks that need to be synchronized */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getRunningProcess")
  List<String> getRunningProcess();

  /**
   * Query process by pageId
   *
   * @param processGroupId processGroupId
   * @param pageId pageId
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessByPageId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  Process getProcessByPageId(
      String username, boolean isAdmin, String processGroupId, String pageId);

  /**
   * Query process by pageIds
   *
   * @param processGroupId processGroupId
   * @param pageIds pageIds
   */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getProcessByPageIds")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "mxGraphModel",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGraphModelMapper.getMxGraphModelByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processStopList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessStopMapper.getProcessStopByProcessId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "processPathList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.process.mapper.ProcessPathMapper.getProcessPathByProcessId",
                fetchType = FetchType.LAZY))
  })
  List<Process> getProcessByPageIds(
      @Param("processGroupId") String processGroupId, @Param("pageIds") String[] pageIds);

  @Select("select fp.run_mode_type from flow_process fp where fp.enable_flag = 1 and fp.id=#{id}")
  RunModeType getProcessRunModeTypeById(@Param("id") String id);

  @Select(
      "select app_id from flow_process "
          + "where enable_flag=1 "
          + "and fk_flow_process_group_id is null "
          + "and app_id is not null "
          + "and ( ( state!='COMPLETED' and state!='FINISHED' and state!='FAILED' and state!='KILLED' ) or state is null )")
  List<String> getRunningProcessAppId();

  @Select(
      "select s.id from flow_process s "
          + "where s.enable_flag=1 "
          + "and s.fk_flow_process_group_id=#{fid} "
          + "and s.page_id=#{pageId}")
  String getProcessIdByPageId(@Param("fid") String fid, @Param("pageId") String pageId);

  @Select("select state from flow_process where enable_flag=1 and id=#{id} ")
  ProcessState getProcessStateById(@Param("id") String id);

  /** get globalParams ids by process id */
  @SelectProvider(type = ProcessMapperProvider.class, method = "getGlobalParamsIdsByProcessId")
  String[] getGlobalParamsIdsByProcessId(String processId);

  /**
   * link GlobalParams
   *
   * @param processId processId
   * @param globalParamsIds globalParamsIds
   */
  @InsertProvider(type = ProcessMapperProvider.class, method = "linkGlobalParams")
  int linkGlobalParams(String processId, String[] globalParamsIds);

  /**
   * unlink GlobalParams
   *
   * @param processId processId
   * @param globalParamsIds globalParamsIds
   */
  @DeleteProvider(type = ProcessMapperProvider.class, method = "unlinkGlobalParams")
  int unlinkGlobalParams(String processId, String[] globalParamsIds);
}
