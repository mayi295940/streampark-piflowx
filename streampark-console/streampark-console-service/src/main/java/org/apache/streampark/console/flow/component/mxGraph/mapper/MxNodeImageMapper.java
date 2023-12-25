package org.apache.streampark.console.flow.component.mxGraph.mapper;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.apache.streampark.console.flow.component.mxGraph.mapper.provider.MxNodeImageMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

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
