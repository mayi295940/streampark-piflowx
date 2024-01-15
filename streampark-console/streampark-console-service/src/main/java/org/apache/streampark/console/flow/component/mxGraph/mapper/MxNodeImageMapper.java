package org.apache.streampark.console.flow.component.mxGraph.mapper;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.apache.streampark.console.flow.component.mxGraph.mapper.provider.MxNodeImageMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface MxNodeImageMapper {

  /**
   * add addMxNodeImage
   *
   * @param mxNodeImage mxNodeImage
   */
  @InsertProvider(type = MxNodeImageMapperProvider.class, method = "addMxNodeImage")
  int addMxNodeImage(MxNodeImage mxNodeImage);

  @SelectProvider(
      type = MxNodeImageMapperProvider.class,
      method = "userGetMxNodeImageListByImageType")
  List<MxNodeImage> userGetMxNodeImageListByImageType(String username, String imageType);
}
