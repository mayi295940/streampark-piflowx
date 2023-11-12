package org.apache.streampark.console.flow.component.mxGraph.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.streampark.console.flow.base.util.FileUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.ReturnMapUtils;
import org.apache.streampark.console.flow.base.util.UUIDUtils;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.apache.streampark.console.flow.component.mxGraph.jpa.domain.MxNodeImageDomain;
import org.apache.streampark.console.flow.component.mxGraph.service.IMxNodeImageService;
import org.apache.streampark.console.flow.component.mxGraph.utils.MxNodeImageUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxNodeImageVo;

@Service
public class MxNodeImageServiceImpl implements IMxNodeImageService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private MxNodeImageDomain mxNodeImageDomain;

  @Override
  public String uploadNodeImage(String username, MultipartFile file, String imageType) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("illegal user");
    }
    if (StringUtils.isBlank(imageType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("imageType is null");
    }
    if (file.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Upload failed, please try again later");
    }
    Map<String, Object> uploadMap = FileUtils.uploadRtnMap(file, SysParamsCache.IMAGES_PATH, null);
    if (null == uploadMap || uploadMap.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Upload failed, please try again later");
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
    mxNodeImageDomain.saveOrUpdate(mxNodeImage);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("imgUrl", mxNodeImage.getImageUrl());
  }

  @Override
  public String getMxNodeImageList(String username, String imageType) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("illegal user");
    }
    if (StringUtils.isBlank(imageType)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("imageType is null");
    }
    List<MxNodeImageVo> mxNodeImageVoList = new ArrayList<>();
    List<MxNodeImage> mxNodeImageList =
        mxNodeImageDomain.userGetMxNodeImageListByImageType(username, imageType);
    if (null == mxNodeImageList || mxNodeImageList.size() <= 0) {
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
