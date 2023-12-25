package org.apache.streampark.console.flow.component.sparkJar.service.impl;

import org.apache.streampark.console.flow.base.utils.FileUtils;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.Eunm.SparkJarState;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.component.sparkJar.domain.SparkJarDomain;
import org.apache.streampark.console.flow.component.sparkJar.entity.SparkJarComponent;
import org.apache.streampark.console.flow.component.sparkJar.service.ISparkJarService;
import org.apache.streampark.console.flow.component.sparkJar.utils.SparkJarUtils;
import org.apache.streampark.console.flow.third.service.ISparkJar;
import org.apache.streampark.console.flow.third.vo.sparkJar.SparkJarVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SparkJarServiceImpl implements ISparkJarService {

  private final SparkJarDomain sparkJarDomain;
  private final ISparkJar sparkJarImpl;

  @Autowired
  public SparkJarServiceImpl(SparkJarDomain sparkJarDomain, ISparkJar sparkJarImpl) {
    this.sparkJarDomain = sparkJarDomain;
    this.sparkJarImpl = sparkJarImpl;
  }

  @Override
  public String uploadSparkJarFile(String username, MultipartFile file) {

    // call piflow server api: sparkJar/path
    String sparkJarPath = sparkJarImpl.getSparkJarPath();

    // upload jar file to sparkJar path
    String sparkJarName = file.getOriginalFilename();

    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (file.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_FILE_EMPTY_MSG());
    }
    Map<String, Object> uploadMap = FileUtils.uploadRtnMap(file, sparkJarPath, sparkJarName);
    if (null == uploadMap || uploadMap.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }
    Integer code = (Integer) uploadMap.get("code");
    if (500 == code) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("failed to upload file");
    }

    // save stopsHub to db
    SparkJarComponent sparkJarComponent = SparkJarUtils.sparkJarNewNoId(username);
    sparkJarComponent.setId(UUIDUtils.getUUID32());
    sparkJarComponent.setJarName(sparkJarName);
    sparkJarComponent.setJarUrl(sparkJarPath + sparkJarName);
    sparkJarComponent.setStatus(SparkJarState.UNMOUNT);
    sparkJarDomain.addSparkJarComponent(sparkJarComponent);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("successful jar upload");
  }

  @Override
  public String mountSparkJar(String username, Boolean isAdmin, String id) {

    SparkJarComponent sparkJarComponent = sparkJarDomain.getSparkJarById(username, isAdmin, id);

    SparkJarVo sparkJarVo = sparkJarImpl.mountSparkJar(sparkJarComponent.getJarName());
    if (sparkJarVo == null) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Mount failed, please try again later");
    }

    sparkJarComponent.setMountId(sparkJarVo.getMountId());
    sparkJarComponent.setStatus(SparkJarState.MOUNT);
    sparkJarComponent.setLastUpdateUser(username);
    sparkJarComponent.setLastUpdateDttm(new Date());
    sparkJarDomain.updateSparkJarComponent(sparkJarComponent);

    return ReturnMapUtils.setSucceededMsgRtnJsonStr("Mount successful");
  }

  @Override
  public String unmountSparkJar(String username, Boolean isAdmin, String id) {

    SparkJarComponent sparkJarComponent = sparkJarDomain.getSparkJarById(username, isAdmin, id);
    if (sparkJarComponent.getStatus() == SparkJarState.UNMOUNT) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Spark jar have been UNMounted already!");
    }

    SparkJarVo sparkJarVo = sparkJarImpl.unmountSparkJar(sparkJarComponent.getMountId());
    if (sparkJarVo == null) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("UNMount failed, please try again later");
    }
    sparkJarComponent.setMountId(sparkJarVo.getMountId());
    sparkJarComponent.setStatus(SparkJarState.UNMOUNT);
    sparkJarComponent.setLastUpdateUser(username);
    sparkJarComponent.setLastUpdateDttm(new Date());
    sparkJarDomain.updateSparkJarComponent(sparkJarComponent);

    return ReturnMapUtils.setSucceededMsgRtnJsonStr("Unmount successful");
  }

  /**
   * sparkJarListPage
   *
   * @param username username
   * @param isAdmin is admin
   * @param pageNo Number of pages
   * @param limit Number each page
   * @param param Search content
   * @return
   */
  @Override
  public String sparkJarListPage(
      String username, Boolean isAdmin, Integer pageNo, Integer limit, String param) {
    if (null == pageNo || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Page<Process> page = PageHelper.startPage(pageNo, limit, "crt_dttm desc");
    sparkJarDomain.getSparkJarListParam(username, isAdmin, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * del spark jar
   *
   * @param username username
   * @param id id
   * @return json
   */
  @Override
  public String delSparkJar(String username, Boolean isAdmin, String id) {
    if (StringUtils.isBlank(id)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("id"));
    }
    SparkJarComponent sparkJarComponent = sparkJarDomain.getSparkJarById(username, isAdmin, id);
    if (null == sparkJarComponent) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    SparkJarState status = sparkJarComponent.getStatus();
    if (SparkJarState.MOUNT == status) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          "The status is MOUNT and deletion is prohibited ");
    }
    int i = sparkJarDomain.deleteSparkJarById(username, id);
    if (i <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DELETE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
  }
}
