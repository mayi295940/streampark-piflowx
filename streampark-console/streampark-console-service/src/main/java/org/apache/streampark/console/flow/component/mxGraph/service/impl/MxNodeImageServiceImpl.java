package org.apache.streampark.console.flow.component.mxGraph.service.impl;

import org.apache.streampark.console.flow.base.utils.FileUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.constant.Constants;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.mxGraph.domain.MxNodeImageDomain;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxNodeImageService;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxNodeImageUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxNodeImageVo;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MxNodeImageServiceImpl implements IMxNodeImageService {

  private final MxNodeImageDomain mxNodeImageDomain;

  @Autowired
  public MxNodeImageServiceImpl(MxNodeImageDomain mxNodeImageDomain) {
    this.mxNodeImageDomain = mxNodeImageDomain;
  }

  @Override
  public String uploadNodeImage(
      String username, MultipartFile file, String imageType, String nodeEngineType)
      throws Exception {

    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(imageType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("imageType is null");
    }
    if (file.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }

    String imagePath =
        Constants.ENGIN_FLINK.equalsIgnoreCase(nodeEngineType)
            ? SysParamsCache.ENGINE_FLINK_IMAGES_PATH
            : SysParamsCache.ENGINE_SPARK_IMAGES_PATH;

    Map<String, Object> uploadMap = FileUtils.uploadRtnMap(file, imagePath, null);
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
    MxNodeImage mxNodeImage = MxNodeImageUtils.newMxNodeImageNoId(username);
    mxNodeImage.setId(UUIDUtils.getUUID32());
    mxNodeImage.setImageName(fileName);
    mxNodeImage.setImagePath(path);
    mxNodeImage.setImageUrl("/images/" + saveFileName);
    mxNodeImage.setImageType(imageType);
    mxNodeImageDomain.addMxNodeImage(mxNodeImage);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("imgUrl", mxNodeImage.getImageUrl());
  }

  @Override
  public String getMxNodeImageList(String username, String imageType) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(imageType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("imageType is null");
    }
    List<MxNodeImageVo> mxNodeImageVoList = new ArrayList<>();
    List<MxNodeImage> mxNodeImageList =
        mxNodeImageDomain.userGetMxNodeImageListByImageType(username, imageType);
    if (null == mxNodeImageList || mxNodeImageList.size() == 0) {
      return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("nodeImageList", mxNodeImageList);
    }
    MxNodeImageVo mxNodeImageVo;
    for (MxNodeImage mxNodeImage : mxNodeImageList) {
      if (null == mxNodeImage) {
        continue;
      }
      mxNodeImageVo = new MxNodeImageVo();
      BeanUtils.copyProperties(mxNodeImage, mxNodeImageVo);
      mxNodeImageVo.setImageUrl(SysParamsCache.SYS_CONTEXT_PATH + mxNodeImage.getImageUrl());
      mxNodeImageVoList.add(mxNodeImageVo);
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("nodeImageList", mxNodeImageVoList);
  }
}
