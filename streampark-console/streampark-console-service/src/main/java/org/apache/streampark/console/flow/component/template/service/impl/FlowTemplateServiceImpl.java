package org.apache.streampark.console.flow.component.template.service.impl;

import org.apache.streampark.console.flow.base.utils.FileUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.TemplateType;
import org.apache.streampark.console.flow.common.constant.Constants;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.flow.domain.FlowDomain;
import org.apache.streampark.console.flow.component.flow.domain.FlowGroupDomain;
import org.apache.streampark.console.flow.component.flow.domain.StopsDomain;
import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroup;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.utils.FlowXmlUtils;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxCellDomain;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxCellUtils;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxGraphModelUtils;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxGraphUtils;
import org.apache.streampark.console.flow.component.template.domain.FlowTemplateDomain;
import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;
import org.apache.streampark.console.flow.component.template.service.IFlowTemplateService;
import org.apache.streampark.console.flow.component.template.utils.FlowTemplateUtils;
import org.apache.streampark.console.flow.component.template.vo.FlowTemplateVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FlowTemplateServiceImpl implements IFlowTemplateService {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private Logger logger = LoggerUtil.getLogger();

  private final FlowTemplateDomain flowTemplateDomain;
  private final FlowGroupDomain flowGroupDomain;
  private final FlowDomain flowDomain;
  private final MxCellDomain mxCellDomain;

  @Autowired
  public FlowTemplateServiceImpl(
      FlowTemplateDomain flowTemplateDomain,
      FlowGroupDomain flowGroupDomain,
      FlowDomain flowDomain,
      StopsDomain stopsDomain,
      MxCellDomain mxCellDomain) {
    this.flowTemplateDomain = flowTemplateDomain;
    this.flowGroupDomain = flowGroupDomain;
    this.flowDomain = flowDomain;
    this.mxCellDomain = mxCellDomain;
  }

  /**
   * add FlowTemplate
   *
   * @param username
   * @param name
   * @param loadId
   * @param templateType
   * @return
   */
  @Override
  public String addFlowTemplate(String username, String name, String loadId, String templateType) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(name)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param name is empty");
    }
    if (StringUtils.isBlank(loadId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param 'loadId' is empty");
    }
    if (StringUtils.isBlank(templateType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param 'templateType' is empty");
    }

    String templateXmlPath;
    String templateXmlStr;
    TemplateType saveTemplateType;

    switch (templateType) {
      case "TASK":
        {
          Flow flowById = flowDomain.getFlowById(loadId);
          if (null == flowById) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(
                "Flow information is empty,loadId：" + loadId);
          }

          if (Constants.ENGIN_FLINK.equalsIgnoreCase(flowById.getEngineType())) {
            templateXmlPath = SysParamsCache.ENGINE_FLINK_XML_PATH;
          } else {
            templateXmlPath = SysParamsCache.ENGINE_SPARK_XML_PATH;
          }

          saveTemplateType = TemplateType.TASK;
          String mxGraphXml_Flow = "";
          MxGraphModel mxGraphModel = flowById.getMxGraphModel();
          if (null != mxGraphModel) {
            // Convert the query mxGraphModel to XML
            mxGraphXml_Flow = MxGraphUtils.mxGraphModelToMxGraph(true, mxGraphModel);
          }

          // Splicing XML according to flowById
          templateXmlStr = FlowXmlUtils.flowAndStopInfoToXml(flowById, mxGraphXml_Flow);
          break;
        }
      case "GROUP":
        {
          FlowGroup flowGroupById = flowGroupDomain.getFlowGroupById(loadId);
          if (null == flowGroupById) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(
                "Group information is empty,loadId：" + loadId);
          }

          if (Constants.ENGIN_FLINK.equalsIgnoreCase(flowGroupById.getEngineType())) {
            templateXmlPath = SysParamsCache.ENGINE_FLINK_XML_PATH;
          } else {
            templateXmlPath = SysParamsCache.ENGINE_SPARK_XML_PATH;
          }

          saveTemplateType = TemplateType.GROUP;
          // Splicing XML according to flowGroupById
          templateXmlStr = FlowXmlUtils.flowGroupToXmlStr(flowGroupById);
          break;
        }
      default:
        return ReturnMapUtils.setFailedMsgRtnJsonStr("param 'templateType' is error");
    }

    logger.info(templateXmlStr);
    String saveFileName = UUIDUtils.getUUID32();
    String path = FileUtils.createXml(templateXmlStr, saveFileName, templateXmlPath);

    FlowTemplate flowTemplate = FlowTemplateUtils.newFlowTemplateNoId(username);
    // flowTemplate.setId(UUIDUtils.getUUID32());
    flowTemplate.setName(name);
    // XML to file and save to specified directory
    flowTemplate.setPath(path);
    flowTemplate.setTemplateType(saveTemplateType);
    flowTemplate.setUrl("/xml/" + saveFileName + ".xml");
    flowTemplate.setDescription(name);
    flowTemplateDomain.insertFlowTemplate(flowTemplate);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("save template success");
  }

  @Override
  public String getFlowTemplateListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("limit or offset is null");
    }
    Page<Process> page = PageHelper.startPage(offset, limit, "crt_dttm desc");
    flowTemplateDomain.getFlowTemplateListByParam(username, isAdmin, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * Delete the template based on id
   *
   * @param id
   * @return
   */
  @Override
  public String deleteFlowTemplate(String id) {
    if (StringUtils.isBlank(id)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("id"));
    }
    int deleteTemplate = flowTemplateDomain.updateEnableFlagById(id, false);
    if (deleteTemplate > 0) {
      return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    } else {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
  }

  /**
   * Download template
   *
   * @param flowTemplateId
   */
  @Override
  public void templateDownload(HttpServletResponse response, String flowTemplateId) {
    FlowTemplate flowTemplate = flowTemplateDomain.getFlowTemplateById(flowTemplateId);
    if (null == flowTemplate) {
      logger.info("Template is empty,Download template failed");
    } else {
      String fileName = flowTemplate.getName() + ".xml"; // The default save name of the file
      String filePath = flowTemplate.getPath(); // File storage path
      // FileUtils.downloadFileResponse(response, fileName, filePath);
      FileUtils.downloadFile(response, fileName, filePath);
    }
  }

  /**
   * Upload xml file and save flowTemplate
   *
   * @param file
   * @return
   */
  @Override
  public String uploadXmlFile(String username, MultipartFile file) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (file.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }
    // todo 上传xml文件路径
    Map<String, Object> uploadMap =
        FileUtils.uploadRtnMap(file, SysParamsCache.ENGINE_FLINK_XML_PATH, null);
    if (null == uploadMap || uploadMap.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }
    Integer code = (Integer) uploadMap.get("code");
    if (500 == code) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("failed to upload file");
    }
    String saveFileName = (String) uploadMap.get("saveFileName");
    String fileName = (String) uploadMap.get("fileName");
    String path = (String) uploadMap.get("path");
    // Read the XML file according to the saved file path and return the XML string
    String xmlFileToStr = FileUtils.FileToStrByAbsolutePath(path);
    if (StringUtils.isBlank(xmlFileToStr)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("XML file read failed, upload template failed");
    }
    TemplateType templateType = MxGraphUtils.determineTemplateType(xmlFileToStr);
    if (null == templateType) {
      FileUtils.deleteFile(path);
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "There is a problem with the template, please check and try again");
    }
    FlowTemplate flowTemplate = FlowTemplateUtils.newFlowTemplateNoId(username);
    flowTemplate.setId(UUIDUtils.getUUID32());
    flowTemplate.setName(fileName);
    flowTemplate.setPath(path);
    flowTemplate.setUrl("/xml/" + saveFileName);
    flowTemplate.setTemplateType(templateType);
    flowTemplate.setDescription(fileName);
    flowTemplateDomain.insertFlowTemplate(flowTemplate);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("successful template upload");
  }

  @Override
  public String flowTemplateList(String username, boolean isAdmin) {
    List<FlowTemplate> findTemPlateList = flowTemplateDomain.getFlowTemplateList(username, isAdmin);
    if (null == findTemPlateList || findTemPlateList.size() <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Query result is empty");
    }
    List<FlowTemplateVo> flowTemplateVoList = new ArrayList<>();
    for (FlowTemplate flowTemplate : findTemPlateList) {
      if (null != flowTemplate) {
        FlowTemplateVo flowTemplateVo = new FlowTemplateVo();
        BeanUtils.copyProperties(flowTemplate, flowTemplateVo);
        flowTemplateVoList.add(flowTemplateVo);
      }
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("temPlateList", flowTemplateVoList);
  }

  @Override
  public String loadGroupTemplate(String username, String templateId, String loadId)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Illegal user, Load failed");
    }
    FlowGroup flowGroupById = flowGroupDomain.getFlowGroupById(loadId);
    if (null == flowGroupById) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Load failed, please try again");
    }
    FlowTemplate flowTemplate = flowTemplateDomain.getFlowTemplateById(templateId);
    if (null == flowTemplate) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Load failed, please try again");
    }
    // The XML file is read and returned according to the saved file path
    String xmlFileToStr = FileUtils.FileToStrByAbsolutePath(flowTemplate.getPath());
    if (StringUtils.isBlank(xmlFileToStr)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("XML file read failed, loading template failed");
    }
    // Get the maximum value of pageId in drawing board node
    MxGraphModel mxGraphModelDb = flowGroupById.getMxGraphModel();
    Integer maxPageId = 2;
    if (null != mxGraphModelDb && StringUtils.isNotBlank(mxGraphModelDb.getId())) {
      maxPageId = mxCellDomain.getMaxPageIdByMxGraphModelId(mxGraphModelDb.getId());
    }
    maxPageId = (null != maxPageId ? maxPageId : 1);
    // Get the current flowGroup containing all flow names
    String[] flowNamesByFlowGroupId = flowDomain.getFlowAndGroupNamesByFlowGroupId(loadId);

    if (TemplateType.TASK == flowTemplate.getTemplateType()) {
      Flow flowXml = FlowXmlUtils.xmlToFlow(xmlFileToStr, maxPageId, username, false);
      if (null == flowXml) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr("Conversion failure");
      }
      flowXml.setFlowGroup(flowGroupById);
      List<Flow> flowList = flowGroupById.getFlowList();
      flowList.add(flowXml);
      flowGroupById.setFlowList(flowList);

      if (null == mxGraphModelDb) {
        mxGraphModelDb = MxGraphModelUtils.mxGraphModelNewNoId(username);
        mxGraphModelDb.setFlowGroup(flowGroupById);
      }
      List<MxCell> rootDb = mxGraphModelDb.getRoot();
      if (null == rootDb || rootDb.size() <= 1) {
        List<MxCell> mxCellList = MxCellUtils.initMxCell(username, mxGraphModelDb);
        rootDb.addAll(mxCellList);
      }
      if (Arrays.asList(flowNamesByFlowGroupId).contains(flowXml.getName())) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr("Duplicate FlowName");
      }

      MxCell mxCell =
          MxCellUtils.AddDefaultFlowMxCell(username, flowXml.getPageId(), flowXml.getName());
      mxCell.setMxGraphModel(mxGraphModelDb);
      rootDb.add(mxCell);
      mxGraphModelDb.setRoot(rootDb);
      flowGroupById.setMxGraphModel(mxGraphModelDb);

    } else if (TemplateType.GROUP == flowTemplate.getTemplateType()) {

      Map<String, Object> XmlStrToFlowGroupRtnMap =
          FlowXmlUtils.XmlStrToFlowGroup(
              xmlFileToStr, maxPageId, username, flowNamesByFlowGroupId, false);
      if (200 != (Integer) XmlStrToFlowGroupRtnMap.get("code")) {
        return ReturnMapUtils.toJson(XmlStrToFlowGroupRtnMap);
      }
      FlowGroup flowGroupXml = (FlowGroup) XmlStrToFlowGroupRtnMap.get("flowGroup");
      if (null == flowGroupXml) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr("Conversion failure");
      }
      // Added processing drawing board data

      // Fetch the drawing board data to be added
      MxGraphModel mxGraphModelXml = flowGroupXml.getMxGraphModel();
      if (null != mxGraphModelXml) {
        MxGraphModel mxGraphModel = flowGroupById.getMxGraphModel();
        if (null == mxGraphModel) {
          mxGraphModel = MxGraphModelUtils.mxGraphModelNewNoId(username);
          mxGraphModel.setFlowGroup(flowGroupById);
          mxGraphModel.setId(UUIDUtils.getUUID32());
          // mxGraphModel = mxGraphModelDomain.saveOrUpdate(mxGraphModel);
        }
        List<MxCell> rootDb = mxGraphModel.getRoot();
        if (null == rootDb || rootDb.size() <= 1) {
          List<MxCell> mxCellList = MxCellUtils.initMxCell(username, mxGraphModel);
          rootDb.addAll(mxCellList);
        }
        List<MxCell> rootXml = mxGraphModelXml.getRoot();
        if (null != rootXml && rootXml.size() > 0) {
          for (MxCell mxCell : rootXml) {
            if (null == mxCell) {
              continue;
            }
            // Associated sketchpad
            mxCell.setMxGraphModel(mxGraphModel);
          }
          rootDb.addAll(rootXml);
        }
        mxGraphModel.setRoot(rootDb);
      }

      // Added processing flow data
      List<Flow> flowListXml = flowGroupXml.getFlowList();
      if (null != flowListXml && flowListXml.size() > 0) {
        List<Flow> flowList = flowGroupById.getFlowList();
        for (Flow flowXml : flowListXml) {
          if (null == flowXml) {
            continue;
          }
          // link
          flowXml.setFlowGroup(flowGroupById);
        }
        flowList.addAll(flowListXml);
        flowGroupById.setFlowList(flowList);
      }

      // Added processing of flowGroupPath data
      List<FlowGroupPaths> flowGroupPathsListXml = flowGroupXml.getFlowGroupPathsList();
      if (null != flowGroupPathsListXml && flowGroupPathsListXml.size() > 0) {
        List<FlowGroupPaths> flowGroupPathsList = flowGroupById.getFlowGroupPathsList();
        for (FlowGroupPaths flowGroupPathsXml : flowGroupPathsListXml) {
          flowGroupPathsXml.setFlowGroup(flowGroupById);
        }
        flowGroupPathsList.addAll(flowGroupPathsListXml);
        flowGroupById.setFlowGroupPathsList(flowGroupPathsList);
      }

      // Added processing of flowGroupPath data
      List<FlowGroup> flowGroupListXml = flowGroupXml.getFlowGroupList();
      if (null != flowGroupListXml && flowGroupListXml.size() > 0) {
        List<FlowGroup> flowGroupList = flowGroupById.getFlowGroupList();
        for (FlowGroup flowGroupListXml_i : flowGroupListXml) {
          flowGroupListXml_i.setFlowGroup(flowGroupById);
        }
        flowGroupList.addAll(flowGroupListXml);
        flowGroupById.setFlowGroupList(flowGroupList);
      }

    } else {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "Template is wrong, please check the template, loading template failed");
    }

    // update
    flowGroupDomain.updateFlowGroup(flowGroupById);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("success");
  }

  @Override
  public String loadTaskTemplate(String username, String templateId, String flowId)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Illegal user, Load failed");
    }
    if (null == flowId) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("FlowId is empty, loading FlowTemplate failed");
    }
    if (null == templateId) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "FlowTemplateId is empty, loading FlowTemplate failed");
    }
    Flow flowById = flowDomain.getFlowById(flowId);
    if (null == flowById) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Flow is empty, loading FlowTemplate failed");
    }
    FlowTemplate flowTemplate = flowTemplateDomain.getFlowTemplateById(templateId);
    if (null == flowTemplate) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "FlowTemplate is empty, loading FlowTemplate failed");
    }
    if (TemplateType.TASK != flowTemplate.getTemplateType()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "Template types do not match, loading FlowTemplate failed");
    }
    // Read the xml file according to the saved file path and return
    String xmlFileToStr = FileUtils.FileToStrByAbsolutePath(flowTemplate.getPath());
    if (StringUtils.isBlank(xmlFileToStr)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The xml file failed to read and the FlowTemplate failed to be loaded.");
    }
    // Get the maximum pageId in stop
    Integer maxStopPageId = flowDomain.getMaxStopPageIdByFlowId(flowId);
    maxStopPageId = null == maxStopPageId ? 2 : maxStopPageId;
    // Get the current flow containing all stop names
    String[] stopNamesByFlowId = flowDomain.getStopNamesByFlowId(flowId);
    Map<String, Object> flowTemplateXmlToFlowRtnMap =
        FlowXmlUtils.flowTemplateXmlToFlow(
            xmlFileToStr, username, maxStopPageId + "", null, stopNamesByFlowId);
    if (200 != (Integer) flowTemplateXmlToFlowRtnMap.get("code")) {
      return ReturnMapUtils.toJson(flowTemplateXmlToFlowRtnMap);
    }
    Flow flowTemplateXmlToFlow = (Flow) flowTemplateXmlToFlowRtnMap.get("flow");
    if (null == flowTemplateXmlToFlow) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Conversion failure");
    }
    // Added processing drawing board data
    // Fetch the drawing board data to be added
    MxGraphModel mxGraphModelXml = flowTemplateXmlToFlow.getMxGraphModel();
    if (null != mxGraphModelXml) {
      MxGraphModel mxGraphModel = flowById.getMxGraphModel();
      if (null == mxGraphModel) {
        mxGraphModel = MxGraphModelUtils.setMxGraphModelBasicInformation(null, false, username);
      } else {
        // Update basic information
        mxGraphModel = MxGraphModelUtils.updateMxGraphModelBasicInformation(mxGraphModel, username);
      }
      // link flow
      mxGraphModel.setFlow(flowById);

      List<MxCell> mxCellList = null;
      if (null == mxGraphModel.getRoot() || mxGraphModel.getRoot().size() <= 1) {
        mxCellList = MxCellUtils.initMxCell(username, mxGraphModel);
      }
      if (null == mxCellList) {
        mxCellList = new ArrayList<>();
      }
      List<MxCell> rootXml = mxGraphModelXml.getRoot();
      if (null != rootXml && rootXml.size() > 0) {
        for (MxCell mxCell : rootXml) {
          mxCell.setMxGraphModel(mxGraphModel);
          mxCellList.add(mxCell);
        }
      }
      mxGraphModel.setRoot(mxCellList);
      flowById.setMxGraphModel(mxGraphModel);
    }
    // Added processing flow data
    List<Stops> stopsListXml = flowTemplateXmlToFlow.getStopsList();
    if (null != stopsListXml && stopsListXml.size() > 0) {
      List<Stops> stopsList = flowById.getStopsList();
      if (null == stopsList) {
        stopsList = new ArrayList<>();
      }
      for (Stops stops : stopsListXml) {
        stops.setFlow(flowById);
        stopsList.add(stops);
      }
      flowById.setStopsList(stopsList);
    }
    List<Paths> pathsListXml = flowTemplateXmlToFlow.getPathsList();
    if (null != pathsListXml && pathsListXml.size() > 0) {
      List<Paths> pathsList = flowById.getPathsList();
      if (null == pathsList) {
        pathsList = new ArrayList<>();
      }
      for (Paths paths : pathsListXml) {
        paths.setFlow(flowById);
        pathsList.add(paths);
      }
      flowById.setPathsList(pathsList);
    }
    // save
    flowDomain.updateFlow(flowById);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("Successfully loaded FlowTemplate");
  }
}
