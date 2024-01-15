package org.apache.streampark.console.flow.component.mxGraph.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxCellVo;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGeometryVo;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;

public class MxGraphModelUtils {

  public static String NODE = "Node";
  public static String PATH = "Path";

  public static MxGraphModel mxGraphModelNewNoId(String username) {
    MxGraphModel mxGraphModel = new MxGraphModel();
    // basic properties (required when creating)
    mxGraphModel.setCrtDttm(new Date());
    mxGraphModel.setCrtUser(username);
    // basic properties
    mxGraphModel.setEnableFlag(true);
    mxGraphModel.setLastUpdateUser(username);
    mxGraphModel.setLastUpdateDttm(new Date());
    mxGraphModel.setVersion(0L);
    return mxGraphModel;
  }

  public static MxGraphModel initMxGraphModelBasicPropertiesNoId(
      MxGraphModel mxGraphModel, String username, boolean isAddId) {
    if (null == mxGraphModel) {
      mxGraphModel = mxGraphModelNewNoId(username);
      if (isAddId) {
        mxGraphModel.setId(UUIDUtils.getUUID32());
      }
      return mxGraphModel;
    }
    // basic properties (required when creating)
    mxGraphModel.setCrtDttm(new Date());
    mxGraphModel.setCrtUser(username);
    // basic properties
    mxGraphModel.setEnableFlag(true);
    mxGraphModel.setLastUpdateUser(username);
    mxGraphModel.setLastUpdateDttm(new Date());
    mxGraphModel.setVersion(0L);
    return mxGraphModel;
  }

  public static MxGraphModel copyMxGraphModelAndNewNoIdAndUnlink(
      String username, MxGraphModel mxGraphModel, boolean isAddId, List<String> disablePageIds) {
    if (null == mxGraphModel) {
      return null;
    }
    MxGraphModel mxGraphModelNew = new MxGraphModel();
    BeanUtils.copyProperties(mxGraphModel, mxGraphModelNew);
    if (isAddId) {
      mxGraphModelNew.setId(UUIDUtils.getUUID32());
    } else {
      mxGraphModelNew.setId(null);
    }
    mxGraphModelNew.setFlow(null);
    mxGraphModelNew.setFlowGroup(null);
    mxGraphModelNew.setProcess(null);
    mxGraphModelNew.setProcessGroup(null);

    List<MxCell> root = mxGraphModel.getRoot();
    mxGraphModelNew.setRoot(null);
    if (null != root && root.size() > 0) {
      List<MxCell> rootNew = new ArrayList<>();
      if (null != disablePageIds) {
        for (MxCell mxCell : root) {
          if (null == mxCell) {
            return null;
          }
          if (disablePageIds.contains(mxCell.getPageId())) {
            continue;
          }
          MxCell mxCellNew = MxCellUtils.copyAndNewMxCell(username, mxCell, isAddId);
          if (null == mxCellNew) {
            continue;
          }
          mxCellNew.setMxGraphModel(mxGraphModelNew);
          rootNew.add(mxCellNew);
        }
      } else {
        for (MxCell mxCell : root) {
          MxCell mxCellNew = MxCellUtils.copyAndNewMxCell(username, mxCell, isAddId);
          if (null == mxCellNew) {
            continue;
          }
          mxCellNew.setMxGraphModel(mxGraphModelNew);
          rootNew.add(mxCellNew);
        }
      }
      mxGraphModelNew.setRoot(rootNew);
    }
    return mxGraphModelNew;
  }

  public static MxGraphModel setMxGraphModelBasicInformation(
      MxGraphModel mxGraphModel, boolean isSetId, String username) {
    if (null == mxGraphModel) {
      mxGraphModel = new MxGraphModel();
    }
    if (isSetId) {
      mxGraphModel.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    mxGraphModel.setCrtDttm(new Date());
    mxGraphModel.setCrtUser(username);
    mxGraphModel.setVersion(0L);
    return updateMxGraphModelBasicInformation(mxGraphModel, username);
  }

  public static MxGraphModel updateMxGraphModelBasicInformation(
      MxGraphModel mxGraphModel, String username) {
    if (null == mxGraphModel) {
      return null;
    }
    mxGraphModel.setLastUpdateDttm(new Date());
    mxGraphModel.setLastUpdateUser(username);
    return mxGraphModel;
  }

  /**
   * mxGraphModel entity to Vo
   *
   * @param mxGraphModel
   * @return
   */
  public static MxGraphModelVo mxGraphModelPoToVo(MxGraphModel mxGraphModel) {
    MxGraphModelVo mxGraphModelVo = null;
    if (null != mxGraphModel) {
      mxGraphModelVo = new MxGraphModelVo();
      BeanUtils.copyProperties(mxGraphModel, mxGraphModelVo);
      List<MxCell> root = mxGraphModel.getRoot();
      mxGraphModelVo.setRootVo(mxCellVoListPoToVo(root));
    }
    return mxGraphModelVo;
  }

  /**
   * mxCellList entity to Vo
   *
   * @param mxCellList
   * @return
   */
  public static List<MxCellVo> mxCellVoListPoToVo(List<MxCell> mxCellList) {
    List<MxCellVo> mxCellVoList = null;
    if (null != mxCellList && mxCellList.size() > 0) {
      mxCellVoList = new ArrayList<MxCellVo>();
      for (MxCell mxCell : mxCellList) {
        MxCellVo mxCellVo = mxCellPoToVo(mxCell);
        if (null != mxCell) {
          mxCellVoList.add(mxCellVo);
        }
      }
    }
    return mxCellVoList;
  }

  /**
   * mxCell entity to Vo
   *
   * @param mxCell
   * @return
   */
  public static MxCellVo mxCellPoToVo(MxCell mxCell) {
    MxCellVo mxCellVo = null;
    if (null != mxCell) {
      mxCellVo = new MxCellVo();
      BeanUtils.copyProperties(mxCell, mxCellVo);
      MxGeometry mxGeometry = mxCell.getMxGeometry();
      if (null != mxGeometry) {
        MxGeometryVo mxGeometryVo = new MxGeometryVo();
        BeanUtils.copyProperties(mxGeometry, mxGeometryVo);
        mxCellVo.setMxGeometryVo(mxGeometryVo);
      }
    }
    return mxCellVo;
  }

  /**
   * Distinguish between 'element' and 'path'
   *
   * @param root
   * @return Returns a list of Mxcell types with elements and paths in the map (keys: paths and
   *     elements)
   */
  public static Map<String, List<MxCellVo>> mxCellVoDistinguishNodesAndPaths(List<MxCellVo> root) {
    Map<String, List<MxCellVo>> map = new HashMap<>();
    if (null != root && root.size() > 0) {
      List<MxCellVo> pathsList = new ArrayList<>();
      List<MxCellVo> nodesList = new ArrayList<>();
      // Loop root
      for (MxCellVo mxCellVo : root) {
        String nodeOrPath = isNodeOrPath(mxCellVo);
        if (NODE.equals(nodeOrPath)) {
          mxCellVo.setParamData(MxCellUtils.mxCellStyleToParamData(mxCellVo.getStyle()));
          nodesList.add(mxCellVo);
        } else if (PATH.equals(nodeOrPath)) {
          pathsList.add(mxCellVo);
        }
      }

      map.put("nodes", nodesList);
      map.put("paths", pathsList);
    }
    return map;
  }

  /**
   * Distinguish between 'element' and 'path'
   *
   * @param root
   * @return Returns a list of Mxcell types with elements and paths in the map (keys: paths and
   *     elements)
   */
  public static Map<String, List<MxCell>> mxCellDistinguishNodesAndPaths(List<MxCell> root) {
    Map<String, List<MxCell>> map = new HashMap<>();
    if (null != root && root.size() > 0) {
      List<MxCell> pathsList = new ArrayList<>();
      List<MxCell> nodesList = new ArrayList<>();
      // Loop root
      for (MxCell mxCell : root) {
        String nodeOrPath = isNodeOrPath(mxCell);
        if (NODE.equals(nodeOrPath)) {
          nodesList.add(mxCell);
        } else if (PATH.equals(nodeOrPath)) {
          pathsList.add(mxCell);
        }
      }
      map.put("nodes", nodesList);
      map.put("paths", pathsList);
    }
    return map;
  }

  /**
   * Judgment object type
   *
   * @param object
   * @return
   */
  public static String isNodeOrPath(Object object) {
    if (null == object) {
      return null;
    }
    String name = object.getClass().getName();
    MxCell mxCell = null;
    if ("org.apache.streampark.console.flow.component.mxGraph.vo.MxCellVo".equals(name)) {
      MxCellVo mxCellVo = (MxCellVo) object;
      mxCell = new MxCell();
      BeanUtils.copyProperties(mxCellVo, mxCell);
    } else if ("org.apache.streampark.console.flow.component.mxGraph.entity.MxCell".equals(name)) {
      mxCell = (MxCell) object;
    } else {
      return null;
    }

    // Take out the style attribute
    String style = mxCell.getStyle();
    // Judge whether it is empty
    if (StringUtils.isBlank(style)) {
      return null;
    }
    if (style.indexOf("image;") == 0) {
      return NODE;
    } else if ("1".equals(mxCell.getEdge()) || "true".equals(mxCell.getEdge())) {
      return PATH;
    }
    return null;
  }

  /**
   * Distinguish between stop and path
   *
   * @param root
   * @return Returns a list of MxCell types with stops and paths in the map (keys: paths and stops)
   */
  public static Map<String, Object> mxCellDistinguishStopsPaths(List<MxCell> root) {
    Map<String, Object> map = null;
    if (null != root && root.size() > 0) {
      map = new HashMap<String, Object>();
      List<MxCell> pathsList = new ArrayList<MxCell>();
      List<MxCell> stopsList = new ArrayList<MxCell>();
      // Loop root
      for (MxCell mxCell : root) {
        if (null != mxCell) {
          // Take out the line-specific attributes to determine if it is empty.
          String edge = mxCell.getEdge();
          if (StringUtils.isNotBlank(edge)) {
            pathsList.add(mxCell);
          } else {
            stopsList.add(mxCell);
          }
        }
      }
      map.put("stops", stopsList);
      map.put("paths", pathsList);
    }
    return map;
  }

  /**
   * Generate a list of paths based on the contents of the MxCellList
   *
   * @param objectPaths
   * @param flow
   * @return
   */
  public static List<Paths> mxCellVoListToPathsList(
      String username, List<MxCellVo> objectPaths, Flow flow) {
    if (null == objectPaths || objectPaths.size() <= 0) {
      return null;
    }
    List<Paths> pathsList = new ArrayList<>();
    // Loop objectPaths
    for (MxCellVo mxCellVo : objectPaths) {
      Paths paths = mxCellToPaths(username, mxCellVo);
      if (null == paths) {
        continue;
      }
      paths.setFlow(flow);
      pathsList.add(paths);
    }
    return pathsList;
  }

  /**
   * Generate a list of paths based on the contents of the MxCellList
   *
   * @param objectPaths
   * @param flow
   * @return
   */
  public static List<Paths> mxCellListToPathsList(
      String username, List<MxCell> objectPaths, Flow flow) {
    if (null == objectPaths || objectPaths.size() <= 0) {
      return null;
    }
    List<Paths> pathsList = new ArrayList<>();
    // Loop objectPaths
    for (MxCell mxCell : objectPaths) {
      Paths paths = mxCellToPaths(username, mxCell, false);
      if (null == paths) {
        continue;
      }
      paths.setFlow(flow);
      pathsList.add(paths);
    }
    return pathsList;
  }

  /**
   * Generate a list of paths based on the contents of the MxCellList
   *
   * @param objectPaths
   * @param flowGroup
   * @return
   */
  public static List<FlowGroupPaths> mxCellVoListToFlowGroupPathsList(
      String username, List<MxCellVo> objectPaths, FlowGroup flowGroup) {
    List<FlowGroupPaths> flowGroupPathsList = null;
    if (null != objectPaths && objectPaths.size() > 0) {
      flowGroupPathsList = new ArrayList<>();
      // Loop objectPaths
      for (MxCellVo mxCellVo : objectPaths) {
        FlowGroupPaths flowGroupPaths = mxCellToFlowGroupPaths(username, mxCellVo);
        if (null != flowGroupPaths) {
          flowGroupPaths.setFlowGroup(flowGroup);
          flowGroupPathsList.add(flowGroupPaths);
        }
      }
    }
    return flowGroupPathsList;
  }

  /**
   * mxCellVo to Paths
   *
   * @param mxCellVo
   * @return
   */
  public static Paths mxCellToPaths(String username, MxCellVo mxCellVo) {
    Paths paths = null;
    if (null != mxCellVo) {
      paths = new Paths();
      paths.setId(UUIDUtils.getUUID32());
      paths.setCrtDttm(new Date());
      paths.setCrtUser(username);
      paths.setLastUpdateDttm(new Date());
      paths.setLastUpdateUser(username);
      paths.setEnableFlag(true);
      paths.setFrom(mxCellVo.getSource());
      paths.setTo(mxCellVo.getTarget());
      paths.setPageId(mxCellVo.getPageId());
    }
    return paths;
  }

  /**
   * mxCellVo to Paths
   *
   * @param username
   * @param mxCell
   * @param isAddId Add ID or not
   * @return
   */
  public static Paths mxCellToPaths(String username, MxCell mxCell, boolean isAddId) {
    if (null == mxCell) {
      return null;
    }
    Paths paths = new Paths();
    if (isAddId) {
      paths.setId(UUIDUtils.getUUID32());
    } else {
      paths.setId(null);
    }
    paths.setCrtDttm(new Date());
    paths.setCrtUser(username);
    paths.setLastUpdateDttm(new Date());
    paths.setLastUpdateUser(username);
    paths.setEnableFlag(true);
    paths.setFrom(mxCell.getSource());
    paths.setTo(mxCell.getTarget());
    paths.setPageId(mxCell.getPageId());
    return paths;
  }

  /**
   * mxCellVo to Paths
   *
   * @param mxCellVo
   * @return
   */
  public static FlowGroupPaths mxCellToFlowGroupPaths(String username, MxCellVo mxCellVo) {
    FlowGroupPaths flowGroupPaths = null;
    if (null != mxCellVo) {
      flowGroupPaths = new FlowGroupPaths();
      flowGroupPaths.setCrtDttm(new Date());
      flowGroupPaths.setCrtUser(username);
      flowGroupPaths.setLastUpdateDttm(new Date());
      flowGroupPaths.setLastUpdateUser(username);
      flowGroupPaths.setEnableFlag(true);
      flowGroupPaths.setFrom(mxCellVo.getSource());
      flowGroupPaths.setTo(mxCellVo.getTarget());
      flowGroupPaths.setPageId(mxCellVo.getPageId());
    }
    return flowGroupPaths;
  }

  /**
   * Generate a list of flows based on the contents of MxCellVoList
   *
   * @param mxCellVoList
   * @param flowGroup
   * @param username
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Map<String, List> mxCellVoListToFlowAndFlowGroups(
      List<MxCellVo> mxCellVoList, FlowGroup flowGroup, String username) {
    Map<String, List> rtnMapData = new HashMap<>();
    List<Flow> flowList = new ArrayList<>();
    List<FlowGroup> flowGroupList = new ArrayList<>();
    if (null == mxCellVoList || mxCellVoList.size() <= 0) {
      rtnMapData.put("flows", flowList);
      rtnMapData.put("flowGroups", flowGroupList);
      return rtnMapData;
    }
    // Loop mxCellVoList
    for (MxCellVo mxCellVo : mxCellVoList) {
      if (null == mxCellVo) {
        continue;
      }
      // image;html=1;labelBackgroundColor=#ffffff00;image=/img/group.png
      String mxCellVoStyle = mxCellVo.getStyle();
      // Judge whether it is empty
      if (StringUtils.isBlank(mxCellVoStyle)) {
        continue;
      }
      if (mxCellVoStyle.indexOf("image;") != 0) {
        continue;
      }
      Map<String, String> paramData = mxCellVo.getParamData();
      String nodeType = paramData.get("nodeType");
      if ("Group".equals(nodeType)) {
        FlowGroup flowGroupNew = mxCellVoToGroup(mxCellVo, flowGroup, username);
        if (null != flowGroupNew) {
          flowGroupList.add(flowGroupNew);
        }
      } else if ("Flow".equals(nodeType)) {
        Flow flowNew = mxCellVoToFlow(mxCellVo, flowGroup, username);
        if (null != flowNew) {
          flowList.add(flowNew);
        }
      }
    }
    rtnMapData.put("flows", flowList);
    rtnMapData.put("flowGroups", flowGroupList);
    return rtnMapData;
  }

  /**
   * Content generation Flow based on mxCellVo
   *
   * @param mxCellVo
   * @param parentsFlowGroup
   * @param username
   * @return
   */
  public static Flow mxCellVoToFlow(
      MxCellVo mxCellVo, FlowGroup parentsFlowGroup, String username) {
    Flow flow = null;
    if (null != mxCellVo) {
      flow = new Flow();
      flow.setCrtDttm(new Date());
      flow.setCrtUser(username);
      flow.setLastUpdateDttm(new Date());
      flow.setLastUpdateUser(username);
      flow.setEnableFlag(true);
      flow.setPageId(mxCellVo.getPageId());
      flow.setName("flow" + mxCellVo.getPageId());
      MxGraphModel mxGraphModel = new MxGraphModel();
      mxGraphModel.setFlow(flow);
      mxGraphModel.setCrtDttm(new Date());
      mxGraphModel.setCrtUser(username);
      mxGraphModel.setLastUpdateDttm(new Date());
      mxGraphModel.setLastUpdateUser(username);
      mxGraphModel.setEnableFlag(true);
      flow.setMxGraphModel(mxGraphModel);
      flow.setFlowGroup(parentsFlowGroup);
    }
    return flow;
  }

  /**
   * Content generation FlowGroup based on mxCellVo
   *
   * @param mxCellVo
   * @param parentsFlowGroup
   * @param username
   * @return
   */
  public static FlowGroup mxCellVoToGroup(
      MxCellVo mxCellVo, FlowGroup parentsFlowGroup, String username) {
    FlowGroup flowGroup = null;
    if (null != mxCellVo) {
      flowGroup = new FlowGroup();
      flowGroup.setCrtDttm(new Date());
      flowGroup.setCrtUser(username);
      flowGroup.setLastUpdateDttm(new Date());
      flowGroup.setLastUpdateUser(username);
      flowGroup.setEnableFlag(true);
      flowGroup.setPageId(mxCellVo.getPageId());
      flowGroup.setName("group" + mxCellVo.getPageId());
      MxGraphModel mxGraphModel = new MxGraphModel();
      mxGraphModel.setFlowGroup(flowGroup);
      mxGraphModel.setCrtDttm(new Date());
      mxGraphModel.setCrtUser(username);
      mxGraphModel.setLastUpdateDttm(new Date());
      mxGraphModel.setLastUpdateUser(username);
      mxGraphModel.setEnableFlag(true);
      flowGroup.setMxGraphModel(mxGraphModel);
      flowGroup.setFlowGroup(parentsFlowGroup);
    }
    return flowGroup;
  }
}
