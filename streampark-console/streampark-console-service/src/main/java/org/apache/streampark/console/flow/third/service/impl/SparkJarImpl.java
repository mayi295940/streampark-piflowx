package org.apache.streampark.console.flow.third.service.impl;

import org.apache.streampark.console.flow.base.utils.HttpUtils;
import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.common.constant.ApiConfig;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.third.service.ISparkJar;
import org.apache.streampark.console.flow.third.vo.sparkJar.SparkJarVo;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SparkJarImpl implements ISparkJar {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private Logger logger = LoggerUtil.getLogger();

  @Override
  public String getSparkJarPath() {

    Map<String, String> map = new HashMap<>();
    // map.put("bundle", bundleStr);
    String sendGetData = HttpUtils.doGet(ApiConfig.getSparkJarPathUrl(), map, 30 * 1000);
    logger.info("return msg：" + sendGetData);
    if (StringUtils.isBlank(sendGetData)) {
      logger.warn("Interface return value is null");
      return null;
    }
    if (sendGetData.contains("Error")
        || sendGetData.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG())) {
      logger.warn("return err: " + sendGetData);
      return null;
    }

    String sparkJarPath = JSONObject.fromObject(sendGetData).getString("sparkJarPath");
    return sparkJarPath;
  }

  @Override
  public SparkJarVo mountSparkJar(String sparkjarName) {

    Map<String, String> map = new HashMap<>();
    map.put("sparkJar", sparkjarName);
    String json = JSONObject.fromObject(map).toString();
    String doPost = HttpUtils.doPost(ApiConfig.getSparkJarMountUrl(), json, 5 * 1000);
    if (StringUtils.isBlank(doPost)) {
      logger.warn("Interface return values is null");
      return null;
    }
    if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Fail")) {
      logger.warn("Interface return exception: " + doPost);
      return null;
    }
    logger.info("Interface return value: " + doPost);
    SparkJarVo sparkJarVo = constructSparkJarVo(JSONObject.fromObject(doPost));
    return sparkJarVo;
  }

  @Override
  public SparkJarVo unmountSparkJar(String sparkJarMountId) {

    Map<String, String> map = new HashMap<>();
    map.put("sparkJarId", sparkJarMountId);
    String json = JSONObject.fromObject(map).toString();
    String doPost = HttpUtils.doPost(ApiConfig.getSparkJarUNMountUrl(), json, 5 * 1000);
    if (StringUtils.isBlank(doPost)) {
      logger.warn("Interface return values is null");
      return null;
    }
    if (doPost.contains(MessageConfig.INTERFACE_CALL_ERROR_MSG()) || doPost.contains("Fail")) {
      logger.warn("Interface return exception : " + doPost);
      return null;
    }
    logger.info("Interface return value: " + doPost);
    SparkJarVo sparkJarVo = constructSparkJarVo(JSONObject.fromObject(doPost));
    return sparkJarVo;
  }

  private SparkJarVo constructSparkJarVo(JSONObject jsonObject) {

    SparkJarVo sparkJarVo = new SparkJarVo();
    String sparkJarMountId = jsonObject.getJSONObject("sparkJar").getString("id");
    sparkJarVo.setMountId(sparkJarMountId);
    return sparkJarVo;
  }
}
