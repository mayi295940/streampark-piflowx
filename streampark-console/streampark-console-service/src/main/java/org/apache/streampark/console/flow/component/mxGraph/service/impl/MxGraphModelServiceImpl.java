package org.apache.streampark.console.flow.component.mxGraph.service.impl;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.dataSource.domain.DataSourceDomain;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSourceProperty;
import org.apache.streampark.console.flow.component.flow.domain.FlowDomain;
import org.apache.streampark.console.flow.component.flow.domain.FlowGroupDomain;
import org.apache.streampark.console.flow.component.flow.domain.FlowStopsPublishingDomain;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.utils.FlowXmlUtils;
import org.apache.streampark.console.flow.component.flow.utils.PropertyUtils;
import org.apache.streampark.console.flow.component.flow.utils.StopsUtils;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxGraphModelDomain;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxGraphModelService;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxCellUtils;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxGraphModelUtils;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxGraphUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxCellVo;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGeometryVo;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphVo;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MxGraphModelServiceImpl implements IMxGraphModelService {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private final Logger logger = LoggerUtil.getLogger();

  private final StopsComponentDomain stopsComponentDomain;
  private final MxGraphModelDomain mxGraphModelDomain;
  private final FlowGroupDomain flowGroupDomain;
  private final FlowDomain flowDomain;
  private final DataSourceDomain dataSourceDomain;
  private final FlowStopsPublishingDomain flowStopsPublishingDomain;

  @Autowired
  public MxGraphModelServiceImpl(
      StopsComponentDomain stopsComponentDomain,
      MxGraphModelDomain mxGraphModelDomain,
      FlowGroupDomain flowGroupDomain,
      FlowDomain flowDomain,
      DataSourceDomain dataSourceDomain,
      FlowStopsPublishingDomain flowStopsPublishingDomain) {
    this.stopsComponentDomain = stopsComponentDomain;
    this.mxGraphModelDomain = mxGraphModelDomain;
    this.flowGroupDomain = flowGroupDomain;
    this.flowDomain = flowDomain;
    this.dataSourceDomain = dataSourceDomain;
    this.flowStopsPublishingDomain = flowStopsPublishingDomain;
  }

  @Override
  public String saveDataForTask(String username, String imageXML, String loadId, String operType)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isAnyEmpty(imageXML, loadId, operType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    // Change the `XML' from the page to `mxGraphModel'
    MxGraphModelVo xmlToMxGraphModel = FlowXmlUtils.xmlToMxGraphModel(imageXML);
    // xmlToMxGraphModel Parameter null
    if (null == xmlToMxGraphModel) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The passed parameter xmlToMxGraphModel is empty and the save failed.");
    }

    if ("ADD".equals(operType)) {
      logger.info("ADD Operation begins");
      return addOperation(username, loadId, xmlToMxGraphModel);
    } else if ("MOVED".equals(operType)) {
      logger.info("MOVED Operation begins");
      return movedOperation(username, xmlToMxGraphModel, loadId);
    } else if ("REMOVED".equals(operType)) {
      logger.info("REMOVED Operation begins");
      return removedOperation(username, xmlToMxGraphModel, loadId);
    } else {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Can't find operType:" + operType + " type ");
    }
    // return ReturnMapUtils.setSucceededMsgRtnJsonStr("Successful Preservation");
  }

  /**
   * Add stops and drawing board mxCell
   *
   * @param username user name
   * @param flowId flow id
   * @param mxGraphModelVo mxGraphModelVo
   * @return json
   */
  private String addOperation(String username, String flowId, MxGraphModelVo mxGraphModelVo)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    // Query flow by flowId
    Flow flowDB = flowDomain.getFlowById(flowId);
    if (null == flowDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "Flow information with flowId: " + flowId + " is not queried");
    }
    // update flow
    flowDB.setLastUpdateDttm(new Date());
    flowDB.setLastUpdateUser(username);
    flowDB.setEnableFlag(true);
    // Take out the drawing board of the data inventory
    MxGraphModel mxGraphModelDB = flowDB.getMxGraphModel();
    // Determine if the drawing board of the data inventory exists
    if (null == mxGraphModelDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Database without drawing board, adding failed");
    }
    // Put the page's drawing board information into the database canvas
    // Copy the value from 'mxGraphModelVo' to 'mxGraphModelDB'
    BeanUtils.copyProperties(mxGraphModelVo, mxGraphModelDB);
    mxGraphModelDB.setEnableFlag(true);
    mxGraphModelDB.setLastUpdateUser(username);
    mxGraphModelDB.setLastUpdateDttm(new Date());
    mxGraphModelDB.setFlow(flowDB);

    // Loop database data
    List<MxCell> mxCellRootDB = mxGraphModelDB.getRoot();
    // Map of the data sent from the page
    Map<String, MxCell> mxCellDBMap = new HashMap<>();
    for (MxCell mxCellDB : mxCellRootDB) {
      if (null == mxCellDB || StringUtils.isBlank(mxCellDB.getPageId())) {
        continue;
      }
      mxCellDBMap.put(mxCellDB.getPageId(), mxCellDB);
    }
    // add New MxCell List
    List<MxCell> addMxCellList = new ArrayList<>();
    // add New Stops List
    List<Stops> addStopsList = new ArrayList<>();
    // add New Paths List
    List<Paths> addPathsList = new ArrayList<>();

    // The data passed from the page mxCellVoList
    List<MxCellVo> mxCellVoList = mxGraphModelVo.getRootVo();

    // Loop mxCellVoList
    for (MxCellVo mxCellVo : mxCellVoList) {
      if (null == mxCellVo) {
        continue;
      }
      // Use pageId to go to map
      MxCell mxCellDB = mxCellDBMap.get(mxCellVo.getPageId());
      // If you can get the value in "mxCellDBMap", it means it already exists, otherwise it doesn't
      // exist
      if (null != mxCellDB) {
        continue;
      }
      // Convert to objects that can be stored in the database
      MxCell mxCell = MxCellUtils.mxCellVoToNewMxCell(username, mxCellVo, false);
      // Judge whether the conversion is successful
      if (null == mxCell) {
        continue;
      }
      String stopByNameAndFlowId = flowDomain.getStopByNameAndFlowId(flowId, mxCell.getValue());
      long currentTimeMillis = System.currentTimeMillis();
      if (StringUtils.isNotBlank(stopByNameAndFlowId)) {
        mxCell.setValue(mxCell.getValue() + "-" + currentTimeMillis);
      }
      // mxGraphModel foreign key
      mxCell.setMxGraphModel(mxGraphModelDB);
      // Judge whether "mxCell" is of type "stops" or "path"
      String nodeOrPath = MxGraphModelUtils.isNodeOrPath(mxCell);
      if (MxGraphModelUtils.NODE.equals(nodeOrPath)) {
        Map<String, String> paramData = mxCellVo.getParamData();
        if (null == paramData || paramData.size() == 0) {
          return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("nodeType"));
        }
        // mxCell to stops
        Stops stops;
        String nodeType = paramData.get("nodeType");
        switch (nodeType) {
          case "Stop":
            {
              stops = this.stopsTemplateToStops(mxCell, paramData, username, false);
              break;
            }
          case "DataSource":
            {
              stops = this.dataSourceToStops(mxCell, paramData, username, false);
              break;
            }
          default:
            {
              stops = null;
              break;
            }
        }
        if (null == stops) {
          return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        }
        if (StringUtils.isNotBlank(stopByNameAndFlowId)) {
          stops.setName(stops.getName() + "-" + currentTimeMillis);
        }
        stops.setFlow(flowDB);
        addStopsList.add(stops);
      } else if (MxGraphModelUtils.PATH.equals(nodeOrPath)) {
        // mxCell to stops
        Paths paths = MxGraphModelUtils.mxCellToPaths(username, mxCell, false);
        if (null == paths) {
          return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.CONVERSION_FAILED_MSG());
        }
        paths.setFlow(flowDB);
        addPathsList.add(paths);
      }
      addMxCellList.add(mxCell);
    }
    // Judge whether there is a new "MxCell"
    if (CollectionUtils.isEmpty(addMxCellList)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("No data can be added, the addition failed");
    }
    // add
    mxCellRootDB.addAll(addMxCellList);
    mxGraphModelDB.setRoot(mxCellRootDB);
    flowDB.setMxGraphModel(mxGraphModelDB);
    // Judge whether there is a new "Stops"
    if (CollectionUtils.isNotEmpty(addStopsList)) {
      List<Stops> stopsList = flowDB.getStopsList();
      stopsList.addAll(addStopsList);
      flowDB.setStopsList(stopsList);
    }
    // Judge whether there is a new "Paths"
    if (null != addStopsList && addPathsList.size() > 0) {
      List<Paths> pathsList = flowDB.getPathsList();
      pathsList.addAll(addPathsList);
      flowDB.setPathsList(pathsList);
    }

    // update mxGraphModel
    int updateFlow = flowDomain.updateFlow(flowDB);
    // Determine if mxGraphModelDb is updated successfully
    if (updateFlow <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPDATE_ERROR_MSG());
    }
    MxGraphModel mxGraphModelByFlowId = mxGraphModelDomain.getMxGraphModelByFlowId(flowId);
    String xmlData = MxGraphUtils.mxGraphModelToMxGraph(false, mxGraphModelByFlowId);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("xmlData", xmlData);
  }

  /** Modification of the drawing board */
  private String movedOperation(String username, MxGraphModelVo mxGraphModelVo, String flowId)
      throws Exception {
    // If "mxGraphModel" and "mxCellList" are empty, the modification will fail,
    // because this method only deals with the modification, not the addition
    if (StringUtils.isBlank(username)) {
      logger.warn("illegal user");
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    MxGraphModel mxGraphModelDB = mxGraphModelDomain.getMxGraphModelByFlowId(flowId);
    // Determine if the incoming data is empty
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("mxGraphModelVo is null, fail to edit");
    }
    // Determine if the database data to be modified is empty
    if (null == mxGraphModelDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The mxGraphModel information with flowId is: " + flowId + " is not found, fail to edit");
    }

    // Copy the value from mxGraphModelVo to mxGraphModelDb
    BeanUtils.copyProperties(mxGraphModelVo, mxGraphModelDB);
    // set mxGraphModel basic attribute
    mxGraphModelDB.setLastUpdateUser(username); // last update user
    mxGraphModelDB.setLastUpdateDttm(new Date()); // last update time
    mxGraphModelDB.setEnableFlag(true); // is it effective

    // Take out the MxCellList information queried by the database.
    List<MxCell> mxCellListDB = mxGraphModelDB.getRoot();
    if (null == mxCellListDB || mxCellListDB.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The database mxCellList is empty and the modification failed.");
    }
    // The data passed from the page MxCellVo
    List<MxCellVo> mxCellVoList = mxGraphModelVo.getRootVo();
    // Convert the list passed to the page to map key for pageId
    Map<String, MxCellVo> mxCellVoMap = new HashMap<>();
    // Determine if it is empty
    if (null != mxCellVoList) {
      for (MxCellVo mxCell : mxCellVoList) {
        mxCellVoMap.put(mxCell.getPageId(), mxCell);
      }
    }
    // Including modified and tombstones
    List<MxCell> updateMxCellList = new ArrayList<>();
    // Loop page data for classification (requires modification and tombstone)
    for (MxCell mxCellDB : mxCellListDB) {
      if (null == mxCellDB) {
        continue;
      }

      // Graphic ID (pageId) on the drawing board
      String pageId = mxCellDB.getPageId();
      // According to the pageId to go to map,
      // Get the description database has a page, do the modification operation,
      // Otherwise, the database has no pages, and the logical deletion is performed.
      MxCellVo mxCellVo = mxCellVoMap.get(pageId);
      if (null == mxCellVo) {
        // Logical deletion
        mxCellDB.setEnableFlag(false);
        mxCellDB.setLastUpdateDttm(new Date());
        mxCellDB.setLastUpdateUser(username);
        continue;
      }

      // Copy the value in mxCellVo to mxCell
      BeanUtils.copyProperties(mxCellVo, mxCellDB);
      // mxCell basic properties
      mxCellDB.setEnableFlag(true);
      mxCellDB.setLastUpdateUser(username);
      mxCellDB.setLastUpdateDttm(new Date());

      // Do not handle foreign keys when modifying, unless you cancel or modify the
      // foreign key
      // mxGraphModel foreign key
      // mxCell.setMxGraphModel(mxGraphModel);
      MxGeometryVo mxGeometryVo = mxCellVo.getMxGeometryVo();
      MxGeometry mxGeometryDB = mxCellDB.getMxGeometry();
      if (null != mxGeometryDB && null != mxGeometryVo) {
        // Copy the value from mxGeometryVo into mxGeometry
        BeanUtils.copyProperties(mxGeometryVo, mxGeometryDB);

        // set mxGraphModel basic properties
        mxGeometryDB.setLastUpdateUser(username); // last update user
        mxGeometryDB.setLastUpdateDttm(new Date()); // last update time
        mxGeometryDB.setEnableFlag(true); // Tombstone ID
        mxGeometryDB.setMxCell(mxCellDB);
        mxCellDB.setMxGeometry(mxGeometryDB);
      }
      mxCellDB.setMxGraphModel(mxGraphModelDB);
      // Fill in the modified list
      updateMxCellList.add(mxCellDB);
    }

    mxGraphModelDB.setRoot(updateMxCellList);

    // save MxGraphModel
    int updateMxGraphModel = mxGraphModelDomain.updateMxGraphModel(mxGraphModelDB);
    // Determine if mxGraphModel is saved successfully
    if (updateMxGraphModel <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
  }

  /**
   * mxCell to stops
   *
   * @param mxCell mxCell
   * @param username username
   * @param isAddId Add ID or not
   */
  private Stops stopsTemplateToStops(
      MxCell mxCell, Map<String, String> paramData, String username, boolean isAddId) {
    if (null == mxCell) {
      return null;
    }
    if (null == paramData || paramData.size() == 0) {
      return null;
    }
    // Get the bundle of the stops
    String bundle = paramData.get("bundle");
    if (null == bundle) {
      return null;
    }
    StopsComponent stopsComponent = stopsComponentDomain.getStopsComponentByBundle(bundle);
    // Whether to judge whether the template is empty
    if (null == stopsComponent) {
      return null;
    }
    Stops stops = new Stops();
    BeanUtils.copyProperties(stopsComponent, stops);
    StopsUtils.initStopsBasicPropertiesNoId(stops, username);
    if (isAddId) {
      stops.setId(UUIDUtils.getUUID32());
    } else {
      stops.setId(null);
    }
    stops.setPageId(mxCell.getPageId());
    stops.setIsDataSource(false);
    List<Property> propertiesList = null;
    List<StopsComponentProperty> propertiesTemplateList = stopsComponent.getProperties();
    if (null != propertiesTemplateList && propertiesTemplateList.size() > 0) {
      propertiesList = new ArrayList<>();
      for (StopsComponentProperty stopsComponentProperty : propertiesTemplateList) {
        Property property = PropertyUtils.propertyNewNoId(username);
        BeanUtils.copyProperties(stopsComponentProperty, property);
        if (isAddId) {
          property.setId(UUIDUtils.getUUID32());
        } else {
          property.setId(null);
        }
        property.setStops(stops);
        property.setCustomValue(stopsComponentProperty.getDefaultValue());
        // Indicates "select"
        if (stopsComponentProperty.getAllowableValues() != null
            && stopsComponentProperty.getAllowableValues().contains(",")
            && stopsComponentProperty.getAllowableValues().length() > 4) {
          property.setIsSelect(true);
          // Determine if there is a default value in "select"
          if (!stopsComponentProperty
              .getAllowableValues()
              .contains(stopsComponentProperty.getDefaultValue())) {
            // Default value if not present
            property.setCustomValue("");
          }
        } else {
          property.setIsSelect(false);
        }
        propertiesList.add(property);
      }
    }
    stops.setProperties(propertiesList);
    return stops;
  }

  /**
   * mxCell to datasource stops
   *
   * @param mxCell mxCell
   * @param username username
   * @param isAddId Add ID or not
   */
  private Stops dataSourceToStops(
      MxCell mxCell, Map<String, String> paramData, String username, boolean isAddId) {
    if (null == mxCell) {
      return null;
    }
    if (null == paramData || paramData.size() == 0) {
      return null;
    }
    String dataSourceId = paramData.get("id");
    if (StringUtils.isBlank(dataSourceId)) {
      return null;
    }
    DataSource dataSource = dataSourceDomain.getDataSourceById(username, true, dataSourceId);
    if (null == dataSource) {
      return null;
    }
    StopsComponent stopsComponent = dataSource.getStopsComponent();
    // Whether to judge whether the template is empty
    if (null == stopsComponent) {
      return null;
    }
    Stops stops = new Stops();
    BeanUtils.copyProperties(stopsComponent, stops);
    StopsUtils.initStopsBasicPropertiesNoId(stops, username);
    stops.setName(mxCell.getValue());
    if (isAddId) {
      stops.setId(UUIDUtils.getUUID32());
    } else {
      stops.setId(null);
    }
    stops.setPageId(mxCell.getPageId());
    stops.setIsDataSource(true);
    stops.setDataSource(dataSource);
    List<StopsComponentProperty> propertiesTemplateList = stopsComponent.getProperties();
    if (null == propertiesTemplateList || propertiesTemplateList.size() == 0) {
      return stops;
    }
    List<DataSourceProperty> dataSourcePropertyList = dataSource.getDataSourcePropertyList();
    Map<String, String> dataSourcePropertyMap = new HashMap<>();
    if (null != dataSourcePropertyList) {
      dataSourcePropertyList.forEach(a -> dataSourcePropertyMap.put(a.getName(), a.getValue()));
    }
    List<Property> propertiesList = new ArrayList<>();
    for (StopsComponentProperty stopsComponentProperty : propertiesTemplateList) {
      Property property = PropertyUtils.propertyNewNoId(username);
      property.setIsLocked(true); // Assign true to prevent the flow page from changing
      BeanUtils.copyProperties(stopsComponentProperty, property);
      if (isAddId) {
        property.setId(UUIDUtils.getUUID32());
      } else {
        property.setId(null);
      }
      property.setStops(stops);
      property.setCustomValue(dataSourcePropertyMap.get(property.getName()));
      String allowableValues = stopsComponentProperty.getAllowableValues();
      // Indicates "select"
      if (allowableValues.contains(",") && allowableValues.length() > 4) {
        // Determine if there is a default value in "select"
        if (!allowableValues.contains(stopsComponentProperty.getDefaultValue())) {
          // Default value if not present
          property.setCustomValue("");
        }
        property.setIsSelect(true);
      } else {
        property.setIsSelect(false);
      }
      propertiesList.add(property);
    }
    stops.setProperties(propertiesList);
    return stops;
  }

  /**
   * Modify Flow
   *
   * @param mxGraphModelVo Information from the page
   * @param flowId The data to be modified
   */
  private String removedOperation(String username, MxGraphModelVo mxGraphModelVo, String flowId)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      logger.warn("illegal user");
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("mxGraphModelVo is empty, modification failed");
    }
    Flow flowDB = flowDomain.getFlowById(flowId);
    if (null == flowDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The flowId cannot find the corresponding flow, and the modification fails.");
    }
    // last update time
    flowDB.setLastUpdateDttm(new Date());
    // last update user
    flowDB.setLastUpdateUser(username);
    // Take out the drawing board of the data inventory
    MxGraphModel mxGraphModelDB = flowDB.getMxGraphModel();
    // Determine if the drawing board of the data inventory exists
    if (null == mxGraphModelDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Database without drawing board, adding failed");
    }
    // Copy the value from mxGraphModelVo to mxGraphModelDb
    BeanUtils.copyProperties(mxGraphModelVo, mxGraphModelDB);
    // set mxGraphModel basic attribute
    mxGraphModelDB.setLastUpdateUser(username);
    mxGraphModelDB.setLastUpdateDttm(new Date());
    mxGraphModelDB.setEnableFlag(true);
    mxGraphModelDB.setFlow(flowDB);
    // Take out the MxCellList information queried by the database.
    List<MxCell> mxCellListDB = mxGraphModelDB.getRoot();
    if (null == mxCellListDB || mxCellListDB.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The database mxCellList is empty and the modification failed.");
    }
    // The data passed from the page MxCellVo
    List<MxCellVo> mxCellVoList = mxGraphModelVo.getRootVo();
    // Convert the list passed to the page to map key for pageId
    Map<String, MxCellVo> mxCellVoMap = new HashMap<>();
    // Determine if it is empty
    if (null != mxCellVoList) {
      for (MxCellVo mxCell : mxCellVoList) {
        mxCellVoMap.put(mxCell.getPageId(), mxCell);
      }
    }
    // update MxCell list
    List<MxCell> updateMxCellList = new ArrayList<>();
    // Loop page data for classification (requires modification and tombstone)
    for (MxCell mxCellDB : mxCellListDB) {
      if (null == mxCellDB) {
        continue;
      }
      // Graphic ID (pageId) on the drawing board
      String pageId = mxCellDB.getPageId();
      // Use "pageId" as the key to get the value in "mxCellVoMap".
      // If it can't get the value, delete it logically.
      MxCellVo mxCellVo = mxCellVoMap.get(pageId);
      if (null == mxCellVo) {
        // Logical deletion
        mxCellDB.setEnableFlag(false);
        mxCellDB.setLastUpdateDttm(new Date());
        mxCellDB.setLastUpdateUser(username);
        mxCellDB.setMxGraphModel(mxGraphModelDB);
      }
      // Fill in the modified list
      updateMxCellList.add(mxCellDB);
    }
    mxGraphModelDB.setRoot(updateMxCellList);

    // Need to delete the path
    // Key is from and to (that is, the pageId of stop) value is inport and outport
    Map<String, String> pathsDelInfoMap = new HashMap<>();

    // Get out the PathsList stored in the database
    List<Paths> pathsListDB = flowDB.getPathsList();
    if (null != pathsListDB && pathsListDB.size() > 0) {
      // The pathsList to be modified
      List<Paths> updatePaths = new ArrayList<>();
      // The data pathsList of the loop database is retrieved by using the pageId in
      // the stops to convert the map to the value of the page passed by the map.
      for (Paths pathsDB : pathsListDB) {
        if (null == pathsDB) {
          continue;
        }
        String pageId = pathsDB.getPageId();
        MxCellVo mxCellVo = mxCellVoMap.get(pageId);
        // Whether the value can be obtained in "mxCellVoMap" according to "pageId". If not, delete
        // it
        if (null == mxCellVo) {
          pathsDB.setEnableFlag(false);
          pathsDB.setLastUpdateUser(username);
          pathsDB.setLastUpdateDttm(new Date());
          pathsDB.setFlow(flowDB);
          // Put the port information that needs to be logically deleted into the map of
          // pathsDelInfoMap
          String in_string = pathsDelInfoMap.get("in" + pathsDB.getTo());
          if (StringUtils.isNotBlank(in_string)) {
            in_string += ",";
          } else {
            in_string = "";
          }
          in_string += pathsDB.getInport();
          String out_string = pathsDelInfoMap.get("out" + pathsDB.getFrom());
          if (StringUtils.isNotBlank(out_string)) {
            out_string += ",";
          } else {
            out_string = "";
          }
          out_string += pathsDB.getInport();
          pathsDelInfoMap.put("in" + pathsDB.getTo(), in_string);
          pathsDelInfoMap.put("out" + pathsDB.getFrom(), out_string);
        }
        updatePaths.add(pathsDB);
      }
      flowDB.setPathsList(updatePaths);
    } else {
      // The stopsList in the database is empty and the modification failed.
      logger.info("The pathsList in the database is empty");
    }

    // Get out the Stopslist stored in the database
    List<Stops> stopsListDB = flowDB.getStopsList();

    //
    List<String> removeStopsId = new ArrayList<>();
    // continue the judgment operation below, or directly add
    // this method only processes the modification and is not responsible for adding
    if (null != stopsListDB && stopsListDB.size() > 0) {
      // The data stopsList of the loop database, using the pageId in the stops to
      // convert to the map of the value of the page after the map is fetched,
      for (Stops stopsDB : stopsListDB) {
        if (null == stopsDB) {
          continue;
        }
        String pageId = stopsDB.getPageId();
        MxCellVo mxCellVo = mxCellVoMap.get(pageId);
        // If you get it, you need to modify it. Otherwise, it is to be deleted.
        if (null == mxCellVo) {
          stopsDB.setEnableFlag(false);
          stopsDB.setLastUpdateDttm(new Date());
          stopsDB.setLastUpdateUser(username);
          removeStopsId.add(stopsDB.getId());
          // stopsDB property
          List<Property> properties = stopsDB.getProperties();
          // Whether the judgment is empty
          if (null != properties) {
            List<Property> propertyList = new ArrayList<>();
            // Loop tombstone properties
            for (Property propertyDB : properties) {
              if (null == propertyDB) {
                continue;
              }
              propertyDB.setEnableFlag(false);
              propertyDB.setLastUpdateDttm(new Date());
              propertyDB.setLastUpdateUser(username);
            }
            stopsDB.setProperties(propertyList);
          }
          continue;
        }
        // When deleting "paths", you need to determine whether there are ports of type "any" at
        // both ends of "paths".
        // If so, you need to delete the corresponding port information.
        if (stopsDB.getInPortType() != PortType.ANY && stopsDB.getOutPortType() != PortType.ANY) {
          continue;
        }
        // Value in the pathsDelInfoMap according to the pageId
        String inprot = pathsDelInfoMap.get("in" + stopsDB.getPageId());
        String outprot = pathsDelInfoMap.get("out" + stopsDB.getPageId());
        // If inprot or outprot has a value, the loop attribute finds the attribute of
        // the corresponding storage port and modifies it.
        if (StringUtils.isBlank(inprot) && StringUtils.isBlank(outprot)) {
          continue;
        }
        // Take out the attribute list
        List<Property> properties = stopsDB.getProperties();
        // Judge
        if (null == properties || properties.size() == 0) {
          continue;
        }
        boolean isUpdate = false;
        // Loop attribute
        for (Property property : properties) {
          if (null == property) {
            continue;
          }
          property.setStops(stopsDB);
          if ("inports".equals(property.getName()) && StringUtils.isNotBlank(inprot)) {
            property = this.replacePortValue(inprot, property);
            isUpdate = true;
          } else if ("outports".equals(property.getName()) && StringUtils.isNotBlank(outprot)) {
            property = this.replacePortValue(outprot, property);
          }
        }
        stopsDB.setProperties(properties);
        if (isUpdate) {
          stopsDB.setLastUpdateDttm(new Date());
          stopsDB.setLastUpdateUser(username);
          stopsDB.setFlow(flowDB);
        }
      }
      flowDB.setStopsList(stopsListDB);
    } else {
      // The stops data in the database is empty.
      logger.info("The stops data in the database is empty.");
    }
    if (CollectionUtils.isNotEmpty(removeStopsId)) {
      List<String> publishingNameList =
          flowStopsPublishingDomain.getPublishingNameListByStopsIds(removeStopsId);
      if (null != publishingNameList && publishingNameList.size() > 0) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(
            MessageConfig.STOP_PUBLISHED_CANNOT_DEL_STOP_MSG(
                publishingNameList.toString().replace("[", "'").replace("]", "'")));
      }
    }
    // save flow
    int updateFlow = flowDomain.updateFlow(flowDB);
    // Determine whether the save is successful
    if (updateFlow <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Modify save failed flow");
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("Successful");
  }

  /**
   * Replace port properties
   *
   * @param prot prot
   * @param property property
   */
  private Property replacePortValue(String prot, Property property) {
    String customValue = property.getCustomValue();
    if (null != customValue) {
      if (customValue.contains(prot + ",")) {
        customValue = customValue.replace(prot + ",", "");
      } else if (customValue.contains("," + prot)) {
        customValue = customValue.replace("," + prot, "");
      } else if (customValue.contains(prot)) {
        customValue = customValue.replace(prot, "");
      }
      property.setCustomValue(customValue);
    }
    return property;
  }

  /**
   * save or add flowGroup
   *
   * @param imageXML imageXML
   * @param loadId loadId
   * @param operType operType
   * @param flag flag
   */
  @Override
  public String saveDataForGroup(
      String username, String imageXML, String loadId, String operType, boolean flag)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      logger.warn("illegal user");
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Illegal operation");
    }
    if (StringUtils.isAnyEmpty(imageXML, loadId, operType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("The incoming parameters are empty");
    }
    // Change the `XML' from the page to `mxGraphModel'
    MxGraphModelVo mxGraphModelVo = FlowXmlUtils.xmlToMxGraphModel(imageXML);
    // Parameter null
    if (StringUtils.isAnyEmpty(loadId, operType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The incoming parameter flowId or operType is empty and the save failed.");
    }
    // mxGraphModelVo Parameter null
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The passed parameter mxGraphModelVo is empty and the save failed.");
    }
    if ("ADD".equals(operType)) {
      logger.info("ADD Operation begins");
      return ReturnMapUtils.toJson(this.addGroupFlows(username, mxGraphModelVo, loadId));
    } else if ("MOVED".equals(operType)) {
      logger.info("MOVED Operation begins");
      return ReturnMapUtils.toJson(this.updateGroupMxGraph(username, mxGraphModelVo, loadId));
    } else if ("REMOVED".equals(operType)) {
      logger.info("REMOVED Operation begins");
      return ReturnMapUtils.toJson(this.updateFlowGroup(username, mxGraphModelVo, loadId));
    } else {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("No OperationType:" + operType + "type");
    }
  }

  /** add flows and drawing board mxCell */
  private Map<String, Object> addGroupFlows(
      String username, MxGraphModelVo mxGraphModelVo, String flowGroupId) throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.ILLEGAL_OPERATION_MSG());
    }
    // Query 'flowGroup' according to 'flowGroupId'
    FlowGroup flowGroup = flowGroupDomain.getFlowGroupById(flowGroupId);
    if (null == flowGroup) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.NO_DATA_BY_ID_XXX_MSG(flowGroupId));
    }
    // Determine if 'mxGraphModelVo' and 'flowGroup' are empty
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsg(
          "The passed parameter mxGraphModelVo is empty or the flow does not exist and the addition fails.");
    }
    // update flow
    flowGroup.setLastUpdateDttm(new Date()); // last update date time
    flowGroup.setLastUpdateUser(username); // last update user
    flowGroup.setEnableFlag(true); // is it effective

    // Take out the drawing board of the data inventory
    MxGraphModel mxGraphModel = flowGroup.getMxGraphModel();
    // Determine if the drawing board of the data inventory exists
    if (null == mxGraphModel) {
      return ReturnMapUtils.setFailedMsg("Database without drawing board, adding failed");
    }
    // Put the page's drawing board information into the database canvas
    // Copy the value from 'mxGraphModelVo' to 'mxGraphModelDb'
    BeanUtils.copyProperties(mxGraphModelVo, mxGraphModel);
    mxGraphModel.setEnableFlag(true);
    mxGraphModel.setLastUpdateUser(username);
    mxGraphModel.setLastUpdateDttm(new Date());
    mxGraphModel.setFlowGroup(flowGroup);

    List<MxCell> mxCellDbRoot = mxGraphModel.getRoot();
    // Convert MxCellVo map to MxCellVoList
    List<MxCellVo> addMxCellVoList = this.filterNewMxCell(mxGraphModelVo.getRootVo(), mxCellDbRoot);
    if (null == addMxCellVoList || addMxCellVoList.size() == 0) {
      return ReturnMapUtils.setFailedMsg("No data can be added, the addition failed");
    }

    for (MxCellVo mxCellVo : addMxCellVoList) {
      if (null != mxCellVo) {
        // save MxCell
        // new
        MxCell mxCell = new MxCell();
        // Copy the value in mxCellVo to mxCell
        BeanUtils.copyProperties(mxCellVo, mxCell);
        if (null != mxCellVo.getValue()) {
          mxCell.setValue(mxCellVo.getValue() + mxCellVo.getPageId());
        }
        // Basic properties of mxCell (Required when creating)
        mxCell.setCrtDttm(new Date());
        mxCell.setCrtUser(username);
        // Basic properties of mxCell
        mxCell.setEnableFlag(true);
        mxCell.setLastUpdateUser(username);
        mxCell.setLastUpdateDttm(new Date());
        // mxGraphModel Foreign key
        mxCell.setMxGraphModel(mxGraphModel);

        MxGeometryVo mxGeometryVo = mxCellVo.getMxGeometryVo();
        if (null != mxGeometryVo) {
          // save MxGeometry
          // new
          MxGeometry mxGeometry = new MxGeometry();
          // Copy the value from mxGeometryVo to mxGeometry
          BeanUtils.copyProperties(mxGeometryVo, mxGeometry);
          // Basic properties of mxGeometry (required when creating)
          mxGeometry.setCrtDttm(new Date());
          mxGeometry.setCrtUser(username);
          // Set mxGraphModel basic properties
          mxGeometry.setEnableFlag(true);
          mxGeometry.setLastUpdateUser(username);
          mxGeometry.setLastUpdateDttm(new Date());
          // mxCell Foreign key
          mxGeometry.setMxCell(mxCell);

          mxCell.setMxGeometry(mxGeometry);
        }
        mxCellDbRoot.add(mxCell);
      }
    }
    mxGraphModel.setRoot(mxCellDbRoot);
    mxGraphModelDomain.saveOrUpdate(mxGraphModel);
    flowGroup.setMxGraphModel(mxGraphModel);

    flowGroup = addFlowGroupNodeAndEdge(flowGroup, addMxCellVoList, username);

    // Update flow information
    flowGroupDomain.updateFlowGroup(flowGroup);
    return ReturnMapUtils.setSucceededMsg("Succeeded");
  }

  /** Modification of the drawing board */
  private Map<String, Object> updateGroupMxGraph(
      String username, MxGraphModelVo mxGraphModelVo, String flowGroupId) throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.ILLEGAL_OPERATION_MSG());
    }
    // Determine if the incoming data is empty
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsg("mxGraphModelVo is empty, modification failed");
    }
    MxGraphModel mxGraphModel = mxGraphModelDomain.getMxGraphModelByFlowGroupId(flowGroupId);
    // Determine if the database data to be modified is empty
    if (null == mxGraphModel) {
      return ReturnMapUtils.setFailedMsg(
          "No query to flowGroupId is: “"
              + flowGroupId
              + "”mxGraphModel information is empty, modification failed");
    }
    // Copy the value from mxGraphModelVo to mxGraphModelDb
    BeanUtils.copyProperties(mxGraphModelVo, mxGraphModel);
    // setmxGraphModel basic properties
    mxGraphModel.setLastUpdateUser(username); // Last updater
    mxGraphModel.setLastUpdateDttm(new Date()); // Last update time
    mxGraphModel.setEnableFlag(true); // is it effective
    // The data passed from the page MxCellVo
    List<MxCellVo> mxCellVoList = mxGraphModelVo.getRootVo();
    // Take out the MxCellList information queried by the database.
    List<MxCell> mxCellList = mxGraphModel.getRoot();
    // If the mxCellList is empty, the modification fails because this method only processes the
    // modifications and is not responsible for adding
    if (null == mxCellList || mxCellList.size() == 0) {
      return ReturnMapUtils.setFailedMsg(
          "The database mxCellList is empty and the modification failed.");
    }
    // Including modified and tombstoned
    List<MxCell> updateMxCellList = new ArrayList<>();
    // Convert the list passed to the page to map key for pageId
    Map<String, MxCellVo> mxCellVoMap = new HashMap<>();
    // Judge
    if (null != mxCellVoList) {
      for (MxCellVo mxCell : mxCellVoList) {
        mxCellVoMap.put(mxCell.getPageId(), mxCell);
      }
    }
    // Loop page data for classification (requires modification and tombstone)
    for (MxCell mxCell : mxCellList) {
      if (null == mxCell) {
        continue;
      }
      // Graphic ID (pageId) on the drawing board
      String pageId = mxCell.getPageId();
      // According to the pageId to go to map,
      // Get the description database has a page, do the modification operation,
      // Otherwise, the database has no pages, and the logical deletion is performed.
      MxCellVo mxCellVo = mxCellVoMap.get(pageId);
      if (null != mxCellVo) {
        // Copy the value in mxCellVo to mxCell
        BeanUtils.copyProperties(mxCellVo, mxCell);
        // Basic properties of mxCell
        mxCell.setEnableFlag(true); // is it effective
        mxCell.setLastUpdateUser(username); // Last updater
        mxCell.setLastUpdateDttm(new Date()); // Last update time

        // Do not handle foreign keys when modifying, unless you cancel or modify the foreign key
        // mxGraphModel foreign key
        // mxCell.setMxGraphModel(mxGraphModel);
        MxGeometryVo mxGeometryVo = mxCellVo.getMxGeometryVo();
        MxGeometry mxGeometry = mxCell.getMxGeometry();
        if (null != mxGeometry) {
          if (null != mxGeometryVo) {
            // Copy the value from mxGeometryVo into mxGeometry
            BeanUtils.copyProperties(mxGeometryVo, mxGeometry);

            // setmxGraphModel basic properties
            mxGeometry.setLastUpdateUser(username); // Last updater
            mxGeometry.setLastUpdateDttm(new Date()); // Last update time
            mxGeometry.setEnableFlag(true); // is it effective
            mxGeometry.setMxCell(mxCell);
          }
        }
      } else {
        // Logical deletion
        mxCell.setEnableFlag(false);
        mxCell.setLastUpdateDttm(new Date());
        mxCell.setLastUpdateUser(username);
      }
      // Fill in the modified list
      updateMxCellList.add(mxCell);
    }
    mxGraphModel.setRoot(updateMxCellList);
    // save MxGraphModel
    mxGraphModelDomain.saveOrUpdate(mxGraphModel);
    return ReturnMapUtils.setSucceededMsg("Succeeded");
  }

  /**
   * Modify Flow
   *
   * @param mxGraphModelVo Information from the page
   * @param flowGroupId The data to be modified
   */
  private Map<String, Object> updateFlowGroup(
      String username, MxGraphModelVo mxGraphModelVo, String flowGroupId) throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsg(MessageConfig.ILLEGAL_OPERATION_MSG());
    }
    if (null == mxGraphModelVo) {
      return ReturnMapUtils.setFailedMsg("mxGraphModelVo is empty, modification failed");
    }
    FlowGroup flowGroup = flowGroupDomain.getFlowGroupById(flowGroupId);
    if (null == flowGroup) {
      return ReturnMapUtils.setFailedMsg(
          "The flowGroupId cannot find the corresponding flowGroup, and the modification fails.");
    }
    // Save and modify the drawing board information
    Map<String, Object> map = this.updateGroupMxGraph(username, mxGraphModelVo, flowGroupId);
    // Determine if mxGraphModel is saved successfully
    if (null != map) {
      Object code = map.get("code");
      if (null == code || 200 != (int) code) {
        return map;
      }
    } else {
      return ReturnMapUtils.setFailedMsg("failed, Near 'updateMxGraph' ");
    }
    flowGroup = flowGroupDomain.getFlowGroupById(flowGroupId);
    // Last update time
    flowGroup.setLastUpdateDttm(new Date());
    // Last updater
    flowGroup.setLastUpdateUser(username);

    // MxCellVo's list from the page
    List<MxCellVo> mxCellVoList = mxGraphModelVo.getRootVo();

    // Separate the flow and lines in the mxCellVoList
    Map<String, List<MxCellVo>> elementsAndPathsMap =
        MxGraphModelUtils.mxCellVoDistinguishNodesAndPaths(mxCellVoList);

    if (null != elementsAndPathsMap) {
      // Take the line of mxCellVoList and to map
      Map<String, MxCellVo> pathsMxCellVoMap =
          stopsMxCellVoListToMap(elementsAndPathsMap.get("paths"));
      // Get out the PathsList stored in the database
      List<FlowGroupPaths> flowGroupPathsList = flowGroup.getFlowGroupPathsList();
      // Determine whether the list of lines in the database is empty
      if (null != flowGroupPathsList && flowGroupPathsList.size() > 0) {
        // The data pathsList of the loop database is retrieved by using the pageId in the stops to
        // convert the map to the value of the page passed by the map.
        for (FlowGroupPaths flowGroupPaths : flowGroupPathsList) {
          if (null == flowGroupPaths) {
            continue;
          }
          String pageId = flowGroupPaths.getPageId();
          MxCellVo mxCellVo = pathsMxCellVoMap.get(pageId);
          // If you get it, you need to modify it. Otherwise, it is to be deleted.
          if (null != mxCellVo) {
            continue;
          }
          flowGroupPaths.setEnableFlag(false);
          flowGroupPaths.setLastUpdateUser(username);
          flowGroupPaths.setLastUpdateDttm(new Date());
        }
        flowGroup.setFlowGroupPathsList(flowGroupPathsList);
      }

      // Take the flow of mxCellVoList and to map
      Map<String, MxCellVo> stopsMxCellVoMap =
          stopsMxCellVoListToMap(elementsAndPathsMap.get("nodes"));
      // Get out the flowList stored in the database
      List<Flow> flowList = flowGroup.getFlowList();
      // If the flowList in the database is empty
      if (null != flowList && flowList.size() > 0) {
        for (Flow flow : flowList) {
          if (null == flow) {
            continue;
          }
          String pageId = flow.getPageId();
          MxCellVo mxCellVo = stopsMxCellVoMap.get(pageId);
          // If you get it, you need to modify it. Otherwise, it is to be deleted.
          if (null != mxCellVo) {
            continue;
          }
          // logically delete
          flow.setEnableFlag(false);
          // Last update time
          flow.setLastUpdateDttm(new Date());
          // Last updater
          flow.setLastUpdateUser(username);
        }
        flowGroup.setFlowList(flowList);
      }
      // Get out the flowGroupList stored in the database
      List<FlowGroup> flowGroupList = flowGroup.getFlowGroupList();
      // If the flowGroupList in the database is empty
      if (null != flowGroupList && flowGroupList.size() > 0) {
        for (FlowGroup flowGroup_i : flowGroupList) {
          if (null == flowGroup_i) {
            continue;
          }
          String pageId = flowGroup_i.getPageId();
          MxCellVo mxCellVo = stopsMxCellVoMap.get(pageId);
          // If you get it, you need to modify it. Otherwise, it is to be deleted.
          if (null != mxCellVo) {
            continue;
          }
          // logically delete
          flowGroup_i.setEnableFlag(false);
          // Last update time
          flowGroup_i.setLastUpdateDttm(new Date());
          // Last updater
          flowGroup_i.setLastUpdateUser(username);
        }
        flowGroup.setFlowGroupList(flowGroupList);
      }
      // save flowGroup
      flowGroupDomain.updateFlowGroup(flowGroup);
    }
    return ReturnMapUtils.setSucceededMsg("Succeeded");
  }

  private Map<String, MxCellVo> stopsMxCellVoListToMap(List<MxCellVo> mxCellVoList) {
    Map<String, MxCellVo> stopsMxCellVoMap = new HashMap<>();
    // Determine if the mxCellVoList passed from the page is empty.
    if (null != mxCellVoList && mxCellVoList.size() > 0) {
      // Loop Convert stopsMxCellVo (page data) to stopsMxCellVoMap
      for (MxCellVo stopsMxCellVo : mxCellVoList) {
        if (null == stopsMxCellVo) {
          continue;
        }
        stopsMxCellVoMap.put(stopsMxCellVo.getPageId(), stopsMxCellVo);
      }
    }
    return stopsMxCellVoMap;
  }

  /**
   * @param mxCellVoList Data passed from page
   * @param mxCellDbRoot database data
   */
  private List<MxCellVo> filterNewMxCell(List<MxCellVo> mxCellVoList, List<MxCell> mxCellDbRoot) {
    List<MxCellVo> rtnMxCellVoList = null;
    // Map of the data sent from the page
    Map<String, MxCellVo> mxCellVoMap = new HashMap<>();
    // The mxCellList passed to the page is transferred to the map, and the key is pageId.
    if (null != mxCellVoList && mxCellVoList.size() > 0) {
      // The mxCellList passed to the page is transferred to the map, and the key is pageId.
      for (MxCellVo mxCellVo : mxCellVoList) {
        if (null != mxCellVo && StringUtils.isNotBlank(mxCellVo.getPageId())) {
          mxCellVoMap.put(mxCellVo.getPageId(), mxCellVo);
        }
      }
    }
    for (MxCell mxCell : mxCellDbRoot) {
      if (null != mxCell) {
        // Use pageId to go to map
        MxCellVo mxCellVo = mxCellVoMap.get(mxCell.getPageId());
        // Get the description database already exists, do not need to add, remove the value removed
        // in the map
        if (null != mxCellVo) {
          mxCellVoMap.remove(mxCell.getPageId());
        }
      }
    }
    // Determine whether there is data in the map after remove, if there is any new processing
    if (mxCellVoMap.size() > 0) {
      rtnMxCellVoList = new ArrayList<>(mxCellVoMap.values());
    }
    return rtnMxCellVoList;
  }

  public String addMxCellAndData(MxGraphVo mxGraphVo, String username) throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == mxGraphVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    String loadId = mxGraphVo.getLoadId();
    if (StringUtils.isBlank(loadId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("loadId is null");
    }
    FlowGroup flowGroup = flowGroupDomain.getFlowGroupById(loadId);

    // update flow
    flowGroup.setLastUpdateDttm(new Date()); // last update date time
    flowGroup.setLastUpdateUser(username); // last update user
    flowGroup.setEnableFlag(true); // is it effective

    // Take out the drawing board of the data inventory
    MxGraphModel mxGraphModel = flowGroup.getMxGraphModel();
    // Determine if the drawing board of the data inventory exists
    if (null == mxGraphModel) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Database without drawing board, adding failed");
    }
    // Put the page's drawing board information into the database canvas
    mxGraphModel.setEnableFlag(true);
    mxGraphModel.setLastUpdateUser(username);
    mxGraphModel.setLastUpdateDttm(new Date());
    mxGraphModel.setFlowGroup(flowGroup);

    List<MxCell> mxCellDbRoot = mxGraphModel.getRoot();
    // Convert MxCellVo map to MxCellVoList
    List<MxCellVo> addMxCellVoList = mxGraphVo.getMxCellVoList();
    if (null == addMxCellVoList || addMxCellVoList.size() == 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("No data can be added, the addition failed");
    }
    if (null == mxCellDbRoot || mxCellDbRoot.size() == 0) {
      mxCellDbRoot = MxCellUtils.initMxCell(username, mxGraphModel);
    }
    for (MxCellVo mxCellVo : addMxCellVoList) {
      if (null == mxCellVo) {
        continue;
      }
      // save MxCell
      // new
      MxCell mxCell = new MxCell();
      // Copy the value in mxCellVo to mxCell
      BeanUtils.copyProperties(mxCellVo, mxCell);
      if (null != mxCellVo.getValue()) {
        mxCell.setValue(mxCellVo.getValue() + mxCellVo.getPageId());
      }
      mxCell.setVertex(String.valueOf("true".equals(mxCellVo.getVertex()) ? 1 : 0));
      mxCell.setEdge(String.valueOf("false".equals(mxCellVo.getEdge()) ? 0 : 1));
      // Basic properties of mxCell (Required when creating)
      mxCell.setCrtDttm(new Date());
      mxCell.setCrtUser(username);
      // Basic properties of mxCell
      mxCell.setEnableFlag(true);
      mxCell.setLastUpdateUser(username);
      mxCell.setLastUpdateDttm(new Date());
      // mxGraphModel Foreign key
      mxCell.setMxGraphModel(mxGraphModel);

      MxGeometryVo mxGeometryVo = mxCellVo.getMxGeometryVo();
      if (null != mxGeometryVo) {
        // save MxGeometry
        // new
        MxGeometry mxGeometry = new MxGeometry();
        // Copy the value from mxGeometryVo to mxGeometry
        BeanUtils.copyProperties(mxGeometryVo, mxGeometry);
        mxGeometry.setRelative("false".equals(mxGeometryVo.getRelative()) ? null : "1");
        // Basic properties of mxGeometry (required when creating)
        mxGeometry.setCrtDttm(new Date());
        mxGeometry.setCrtUser(username);
        // Set mxGraphModel basic properties
        mxGeometry.setEnableFlag(true);
        mxGeometry.setLastUpdateUser(username);
        mxGeometry.setLastUpdateDttm(new Date());
        // mxCell Foreign key
        mxGeometry.setMxCell(mxCell);

        mxCell.setMxGeometry(mxGeometry);
      }
      mxCellDbRoot.add(mxCell);
    }
    mxGraphModel.setRoot(mxCellDbRoot);
    flowGroup.setMxGraphModel(mxGraphModel);
    flowGroup = addFlowGroupNodeAndEdge(flowGroup, addMxCellVoList, username);
    flowGroupDomain.updateFlowGroup(flowGroup);
    if (null == addMxCellVoList || addMxCellVoList.size() == 0) {
      return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }

    // save id and page list
    List<Map<String, String>> addNodeIdAndPageIdList = new ArrayList<>();
    Map<String, String> addNodeIdAndPageId;
    for (MxCellVo mxCellVo : addMxCellVoList) {
      if (null == mxCellVo || "1".equals(mxCellVo.getEdge())) {
        continue;
      }
      addNodeIdAndPageId = new HashMap<>();
      String flowIdByPageId = flowDomain.getFlowIdByPageId(loadId, mxCellVo.getPageId());
      if (StringUtils.isNotBlank(flowIdByPageId)) {
        addNodeIdAndPageId.put("id", flowIdByPageId);
        addNodeIdAndPageId.put("pageId", mxCellVo.getPageId());
        addNodeIdAndPageId.put("type", "flow");
        addNodeIdAndPageIdList.add(addNodeIdAndPageId);
        continue;
      }
      String flowGroupIdByPageId =
          flowGroupDomain.getFlowGroupIdByPageId(loadId, mxCellVo.getPageId());
      if (StringUtils.isNotBlank(flowGroupIdByPageId)) {
        addNodeIdAndPageId.put("id", flowGroupIdByPageId);
        addNodeIdAndPageId.put("pageId", mxCellVo.getPageId());
        addNodeIdAndPageId.put("type", "flowGroup");
        addNodeIdAndPageIdList.add(addNodeIdAndPageId);
      }
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr(
        "addNodeIdAndPageIdList", addNodeIdAndPageIdList);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private FlowGroup addFlowGroupNodeAndEdge(
      FlowGroup flowGroup, List<MxCellVo> addMxCellVo, String username) {
    if (null == flowGroup) {
      return null;
    }
    // Separate the flows and lines that need to be added in addMxCellVoList
    Map<String, List<MxCellVo>> flowGroupNodeAndEdge =
        MxGraphModelUtils.mxCellVoDistinguishNodesAndPaths(addMxCellVo);

    // Take mxCellVoList (list of elements) from Map
    List<MxCellVo> flowGroupNodeObject = flowGroupNodeAndEdge.get("nodes");

    // Generate a list of elements based on the contents of the MxCellList
    Map<String, List> addFlowAndFlowGroupsMap =
        MxGraphModelUtils.mxCellVoListToFlowAndFlowGroups(flowGroupNodeObject, flowGroup, username);
    List<Flow> addFlowsList = addFlowAndFlowGroupsMap.get("flows");

    List<Flow> flowList = flowGroup.getFlowList();
    if (null != addFlowsList && addFlowsList.size() > 0) {
      if (null == flowList) {
        flowList = new ArrayList<>();
      }
      for (Flow flow : addFlowsList) {
        flow.setFlowGroup(flowGroup);
        flowList.add(flow);
      }
      // flowList = flowDomain.saveOrUpdate(flowList);
      flowGroup.setFlowList(flowList);
    }

    List<FlowGroup> addFlowGroupList = addFlowAndFlowGroupsMap.get("flowGroups");

    List<FlowGroup> flowGroupList = flowGroup.getFlowGroupList();
    if (null != addFlowGroupList && addFlowGroupList.size() > 0) {
      if (null == flowGroupList) {
        flowGroupList = new ArrayList<>();
      }
      for (FlowGroup addFlowGroup : addFlowGroupList) {
        addFlowGroup.setFlowGroup(flowGroup);
        flowGroupList.add(addFlowGroup);
      }
      flowGroup.setFlowGroupList(flowGroupList);
    }

    // Take "mxCellVoList" from the "Map" (array of lines)
    List<MxCellVo> objectPaths = flowGroupNodeAndEdge.get("paths");

    // Generate a list of paths based on the contents of the MxCellList
    List<FlowGroupPaths> addFlowGroupPathsList =
        MxGraphModelUtils.mxCellVoListToFlowGroupPathsList(username, objectPaths, flowGroup);
    // Judge empty pathsList
    if (null != addFlowGroupPathsList && addFlowGroupPathsList.size() > 0) {
      List<FlowGroupPaths> flowGroupPathsList = flowGroup.getFlowGroupPathsList();
      for (FlowGroupPaths addFlowGroupPaths : addFlowGroupPathsList) {
        addFlowGroupPaths.setFlowGroup(flowGroup);
        flowGroupPathsList.add(addFlowGroupPaths);
      }
    }

    return flowGroup;
  }
}
