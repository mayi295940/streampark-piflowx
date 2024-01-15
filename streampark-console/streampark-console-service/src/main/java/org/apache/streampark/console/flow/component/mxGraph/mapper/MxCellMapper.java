package org.apache.streampark.console.flow.component.mxGraph.mapper;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;
import org.apache.streampark.console.flow.component.mxGraph.mapper.provider.MxCellMapperProvider;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface MxCellMapper {

  /**
   * add mxCell
   *
   * @param mxCell mxCell
   */
  @InsertProvider(type = MxCellMapperProvider.class, method = "addMxCell")
  int addMxCell(MxCell mxCell);

  /**
   * update mxCell
   *
   * @param mxCell mxCell
   */
  @UpdateProvider(type = MxCellMapperProvider.class, method = "updateMxCell")
  int updateMxCell(MxCell mxCell);

  /**
   * Query MxCell's list based on mxGraphId
   *
   * @param mxGraphId mxGraphId
   */
  @SelectProvider(type = MxCellMapperProvider.class, method = "getMeCellByMxGraphId")
  @Results({
    @Result(column = "ID", property = "id"),
    @Result(column = "MX_PAGEID", property = "pageId"),
    @Result(column = "MX_PARENT", property = "parent"),
    @Result(column = "MX_STYLE", property = "style"),
    @Result(column = "MX_EDGE", property = "edge"),
    @Result(column = "MX_SOURCE", property = "source"),
    @Result(column = "MX_TARGET", property = "target"),
    @Result(column = "MX_VALUE", property = "value"),
    @Result(column = "MX_VERTEX", property = "vertex"),
    @Result(
        column = "id",
        property = "mxGeometry",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGeometryMapper.getMxGeometryByMxCellId",
                fetchType = FetchType.LAZY))
  })
  List<MxCell> getMeCellByMxGraphId(String mxGraphId);

  /**
   * Query MxCell based on Id
   *
   * @param id id
   */
  @SelectProvider(type = MxCellMapperProvider.class, method = "getMeCellById")
  @Results({
    @Result(column = "ID", property = "id"),
    @Result(column = "MX_PAGEID", property = "pageId"),
    @Result(column = "MX_PARENT", property = "parent"),
    @Result(column = "MX_STYLE", property = "style"),
    @Result(column = "MX_EDGE", property = "edge"),
    @Result(column = "MX_SOURCE", property = "source"),
    @Result(column = "MX_TARGET", property = "target"),
    @Result(column = "MX_VALUE", property = "value"),
    @Result(column = "MX_VERTEX", property = "vertex"),
    @Result(
        column = "id",
        property = "mxGeometry",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGeometryMapper.getMxGeometryByMxCellId",
                fetchType = FetchType.LAZY))
  })
  MxCell getMeCellById(String id);

  /**
   * Query MxCell based on mxGraphId and pageId
   *
   * @param mxGraphId mxGraphId
   * @param pageId pageId
   */
  @SelectProvider(type = MxCellMapperProvider.class, method = "getMxCellByMxGraphIdAndPageId")
  @Results({
    @Result(column = "ID", property = "id"),
    @Result(column = "MX_PAGEID", property = "pageId"),
    @Result(column = "MX_PARENT", property = "parent"),
    @Result(column = "MX_STYLE", property = "style"),
    @Result(column = "MX_EDGE", property = "edge"),
    @Result(column = "MX_SOURCE", property = "source"),
    @Result(column = "MX_TARGET", property = "target"),
    @Result(column = "MX_VALUE", property = "value"),
    @Result(column = "MX_VERTEX", property = "vertex"),
    @Result(
        column = "id",
        property = "mxGeometry",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.mxGraph.mapper.MxGeometryMapper.getMxGeometryByMxCellId",
                fetchType = FetchType.LAZY))
  })
  MxCell getMxCellByMxGraphIdAndPageId(String mxGraphId, String pageId);

  /**
   * Delete according to id logic, set to invalid
   *
   * @param username username
   * @param id id
   */
  @UpdateProvider(type = MxCellMapperProvider.class, method = "updateEnableFlagById")
  int updateMxCellEnableFlagById(String username, String id);

  /**
   * Delete 'MxCell' by 'mxGraphModelId'
   *
   * @param username username
   * @param mxGraphModelId mxGraphModelId
   */
  @UpdateProvider(type = MxCellMapperProvider.class, method = "deleteMxCellByFlowId")
  int deleteMxCellByFlowId(String username, String mxGraphModelId);

  /**
   * query max pageId by mxGraphModelId
   *
   * @param mxGraphModelId mxGraphModelId
   */
  @Select(
      "select MAX(s.mx_pageid+0) from mx_cell s "
          + "where s.enable_flag=1 "
          + "and s.fk_mx_graph_id=#{mxGraphModelId}")
  Integer getMaxPageIdByMxGraphModelId(@Param("mxGraphModelId") String mxGraphModelId);
}
