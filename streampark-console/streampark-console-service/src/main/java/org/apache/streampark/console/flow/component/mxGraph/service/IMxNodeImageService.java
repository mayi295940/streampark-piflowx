package org.apache.streampark.console.flow.component.mxGraph.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMxNodeImageService {

  String uploadNodeImage(
      String username, MultipartFile file, String imageType, String nodeEngineType)
      throws Exception;

  String getMxNodeImageList(String username, String imageType);
}
