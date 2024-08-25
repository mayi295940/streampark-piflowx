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

package org.apache.streampark.console.flow.third.service.impl;

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.common.Eunm.ComponentFileType;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.process.domain.ProcessDomain;
import org.apache.streampark.console.flow.component.process.entity.Process;
import org.apache.streampark.console.flow.component.process.entity.ProcessStop;
import org.apache.streampark.console.flow.component.process.utils.ProcessUtils;
import org.apache.streampark.console.flow.component.stopsComponent.domain.StopsComponentDomain;
import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsComponent;
import org.apache.streampark.console.flow.third.service.IFlow;
import org.apache.streampark.console.flow.third.utils.ThirdFlowInfoVoUtils;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoVo;
import org.apache.streampark.console.flow.third.vo.flow.ThirdProgressVo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FlowImpl implements IFlow {

    private final Logger logger = LoggerUtil.getLogger();

    private final ProcessDomain processDomain;
    private final StopsComponentDomain stopsComponentDomain;

    @Autowired
    public FlowImpl(ProcessDomain processDomain, StopsComponentDomain stopsComponentDomain) {
        this.processDomain = processDomain;
        this.stopsComponentDomain = stopsComponentDomain;
    }

    @Override
    public Map<String, Object> startFlow(
                                         Process process, String checkpoint, RunModeType runModeType) {
        if (null == process) {
            return ReturnMapUtils.setFailedMsg(MessageConfig.PARAM_ERROR_MSG());
        }
        List<ProcessStop> processStopList = process.getProcessStopList();
        if (processStopList == null || processStopList.size() == 0) {
            return ReturnMapUtils.setFailedMsg(MessageConfig.PARAM_IS_NULL_MSG("Stop"));
        } else {
            for (ProcessStop processStop : processStopList) {
                StopsComponent stops =
                    stopsComponentDomain.getOnlyStopsComponentByBundle(processStop.getBundle());
                if (stops == null) {
                    return ReturnMapUtils.setFailedMsg(MessageConfig.DATA_ERROR_MSG());
                }
                processStop.setComponentType(stops.getComponentType());
                if (ComponentFileType.PYTHON == processStop.getComponentType()) {
                    processStop.setDockerImagesName(stops.getDockerImagesName());
                }
            }
        }

        String formatJson =
            ProcessUtils.processToJson(
                process, checkpoint, runModeType, process.getFlowGlobalParamsList());
        String doPost = HttpUtils.doPost(ApiConfig.getFlowStartUrl(), formatJson, null);
        if (StringUtils.isBlank(doPost)) {
            return ReturnMapUtils.setFailedMsg(
                "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
        }
        if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Exception")) {
            return ReturnMapUtils.setFailedMsg(MessageConfig.INTERFACE_CALL_ERROR_MSG() + " : " + doPost);
        }
        try {
            // Convert a json string to a json object
            JSONObject obj = JSONObject.parseObject(doPost).getJSONObject("flow");
            String appId = obj.getString("id");
            if (StringUtils.isBlank(appId)) {
                return ReturnMapUtils.setFailedMsg(
                    "Error : " + MessageConfig.INTERFACE_RETURN_VALUE_IS_NULL_MSG());
            }
            return ReturnMapUtils.setSucceededCustomParam("appId", appId);
        } catch (Exception e) {
            logger.error("error: ", e);
            return ReturnMapUtils.setFailedMsg(MessageConfig.CONVERSION_FAILED_MSG());
        }
    }

    @Override
    public String getProcessJson(Process process, String checkpoint, RunModeType runModeType) {
        if (null == process) {
            throw new RuntimeException(MessageConfig.PARAM_ERROR_MSG());
        }
        List<ProcessStop> processStopList = process.getProcessStopList();
        if (processStopList == null || processStopList.size() == 0) {
            throw new RuntimeException(MessageConfig.PARAM_IS_NULL_MSG("Stop"));
        } else {
            for (ProcessStop processStop : processStopList) {
                StopsComponent stops =
                    stopsComponentDomain.getOnlyStopsComponentByBundle(processStop.getBundle());
                if (stops == null) {
                    throw new RuntimeException(MessageConfig.DATA_ERROR_MSG());
                }
                processStop.setComponentType(stops.getComponentType());
                if (ComponentFileType.PYTHON == processStop.getComponentType()) {
                    processStop.setDockerImagesName(stops.getDockerImagesName());
                }
            }
        }

        return ProcessUtils.processToJson(
            process, checkpoint, runModeType, process.getFlowGlobalParamsList());
    }

    @Override
    public String stopFlow(String appId) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appId);
        String json = JSONObject.toJSONString(map);
        String doPost = HttpUtils.doPost(ApiConfig.getFlowStopUrl(), json, 5 * 1000);
        if (StringUtils.isBlank(doPost)
            || doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())
            || doPost.contains("Exception")) {
            logger.warn("Interface return exception : " + doPost);
        } else {
            logger.info("Interface return value: " + doPost);
        }
        return doPost;
    }

    /** send post request */
    @Override
    public ThirdProgressVo getFlowProgress(String appId) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appId);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowProgressUrl(), map, 10 * 1000);
        if (StringUtils.isBlank(doGet)
            || doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())
            || doGet.contains("Exception")) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doGet);
            return null;
        }
        String jsonResult = JSONObject.parseObject(doGet).getString("FlowInfo");
        if (StringUtils.isNotBlank(jsonResult)) {
            return null;
        }
        // Also convert the json string to a json object,
        // and then convert the json object to a java object,
        // as shown below.
        // Convert a json object to a java object
        ThirdProgressVo jd = JSONObject.parseObject(jsonResult, ThirdProgressVo.class);
        String progressNums = jd.getProgress();
        if (StringUtils.isNotBlank(progressNums)) {
            try {
                double progressNumsD = Double.parseDouble(progressNums);
                jd.setProgress(String.format("%.2f", progressNumsD));
            } catch (Throwable e) {
                logger.warn("Progress conversion failed");
            }
        }
        return jd;
    }

    /** send post request */
    @Override
    public String getFlowLog(String appId) {
        // ThirdFlowLog thirdFlowLog = null;
        Map<String, String> map = new HashMap<>();
        map.put("appID", appId);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowLogUrl(), map, 5 * 1000);
        if (StringUtils.isBlank(doGet)) {
            logger.info("call failed, return is null ");
            return "";
        }
        if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
            logger.info("call failed : " + doGet);
            return "";
        }
        logger.info("Successful call : " + doGet);
        // Also convert the json string to a json object,
        // and then convert the json object to a java object,
        // as shown below.

        // Convert a json string to a json object
        JSONObject obj = JSONObject.parseObject(doGet);
        if (null == obj) {
            return "";
        }
        JSONObject app = obj.getJSONObject("app");
        if (null == app) {
            return "";
        }
        // Convert a json object to a java object
        // thirdFlowLog = (ThirdFlowLog) JSONObject.toBean(obj, ThirdFlowLog.class);
        // return thirdFlowLog;
        return app.getString("amContainerLogs");
    }

    /** send get */
    @Override
    public String getCheckpoints(String appID) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appID);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowCheckpointsUrl(), map, 5 * 1000);
        if (StringUtils.isBlank(doGet)) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + " return is null ");
            return null;
        }
        if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doGet);
            return null;
        }
        // Also convert the json string to a json object,
        // and then convert the json object to a java object,
        // as shown below.

        // Convert a json string to a json object
        JSONObject obj = JSONObject.parseObject(doGet);
        if (null == obj) {
            return null;
        }
        return obj.getString("checkpoints");
    }

    @Override
    public String getDebugData(String appID, String stopName, String portName) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appID);
        map.put("stopName", stopName);
        map.put("port", portName);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowDebugDataUrl(), map, 5 * 1000);
        logger.info("call succeeded : " + doGet);
        if (StringUtils.isBlank(doGet)) {
            return MessageConfig.INTERFACE_CALL_ERROR_MSG() + " return is null ";
        }
        if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
            return MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doGet;
        }
        // Also convert the json string to a json object,
        // and then convert the json object to a java object,
        // as shown below.
        // JSONObject obj = JSONObject.fromObject(doGet);// Convert a json string to a json object
        // if (null != obj) {
        // jb = obj.getString("checkpoints");
        // }
        return doGet;
    }

    @Override
    public String getVisualizationData(String appID, String stopName, String visualizationType) {
        Map<String, String> map = new HashMap<>();
        map.put("appID", appID);
        map.put("stopName", stopName);
        map.put("visualizationType", visualizationType);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowVisualizationDataUrl(), map, 5 * 1000);
        logger.info("call succeeded : " + doGet);
        if (StringUtils.isBlank(doGet)) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + " return is null ");
            return null;
        }
        if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doGet);
            return null;
        }
        return doGet;
    }

    /** Send post request */
    @SuppressWarnings("rawtypes")
    @Override
    public ThirdFlowInfoVo getFlowInfo(String appId) {
        ThirdFlowInfoVo jb;
        Map<String, String> map = new HashMap<>();
        map.put("appID", appId);
        String doGet = HttpUtils.doGet(ApiConfig.getFlowInfoUrl(), map, 30 * 1000);
        if (StringUtils.isBlank(doGet)) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + " return is null ");
            return null;
        }
        if (doGet.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doGet.contains("Exception")) {
            logger.warn(MessageConfig.INTERFACE_CALL_ERROR_MSG() + ": " + doGet);
            return null;
        }
        // Also convert the json string to a json object,
        // and then convert the json object to a java object,
        // as shown below.

        // Convert a json string to a json object
        JSONObject obj = JSONObject.parseObject(doGet).getJSONObject("flow");
        jb = obj.toJavaObject(ThirdFlowInfoVo.class);
        String progressNums = jb.getProgress();
        if (StringUtils.isNotBlank(progressNums)) {
            try {
                double progressNumsD = Double.parseDouble(progressNums);
                jb.setProgress(String.format("%.2f", progressNumsD));
            } catch (Throwable e) {
                logger.warn("Progress conversion failed");
            }
        }
        return jb;
    }

    @Override
    public void getProcessInfoAndSave(String appId) throws Exception {
        ThirdFlowInfoVo thirdFlowInfoVo = getFlowInfo(appId);
        // Determine if the progress returned by the interface is empty
        if (null != thirdFlowInfoVo) {
            List<Process> processList = processDomain.getProcessNoGroupByAppId(appId);
            for (Process process : processList) {
                process = ThirdFlowInfoVoUtils.setProcess(process, thirdFlowInfoVo);
                if (null != process) {
                    processDomain.saveOrUpdate(process);
                }
            }
        }
    }

    @Override
    public void processInfoAndSaveSync() throws Exception {
        List<String> runningProcess = processDomain.getRunningProcessAppId();
        if (CollectionUtils.isEmpty(runningProcess)) {
            return;
        }
        for (String appId : runningProcess) {
            getProcessInfoAndSave(appId);
        }
    }

    @Override
    public String getTestDataPathUrl() {
        return HttpUtils.doGet(ApiConfig.getTestDataPathUrl(), null, null);
    }
}
