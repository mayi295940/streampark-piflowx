package org.apache.streampark.console.flow.component.mxGraph.mapper;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;
import org.apache.streampark.console.flow.component.mxGraph.mapper.provider.MxGraphModelProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface MxGraphModelMapper {

  /**
   * add mxGraph Model
   *
   * @param mxGraphModel mxGraph Model
   */
  @InsertProvider(type = MxGraphModelProvider.class, method = "addMxGraphModel")
  int addMxGraphModel(MxGraphModel mxGraphModel);

  /**
   * Modify mxgraph model
   *
   * @param mxGraphModel mxGraph Model
   */
  @UpdateProvider(type = MxGraphModelProvider.class, method = "updateMxGraphModel")
  int updateMxGraphModel(MxGraphModel mxGraphModel);

  /**
   * Query mxgraph model according to ID
   *
   * @param id id
   */
  @SelectProvider(type = MxGraphModelProvider.class, method = "getMxGraphModelById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "mx_dx", property = "dx"),
    @Result(column = "mx_dy", property = "dy"),
    @Result(column = "mx_grid", property = "grid"),
    @Result(column = "mx_gridsize", property = "gridSize"),
    @Result(column = "mx_guides", property = "guides"),
    @Result(column = "mx_tooltips", property = "tooltips"),
    @Result(column = "mx_connect", property = "connect"),
    @Result(column = "mx_arrows", property = "arrows"),
    @Result(column = "mx_fold", property = "fold"),
    @Result(column = "mx_page", property = "page"),
    @Result(column = "mx_pagescale", property = "pageScale"),
    @Result(column = "mx_pagewidth", property = "pageWidth"),
    @Result(column = "mx_pageheight", property = "pageHeight"),
    @Result(column = "mx_background", property = "background"),
    @Result(
        column = "id",
        property = "root",
        many =
            @Many(
                select = "cn.cnic.component.mxGraph.mapper.MxCellMapper.getMeCellByMxGraphId",
                fetchType = FetchType.LAZY))
  })
  MxGraphModel getMxGraphModelById(String id);

  /**
   * Query mxGraphModel according to flowId
   *
   * @param flowId flowId
   */
  @SelectProvider(type = MxGraphModelProvider.class, method = "getMxGraphModelByFlowId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "mx_dx", property = "dx"),
    @Result(column = "mx_dy", property = "dy"),
    @Result(column = "mx_grid", property = "grid"),
    @Result(column = "mx_gridsize", property = "gridSize"),
    @Result(column = "mx_guides", property = "guides"),
    @Result(column = "mx_tooltips", property = "tooltips"),
    @Result(column = "mx_connect", property = "connect"),
    @Result(column = "mx_arrows", property = "arrows"),
    @Result(column = "mx_fold", property = "fold"),
    @Result(column = "mx_page", property = "page"),
    @Result(column = "mx_pagescale", property = "pageScale"),
    @Result(column = "mx_pagewidth", property = "pageWidth"),
    @Result(column = "mx_pageheight", property = "pageHeight"),
    @Result(column = "mx_background", property = "background"),
    @Result(
        column = "id",
        property = "root",
        many =
            @Many(
                select = "cn.cnic.component.mxGraph.mapper.MxCellMapper.getMeCellByMxGraphId",
                fetchType = FetchType.LAZY))
  })
  MxGraphModel getMxGraphModelByFlowId(String flowId);

  /**
   * Query mxGraphModel according to flowGroupId
   *
   * @param flowGroupId flowGroupId
   */
  @SelectProvider(type = MxGraphModelProvider.class, method = "getMxGraphModelByFlowGroupId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "mx_dx", property = "dx"),
    @Result(column = "mx_dy", property = "dy"),
    @Result(column = "mx_grid", property = "grid"),
    @Result(column = "mx_gridsize", property = "gridSize"),
    @Result(column = "mx_guides", property = "guides"),
    @Result(column = "mx_tooltips", property = "tooltips"),
    @Result(column = "mx_connect", property = "connect"),
    @Result(column = "mx_arrows", property = "arrows"),
    @Result(column = "mx_fold", property = "fold"),
    @Result(column = "mx_page", property = "page"),
    @Result(column = "mx_pagescale", property = "pageScale"),
    @Result(column = "mx_pagewidth", property = "pageWidth"),
    @Result(column = "mx_pageheight", property = "pageHeight"),
    @Result(column = "mx_background", property = "background"),
    @Result(
        column = "id",
        property = "root",
        many =
            @Many(
                select = "cn.cnic.component.mxGraph.mapper.MxCellMapper.getMeCellByMxGraphId",
                fetchType = FetchType.LAZY))
  })
  MxGraphModel getMxGraphModelByFlowGroupId(String flowGroupId);

  /**
   * Query mxGraphModel according to flowGroupId
   *
   * @param processId processId
   */
  @SelectProvider(type = MxGraphModelProvider.class, method = "getMxGraphModelByProcessId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "mx_dx", property = "dx"),
    @Result(column = "mx_dy", property = "dy"),
    @Result(column = "mx_grid", property = "grid"),
    @Result(column = "mx_gridsize", property = "gridSize"),
    @Result(column = "mx_guides", property = "guides"),
    @Result(column = "mx_tooltips", property = "tooltips"),
    @Result(column = "mx_connect", property = "connect"),
    @Result(column = "mx_arrows", property = "arrows"),
    @Result(column = "mx_fold", property = "fold"),
    @Result(column = "mx_page", property = "page"),
    @Result(column = "mx_pagescale", property = "pageScale"),
    @Result(column = "mx_pagewidth", property = "pageWidth"),
    @Result(column = "mx_pageheight", property = "pageHeight"),
    @Result(column = "mx_background", property = "background"),
    @Result(
        column = "id",
        property = "root",
        many =
            @Many(
                select = "cn.cnic.component.mxGraph.mapper.MxCellMapper.getMeCellByMxGraphId",
                fetchType = FetchType.LAZY))
  })
  MxGraphModel getMxGraphModelByProcessId(String processId);

  /**
   * Query mxGraphModel according to processGroupId
   *
   * @param processGroupId processGroupId
   */
  @SelectProvider(type = MxGraphModelProvider.class, method = "getMxGraphModelByProcessGroupId")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "mx_dx", property = "dx"),
    @Result(column = "mx_dy", property = "dy"),
    @Result(column = "mx_grid", property = "grid"),
    @Result(column = "mx_gridsize", property = "gridSize"),
    @Result(column = "mx_guides", property = "guides"),
    @Result(column = "mx_tooltips", property = "tooltips"),
    @Result(column = "mx_connect", property = "connect"),
    @Result(column = "mx_arrows", property = "arrows"),
    @Result(column = "mx_fold", property = "fold"),
    @Result(column = "mx_page", property = "page"),
    @Result(column = "mx_pagescale", property = "pageScale"),
    @Result(column = "mx_pagewidth", property = "pageWidth"),
    @Result(column = "mx_pageheight", property = "pageHeight"),
    @Result(column = "mx_background", property = "background"),
    @Result(
        column = "id",
        property = "root",
        many =
            @Many(
                select = "cn.cnic.component.mxGraph.mapper.MxCellMapper.getMeCellByMxGraphId",
                fetchType = FetchType.LAZY))
  })
  MxGraphModel getMxGraphModelByProcessGroupId(String processGroupId);

  /**
   * delete 'MxGraphModel' by 'flowId'
   *
   * @param username username
   * @param flowId flowId
   */
  @UpdateProvider(
      type = MxGraphModelProvider.class,
      method = "deleteMxGraphModelEnableFlagByFlowId")
  int deleteMxGraphModelEnableFlagByFlowId(String username, String flowId);

}
