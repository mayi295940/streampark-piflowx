package org.apache.streampark.console.flow.component.mxGraph.mapper;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;
import org.apache.streampark.console.flow.component.mxGraph.mapper.provider.MxGeometryMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
public interface MxGeometryMapper {

  /**
   * add MxGeometry
   *
   * @param mxGeometry mxGeometry
   */
  @InsertProvider(type = MxGeometryMapperProvider.class, method = "addMxGeometry")
  int addMxGeometry(MxGeometry mxGeometry);

  /**
   * update MxGeometry
   *
   * @param mxGeometry mxGeometry
   */
  @UpdateProvider(type = MxGeometryMapperProvider.class, method = "updateMxGeometry")
  int updateMxGeometry(MxGeometry mxGeometry);

  /**
   * Query MxGeometry based on id
   *
   * @param id id
   */
  @SelectProvider(type = MxGeometryMapperProvider.class, method = "getMxGeometryById")
  @Results({
    @Result(column = "mx_relative", property = "relative"),
    @Result(column = "mx_as", property = "as"),
    @Result(column = "mx_x", property = "x"),
    @Result(column = "mx_y", property = "y"),
    @Result(column = "mx_width", property = "width"),
    @Result(column = "mx_height", property = "height")
  })
  MxGeometry getMxGeometryById(String id);

  /**
   * Query MxGeometry based on flowId
   *
   * @param mxCellId mxCellId
   */
  @SelectProvider(type = MxGeometryMapperProvider.class, method = "getMxGeometryByMxCellId")
  @Results({
    @Result(column = "mx_relative", property = "relative"),
    @Result(column = "mx_as", property = "as"),
    @Result(column = "mx_x", property = "x"),
    @Result(column = "mx_y", property = "y"),
    @Result(column = "mx_width", property = "width"),
    @Result(column = "mx_height", property = "height")
  })
  MxGeometry getMxGeometryByMxCellId(String mxCellId);

  /**
   * Delete 'MxGeometry' by 'mxCellId'
   *
   * @param username username
   * @param mxCellId mxCellId
   */
  @UpdateProvider(type = MxGeometryMapperProvider.class, method = "deleteMxGeometryByFlowId")
  int deleteMxGeometryByFlowId(String username, String mxCellId);
}
