/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.flow.component.flow.service.impl;

import org.apache.streampark.console.flow.base.utils.CheckFiledUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.flow.domain.FlowDomain;
import org.apache.streampark.console.flow.component.flow.domain.FlowStopsPublishingDomain;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;
import org.apache.streampark.console.flow.component.flow.service.IPropertyService;
import org.apache.streampark.console.flow.component.flow.utils.StopsUtils;
import org.apache.streampark.console.flow.component.flow.vo.StopsVo;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponentProperty;
import org.apache.streampark.console.flow.utils.FlinkTableUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PropertyServiceImpl implements IPropertyService {

    private final Logger logger = LoggerUtil.getLogger();

    private final FlowDomain flowDomain;
    private final StopsComponentDomain stopsComponentDomain;
    private final FlowStopsPublishingDomain flowStopsPublishingDomain;

    @Autowired
    public PropertyServiceImpl(
                               FlowDomain flowDomain,
                               StopsComponentDomain stopsComponentDomain,
                               FlowStopsPublishingDomain flowStopsPublishingDomain) {
        this.flowDomain = flowDomain;
        this.stopsComponentDomain = stopsComponentDomain;
        this.flowStopsPublishingDomain = flowStopsPublishingDomain;
    }

    @Override
    public String queryAll(String fid, String stopPageId) {
        if (StringUtils.isBlank(fid)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("fid"));
        }
        if (StringUtils.isBlank(stopPageId)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("stopPageId"));
        }
        Stops stops = flowDomain.getStopsByPageId(fid, stopPageId);
        if (null == stops) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        StopsComponent stopsComponentByBundle =
            stopsComponentDomain.getStopsComponentByBundle(stops.getBundle());
        StopsVo stopsVo = StopsUtils.stopPoToVo(stops, stopsComponentByBundle);
        if (null == stopsVo) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("stopsVo", stopsVo);
    }

    @Override
    public String updatePropertyList(String username, String[] content) {
        if (StringUtils.isBlank(username)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
        }
        if (null == content || content.length == 0) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        }
        int updateStops = 0;
        for (String string : content) {
            // Use the #id# tag to intercept the data, the first is the content, and the second is the id
            // of the record to be modified.
            String[] split = string.split("#id#");
            if (split.length != 2) {
                continue;
            }
            String updateContent = split[0];
            String updateId = split[1];
            updateStops += flowDomain.updatePropertyCustomValue(username, updateContent, updateId);
        }
        if (updateStops > 0) {
            return ReturnMapUtils.setSucceededMsgRtnJsonStr(
                "The stops attribute was successfully modified. counts:" + updateStops);
        } else {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPDATE_ERROR_MSG());
        }
    }

    @Override
    public String updateProperty(String username, String content, String id) {
        if (StringUtils.isBlank(username)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
        }
        if (StringUtils.isBlank(id)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        }
        int updateStops = flowDomain.updatePropertyCustomValue(username, content, id);
        if (updateStops > 0) {
            logger.info("The stops attribute was successfully modified:" + updateStops);
            return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("value", content);
        } else {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
        }
    }

    @Override
    public List<Property> getStopsPropertyList() {
        return flowDomain.getStopsPropertyList();
    }

    @Override
    public int deleteStopsPropertyById(String id) {
        return flowDomain.deleteStopsPropertyById(id);
    }

    /** Compare the 'stops' template if it is different */
    @Override
    public void checkStopTemplateUpdate(String username, String id) {
        if (StringUtils.isBlank(username)) {
            return;
        }
        Map<String, Property> PropertyMap = new HashMap<>();
        List<Property> addPropertyList = new ArrayList<>();
        // Get stop information
        Stops stopsList = flowDomain.getStopsById(id);
        // Get the StopsTemplate of the current stops
        List<StopsComponent> stopsComponentList =
            stopsComponentDomain.getStopsComponentByName(stopsList.getName());
        StopsComponent stopsComponent = null;
        List<StopsComponentProperty> propertiesTemplateList = null;
        if (null != stopsComponentList && !stopsComponentList.isEmpty()) {
            stopsComponent = stopsComponentList.get(0);
            logger.info("'stopsTemplateList' record number:" + stopsComponentList.size());
        }
        // Get the template attribute of 'StopsTemplate'
        if (null != stopsComponent) {
            propertiesTemplateList = stopsComponent.getProperties();
        }
        // Current 'stop' attribute
        List<Property> property = stopsList.getProperties();
        if (null != property && property.size() > 0)
            for (Property one : property) {
                PropertyMap.put(one.getName(), one);
            }
        // If the data of the template is larger than the current number of attributes of 'stop',
        // the same modification operation is performed, and the new 'stops' attribute is added.
        if (CollectionUtils.isNotEmpty(propertiesTemplateList)
            && CollectionUtils.isNotEmpty(property)) {
            for (StopsComponentProperty pt : propertiesTemplateList) {
                if (null == pt) {
                    continue;
                }
                Property ptname = PropertyMap.get(pt.getName());
                if (ptname != null) {
                    PropertyMap.remove(pt.getName());
                    Property update = new Property();
                    String name = ptname.getName();
                    Date crtDttm = ptname.getCrtDttm();
                    String crtUser = ptname.getCrtUser();
                    String displayName = pt.getDisplayName();
                    String description = pt.getDescription();
                    BeanUtils.copyProperties(pt, update);
                    update.setName(name);
                    update.setCrtDttm(crtDttm);
                    update.setCrtUser(crtUser);
                    update.setDisplayName(displayName);
                    update.setDescription(description);
                    update.setId(ptname.getId());
                    flowDomain.updateStopsProperty(update);
                } else {
                    logger.info(
                        "The 'stop' attribute is inconsistent with the template and needs to be added");
                    Property newProperty = new Property();
                    String displayName = pt.getDisplayName();
                    String description = pt.getDescription();
                    BeanUtils.copyProperties(pt, newProperty);
                    newProperty.setId(UUIDUtils.getUUID32());
                    newProperty.setCrtDttm(new Date());
                    newProperty.setCrtUser(username);
                    newProperty.setEnableFlag(true);
                    newProperty.setDisplayName(displayName);
                    newProperty.setDescription(description);
                    newProperty.setStops(stopsList);
                    addPropertyList.add(newProperty);
                }
            }
            if (addPropertyList.size() > 0) {
                flowDomain.addPropertyList(addPropertyList);
            }
            // All the changes in ‘objectPathsMap’ that need to be modified, left for logical deletion.
            if (PropertyMap.size() > 0)
                for (String pageId : PropertyMap.keySet()) {
                    Property deleteProperty = PropertyMap.get(pageId);
                    if (null == deleteProperty) {
                        continue;
                    }
                    logger.info(
                        "===============The 'stop' attribute is inconsistent with the template and needs to be deleted.=================");
                    flowDomain.deleteStopsPropertyById(deleteProperty.getId());
                }
        }
    }

    @Override
    public String saveOrUpdateRoutePath(String username, UpdatePathRequest updatePathRequest) {
        String[] checkFields = new String[]{"flowId", "pathLineId", "sourceId", "targetId"};
        if (CheckFiledUtils.checkObjSpecifiedFieldsIsNull(updatePathRequest, checkFields)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
        }
        String flowId = updatePathRequest.getFlowId();
        String pathLineId = updatePathRequest.getPathLineId();
        String sourceId = updatePathRequest.getSourceId();
        String sourcePortVal = updatePathRequest.getSourcePortVal();
        String targetId = updatePathRequest.getTargetId();
        String targetPortVal = updatePathRequest.getTargetPortVal();
        Stops sourceStop = null;
        Stops targetStop = null;
        List<Stops> queryInfoList =
            flowDomain.getStopsListByFlowIdAndPageIds(flowId, new String[]{sourceId, targetId});
        // If 'queryInfoList' is empty, or the size of 'queryInfoList' is less than 2, return directly
        if (null == queryInfoList || queryInfoList.size() < 2) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr("Can't find 'source' or 'target'");
        }
        // Loop out 'sourceStop' and 'targetStop'
        for (Stops stop : queryInfoList) {
            if (null == stop) {
                continue;
            }
            if (sourceId.equals(stop.getPageId())) {
                sourceStop = stop;
            } else if (targetId.equals(stop.getPageId())) {
                targetStop = stop;
            }
        }
        Paths currentPaths = null;
        List<Paths> pathsList = flowDomain.getPaths(flowId, pathLineId, null, null);
        if (null != pathsList && pathsList.size() == 1) {
            currentPaths = pathsList.get(0);
        }
        flowDomain.getPathsCounts(flowId, null, sourceId, null);
        if (updatePathRequest.isSourceRoute()) {
            if (PortType.ROUTE == sourceStop.getOutPortType()) {
                currentPaths.setFilterCondition(updatePathRequest.getSourceFilter());
                currentPaths.setOutport(updatePathRequest.getSourceFilter());
            }
        } else if (StringUtils.isNotBlank(sourcePortVal)) {
            currentPaths.setOutport(sourcePortVal);
            updatePropertyBypaths(username, sourcePortVal, sourceStop, "outports");
        }

        if (StringUtils.isNotBlank(targetPortVal)) {
            currentPaths.setInport(targetPortVal);
            updatePropertyBypaths(username, targetPortVal, targetStop, "inports");
        }
        currentPaths.setLastUpdateDttm(new Date());
        currentPaths.setLastUpdateUser("-1");
        int i = flowDomain.updatePaths(currentPaths);
        if (i <= 0) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
        }
        return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }

    /**
     * Modify the port attribute value of port type ‘any’ according to the port information of ‘paths’
     */
    private void updatePropertyBypaths(
                                       String username, String sourcePortVal, Stops stops, String propertyName) {
        if (null == stops) {
            return;
        }
        if (PortType.ANY != stops.getInPortType() && PortType.ANY != stops.getOutPortType()) {
            return;
        }
        List<Property> propertyList = stops.getProperties();
        if (null == propertyList || propertyList.size() == 0) {
            return;
        }
        String ports;
        Property propertySave = null;
        for (Property property : propertyList) {
            if (!propertyName.equals(property.getName())) {
                continue;
            }
            propertySave = property;
            break;
        }
        if (null == propertySave) {
            return;
        }
        if (null == propertySave.getCustomValue()) {
            ports = "";
        } else {
            ports = propertySave.getCustomValue();
        }
        if (StringUtils.isNotBlank(ports)) {
            ports = ports + ",";
        }
        flowDomain.updatePropertyCustomValue(username, (ports + sourcePortVal), propertySave.getId());
    }

    /** deleteLastReloadDataByStopsId */
    @Override
    public String deleteLastReloadDataByStopsId(String stopId) {
        int i = flowDomain.deletePropertiesByIsOldDataAndStopsId(stopId);
        if (i > 0) {
            return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.DELETE_SUCCEEDED_MSG());
        }
        return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DELETE_ERROR_MSG());
    }

    @Override
    public String updateStopDisabled(String username, Boolean isAdmin, String id, Boolean disabled) {
        if (StringUtils.isBlank(username)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
        }
        if (null == isAdmin) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
        }
        if (StringUtils.isBlank(id)) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("id"));
        }
        if (null == disabled) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("disabled"));
        }

        if (disabled) {
            List<String> publishingNames = flowStopsPublishingDomain.getPublishingNamesByStopsId(id);
            if (null != publishingNames && publishingNames.size() > 0) {
                return ReturnMapUtils.setFailedMsgRtnJsonStr(
                    MessageConfig.STOP_PUBLISHED_CANNOT_DISABLED_MSG(
                        publishingNames.toString().replace("[", "'").replace("]", "'")));
            }
        }
        Stops stops = flowDomain.getStopsById(id);
        if (null == stops) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
        }
        if (!isAdmin) {
            String crtUser = stops.getCrtUser();
            if (!username.equals(crtUser)) {
                return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_OPERATION_MSG());
            }
        }
        stops.setIsDisabled(disabled);
        try {
            int affectedRows = flowDomain.saveOrUpdate(stops);
            if (affectedRows <= 0) {
                return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
            }
        } catch (Exception e) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
        }
        return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }

    @Override
    public String previewCreateSql(String fid, String stopPageId) {
        if (StringUtils.isBlank(fid)) {
            throw new IllegalArgumentException(MessageConfig.PARAM_IS_NULL_MSG("fid"));
        }
        if (StringUtils.isBlank(stopPageId)) {
            throw new IllegalArgumentException(MessageConfig.PARAM_IS_NULL_MSG("stopPageId"));
        }
        Stops stops = flowDomain.getStopsByPageId(fid, stopPageId);
        if (null == stops) {
            throw new RuntimeException(MessageConfig.NO_DATA_MSG());
        }

        StopsComponent stopsComponentByBundle =
            stopsComponentDomain.getStopsComponentByBundle(stops.getBundle());
        StopsVo stopsVo = StopsUtils.stopPoToVo(stops, stopsComponentByBundle);
        if (null == stopsVo) {
            throw new RuntimeException(MessageConfig.NO_DATA_MSG());
        }

        return FlinkTableUtil.getDDL(stopsVo.getPropertiesVo());
    }
}