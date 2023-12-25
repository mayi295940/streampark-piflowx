package org.apache.streampark.console.flow.component.flow.domain;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.mapper.FlowMapper;
import org.apache.streampark.console.flow.component.flow.mapper.PathsMapper;
import org.apache.streampark.console.flow.component.flow.utils.FlowGlobalParamsUtils;
import org.apache.streampark.console.flow.component.flow.vo.FlowVo;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxGraphModelDomain;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class FlowDomain extends StopsDomain {

  @Autowired private FlowMapper flowMapper;

  @Autowired private PathsMapper pathsMapper;

  @Autowired private MxGraphModelDomain mxGraphModelDomain;

  public int saveOrUpdate(Flow flow, String[] globalParamsIds) throws Exception {
    if (null == flow) {
      throw new Exception("save failed, flow is null");
    }
    if (StringUtils.isBlank(flow.getId())) {
      return addFlow(flow);
    }
    return updateFlow(flow);
  }

  public int addFlow(Flow flow) throws Exception {
    if (null == flow) {
      throw new Exception("save failed");
    }
    String id = flow.getId();
    if (StringUtils.isBlank(id)) {
      flow.setId(UUIDUtils.getUUID32());
    }
    int affectedRows = flowMapper.addFlow(flow);
    if (affectedRows <= 0) {
      throw new Exception("save failed");
    }

    List<Stops> stopsList = flow.getStopsList();
    if (null != stopsList && stopsList.size() > 0) {
      for (Stops stops : stopsList) {
        if (null == stops) {
          continue;
        }
        stops.setFlow(flow);
        affectedRows += addStops(stops);
      }
    }

    List<Paths> pathsList = flow.getPathsList();
    if (null != pathsList && pathsList.size() > 0) {
      for (Paths paths : pathsList) {
        if (null == paths) {
          continue;
        }
        paths.setFlow(flow);
        ;
        affectedRows += addPaths(paths);
      }
    }

    MxGraphModel mxGraphModel = flow.getMxGraphModel();
    if (null != mxGraphModel) {
      mxGraphModel.setFlow(flow);
      affectedRows += mxGraphModelDomain.addMxGraphModel(mxGraphModel);
    }
    String[] globalParamsIds =
        FlowGlobalParamsUtils.globalParamsToIds(flow.getFlowGlobalParamsList());
    if (null != globalParamsIds && globalParamsIds.length > 0) {
      affectedRows += linkGlobalParams(flow.getId(), globalParamsIds);
    }
    return affectedRows;
  }

  public int addPaths(Paths paths) throws Exception {
    if (null == paths) {
      throw new Exception("save failed");
    }
    String id = paths.getId();
    if (StringUtils.isBlank(id)) {
      paths.setId(UUIDUtils.getUUID32());
    }
    int affectedRows = pathsMapper.addPaths(paths);
    if (affectedRows <= 0) {
      throw new Exception("save failed");
    }
    return affectedRows;
  }

  public int updateFlow(Flow flow) throws Exception {
    if (null == flow) {
      return 0;
    }
    int affectedRows = flowMapper.updateFlow(flow);
    List<Stops> stopsList = flow.getStopsList();
    if (null != stopsList && stopsList.size() > 0) {
      for (Stops stops : stopsList) {
        if (null == stops) {
          continue;
        }
        stops.setFlow(flow);
        if (StringUtils.isBlank(stops.getId())) {
          affectedRows += addStops(stops);
          continue;
        }
        affectedRows += updateStops(stops);
      }
    }

    List<Paths> pathsList = flow.getPathsList();
    if (null != pathsList && pathsList.size() > 0) {
      for (Paths paths : pathsList) {
        if (null == paths) {
          continue;
        }
        paths.setFlow(flow);
        if (StringUtils.isBlank(paths.getId())) {
          affectedRows += addPaths(paths);
          continue;
        }
        affectedRows += updatePaths(paths);
      }
    }

    MxGraphModel mxGraphModel = flow.getMxGraphModel();
    if (null != mxGraphModel) {
      mxGraphModel.setFlow(flow);
      if (StringUtils.isBlank(mxGraphModel.getId())) {
        affectedRows += mxGraphModelDomain.addMxGraphModel(mxGraphModel);
      } else {
        affectedRows += mxGraphModelDomain.updateMxGraphModel(mxGraphModel);
      }
    }
    String[] globalParamsIdsDB = getGlobalParamsIdsByFlowId(flow.getId());
    String[] globalParamsIds =
        FlowGlobalParamsUtils.globalParamsToIds(flow.getFlowGlobalParamsList());

    affectedRows += unlinkGlobalParams(flow.getId(), globalParamsIdsDB);
    if (null != globalParamsIds && globalParamsIds.length > 0) {
      // List<String> req = Arrays.asList(globalParamsIds);
      // List<String> db = Arrays.asList(globalParamsIdsDB);
      // 1、并集 union
      // Object[] array = CollectionUtils.union(req, db).toArray();
      // 2、交集 intersection
      // Object[] array = CollectionUtils.intersection(req, db).toArray();
      // 3、交集的补集
      // Object[] disjunction = CollectionUtils.disjunction(req, db).toArray();
      // 4、差集（扣除）
      // Object[] subtract = CollectionUtils.subtract(db, req).toArray();
      // String[] unlinkIds = Arrays.copyOf(subtract, subtract.length, String[].class);
      // affectedRows += unlinkGlobalParams(flow.getId(), unlinkIds);
      affectedRows += linkGlobalParams(flow.getId(), globalParamsIds);
    }
    return affectedRows;
  }

  public int updateFlowNoCascading(Flow flow) {
    if (null == flow) {
      return 0;
    }
    return flowMapper.updateFlow(flow);
  }

  public int updatePaths(Paths paths) {
    if (null == paths) {
      return 0;
    }
    return pathsMapper.updatePaths(paths);
  }

  public int addPathsList(List<Paths> pathsList) {
    if (null == pathsList || pathsList.size() == 0) {
      return 0;
    }
    return pathsMapper.addPathsList(pathsList);
  }

  public Flow getFlowById(String id) {
    return flowMapper.getFlowById(id);
  }

  public List<FlowVo> getFlowListParam(String username, boolean isAdmin, String param) {
    return flowMapper.getFlowListParam(username, isAdmin, param);
  }

  public List<String> getFlowNamesByFlowGroupId(String flowGroupId, String flowName) {
    return flowMapper.getFlowNamesByFlowGroupId(flowGroupId, flowName);
  }

  public String getFlowIdByNameAndFlowGroupId(String fid, String flowName) {
    return flowMapper.getFlowIdByNameAndFlowGroupId(fid, flowName);
  }

  public Flow getFlowByPageId(String fid, String pageId) {
    return flowMapper.getFlowByPageId(fid, pageId);
  }

  public String[] getFlowAndGroupNamesByFlowGroupId(String flowGroupId) {
    return flowMapper.getFlowAndGroupNamesByFlowGroupId(flowGroupId);
  }

  public String getFlowIdByPageId(String fid, String pageId) {
    return flowMapper.getFlowIdByPageId(fid, pageId);
  }

  public Integer getMaxFlowPageIdByFlowGroupId(String flowGroupId) {
    return flowMapper.getMaxFlowPageIdByFlowGroupId(flowGroupId);
  }

  public List<Flow> getFlowList() {
    return flowMapper.getFlowList();
  }

  public List<Flow> getFlowExampleList() {
    return flowMapper.getFlowExampleList();
  }

  public Integer getMaxStopPageId(String flowId) {
    return flowMapper.getMaxStopPageId(flowId);
  }

  public Integer deleteFlowInfoById(String username, String flowId) throws Exception {
    Flow flow = flowMapper.getFlowById(flowId);
    if (null == flow) {
      throw new Exception("Data does not exist");
    }
    // remove flow
    Integer affectedRows = 0;
    if (null != flow.getStopsList()) {
      // remove stops
      affectedRows += deleteStopsByFlowId(username, flowId);
      // Loop delete stop attribute
      for (Stops stopId : flow.getStopsList()) {
        if (null == stopId.getProperties()) {
          continue;
        }
        affectedRows += updateStopPropertyEnableFlagByStopId(username, stopId.getId());
      }
    }
    // remove paths
    affectedRows += updatePathsEnableFlagByFlowId(username, flow.getId());
    // remove mxGraph
    affectedRows += mxGraphModelDomain.deleteMxGraphModelByFlowId(username, flow.getId());
    affectedRows += flowMapper.updateEnableFlagById(username, flowId);
    return affectedRows;
  }

  public String getFlowName(String flowName) {
    return flowMapper.getFlowName(flowName);
  }

  public String[] getGlobalParamsIdsByFlowId(String flowId) {
    return flowMapper.getGlobalParamsIdsByFlowId(flowId);
  }

  public int linkGlobalParams(String flowId, String[] globalParamsIds) {
    return flowMapper.linkGlobalParams(flowId, globalParamsIds);
  }

  public int unlinkGlobalParams(String flowId, String[] globalParamsIds) {
    return flowMapper.unlinkGlobalParams(flowId, globalParamsIds);
  }

  public String getFlowNameByPageId(String fid, String pageId) {
    return flowMapper.getFlowNameByPageId(fid, pageId);
  }

  public int updatePathsEnableFlagByFlowId(String username, String flowId) {
    return pathsMapper.updateEnableFlagByFlowId(username, flowId);
  }

  public int addMxGraphModel(MxGraphModel mxGraphModel) throws Exception {
    return mxGraphModelDomain.addMxGraphModel(mxGraphModel);
  }

  public MxGraphModel getMxGraphModelById(String id) {
    return mxGraphModelDomain.getMxGraphModelById(id);
  }

  public List<Paths> getPaths(String flowId, String pageId, String from, String to) {
    return pathsMapper.getPaths(flowId, pageId, from, to);
  }

  public Integer getPathsCounts(String flowId, String pageId, String from, String to) {
    return pathsMapper.getPathsCounts(flowId, pageId, from, to);
  }

  public Paths getPathsById(String id) {
    return pathsMapper.getPathsById(id);
  }

  public int updateMxCell(MxCell mxCell) throws Exception {
    return mxGraphModelDomain.updateMxCell(mxCell);
  }

  public MxCell getMxCellByMxGraphIdAndPageId(String mxGraphId, String pageId) {
    return mxGraphModelDomain.getMxCellByMxGraphIdAndPageId(mxGraphId, pageId);
  }
}
