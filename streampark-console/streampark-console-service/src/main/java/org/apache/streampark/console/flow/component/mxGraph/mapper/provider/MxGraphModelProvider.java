package org.apache.streampark.console.flow.component.mxGraph.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;

public class MxGraphModelProvider {

  private String id;
  private int enableFlag;
  private String lastUpdateDttmStr;
  private String lastUpdateUser;
  private Long version;
  private String dx;
  private String dy;
  private String grid;
  private String gridSize;
  private String guides;
  private String tooltips;
  private String connect;
  private String arrows;
  private String fold;
  private String page;
  private String pageScale;
  private String pageWidth;
  private String pageHeight;
  private String background;
  private String flowId;
  private String flowGroupId;
  private String processId;
  private String processGroupId;

  private boolean preventSQLInjectionMxGraphModel(MxGraphModel mxGraphModel) {
    if (null == mxGraphModel || StringUtils.isBlank(mxGraphModel.getLastUpdateUser())) {
      return false;
    }
    // Mandatory Field
    String id = mxGraphModel.getId();
    String lastUpdateUser = mxGraphModel.getLastUpdateUser();
    Boolean enableFlag = mxGraphModel.getEnableFlag();
    Long version = mxGraphModel.getVersion();
    Date lastUpdateDttm = mxGraphModel.getLastUpdateDttm();
    String lastUpdateDttmStr =
        DateUtils.dateTimesToStr(null != lastUpdateDttm ? lastUpdateDttm : new Date());
    this.id = SqlUtils.preventSQLInjection(id);
    this.lastUpdateUser = SqlUtils.preventSQLInjection(lastUpdateUser);
    this.enableFlag = ((null != enableFlag && enableFlag) ? 1 : 0);
    this.version = (null != version ? version : 0L);

    this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttmStr);

    // Selection field
    this.dx = SqlUtils.preventSQLInjection(mxGraphModel.getDx());
    this.dy = SqlUtils.preventSQLInjection(mxGraphModel.getDy());
    this.grid = SqlUtils.preventSQLInjection(mxGraphModel.getGrid());
    this.gridSize = SqlUtils.preventSQLInjection(mxGraphModel.getGridSize());
    this.guides = SqlUtils.preventSQLInjection(mxGraphModel.getGuides());
    this.tooltips = SqlUtils.preventSQLInjection(mxGraphModel.getTooltips());
    this.connect = SqlUtils.preventSQLInjection(mxGraphModel.getConnect());
    this.arrows = SqlUtils.preventSQLInjection(mxGraphModel.getArrows());
    this.fold = SqlUtils.preventSQLInjection(mxGraphModel.getFold());
    this.page = SqlUtils.preventSQLInjection(mxGraphModel.getPage());
    this.pageScale = SqlUtils.preventSQLInjection(mxGraphModel.getPageScale());
    this.pageWidth = SqlUtils.preventSQLInjection(mxGraphModel.getPageWidth());
    this.pageHeight = SqlUtils.preventSQLInjection(mxGraphModel.getPageHeight());
    this.background = SqlUtils.preventSQLInjection(mxGraphModel.getBackground());
    String flowIdStr = (null != mxGraphModel.getFlow() ? mxGraphModel.getFlow().getId() : null);
    this.flowId = SqlUtils.preventSQLInjection(flowIdStr);
    String flowGroupIdStr =
        (null != mxGraphModel.getFlowGroup() ? mxGraphModel.getFlowGroup().getId() : null);
    this.flowGroupId = SqlUtils.preventSQLInjection(flowGroupIdStr);
    String processIdStr =
        (null != mxGraphModel.getProcess() ? mxGraphModel.getProcess().getId() : null);
    this.processId = SqlUtils.preventSQLInjection(processIdStr);
    String processGroupIdStr =
        (null != mxGraphModel.getProcessGroup() ? mxGraphModel.getProcessGroup().getId() : null);
    this.processGroupId = SqlUtils.preventSQLInjection(processGroupIdStr);
    return true;
  }

  private void reset() {
    this.id = null;
    this.lastUpdateDttmStr = null;
    this.lastUpdateUser = null;
    this.enableFlag = 1;
    this.version = 0L;
    this.dx = null;
    this.dy = null;
    this.grid = null;
    this.gridSize = null;
    this.guides = null;
    this.tooltips = null;
    this.connect = null;
    this.arrows = null;
    this.fold = null;
    this.page = null;
    this.pageScale = null;
    this.pageWidth = null;
    this.pageHeight = null;
    this.background = null;
    this.flowId = null;
    this.flowGroupId = null;
    this.processId = null;
    this.processGroupId = null;
  }

  public String addMxGraphModel(MxGraphModel mxGraphModel) {
    String sqlStr = "SELECT 0";
    boolean flag = this.preventSQLInjectionMxGraphModel(mxGraphModel);
    if (flag) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("INSERT INTO mx_graph_model");
      stringBuffer.append("(");
      stringBuffer.append(SqlUtils.baseFieldName() + ",");
      stringBuffer.append("mx_dx,");
      stringBuffer.append("mx_dy,");
      stringBuffer.append("mx_grid,");
      stringBuffer.append("mx_gridsize,");
      stringBuffer.append("mx_guides,");
      stringBuffer.append("mx_tooltips,");
      stringBuffer.append("mx_connect,");
      stringBuffer.append("mx_arrows,");
      stringBuffer.append("mx_fold,");
      stringBuffer.append("mx_page,");
      stringBuffer.append("mx_pagescale,");
      stringBuffer.append("mx_pagewidth,");
      stringBuffer.append("mx_pageheight,");
      stringBuffer.append("mx_background,");
      stringBuffer.append("fk_flow_id,");
      stringBuffer.append("fk_flow_group_id,");
      stringBuffer.append("fk_process_id,");
      stringBuffer.append("fk_process_group_id");
      stringBuffer.append(") ");
      stringBuffer.append("VALUES");
      stringBuffer.append("(");
      stringBuffer.append(SqlUtils.baseFieldValues(mxGraphModel) + ",");
      stringBuffer.append(dx + ",");
      stringBuffer.append(dy + ",");
      stringBuffer.append(grid + ",");
      stringBuffer.append(gridSize + ",");
      stringBuffer.append(guides + ",");
      stringBuffer.append(tooltips + ",");
      stringBuffer.append(connect + ",");
      stringBuffer.append(arrows + ",");
      stringBuffer.append(fold + ",");
      stringBuffer.append(page + ",");
      stringBuffer.append(pageScale + ",");
      stringBuffer.append(pageWidth + ",");
      stringBuffer.append(pageHeight + ",");
      stringBuffer.append(background + ",");
      stringBuffer.append(flowId + ",");
      stringBuffer.append(flowGroupId + ",");
      stringBuffer.append(processId + ",");
      stringBuffer.append(processGroupId + " ");
      stringBuffer.append(")");
      sqlStr = stringBuffer.toString();
    }
    this.reset();
    return sqlStr;
  }

  public String updateMxGraphModel(MxGraphModel mxGraphModel) {
    String sqlStr = "";
    this.preventSQLInjectionMxGraphModel(mxGraphModel);
    if (null != mxGraphModel) {
      if (StringUtils.isNotBlank(id)) {
        SQL sql = new SQL();

        // INSERT_INTO brackets is table name
        sql.UPDATE("mx_graph_model");
        // The first string in the SET is the name of the field corresponding to the table in the
        // database
        // all types except numeric fields must be enclosed in single quotes

        // Process the required fields first
        sql.SET("last_update_dttm = " + lastUpdateDttmStr);
        sql.SET("last_update_user = " + lastUpdateUser);
        sql.SET("version = " + (version + 1));

        // handle other fields
        sql.SET("enable_flag = " + enableFlag);
        sql.SET("mx_dx = " + dx);
        sql.SET("mx_dy = " + dy);
        sql.SET("mx_grid = " + grid);
        sql.SET("mx_gridsize = " + gridSize);
        sql.SET("mx_guides = " + guides);
        sql.SET("mx_tooltips = " + tooltips);
        sql.SET("mx_connect = " + connect);
        sql.SET("mx_arrows = " + arrows);
        sql.SET("mx_fold = " + fold);
        sql.SET("mx_page = " + page);
        sql.SET("mx_pagescale = " + pageScale);
        sql.SET("mx_pagewidth = " + pageWidth);
        sql.SET("mx_pageheight = " + pageHeight);
        sql.SET("mx_background = " + background);
        if (!"null".equals(flowId)) {
          sql.SET("fk_flow_id = " + flowId);
        }
        if (!"null".equals(flowGroupId)) {
          sql.SET("fk_flow_group_id = " + flowGroupId);
        }
        if (!"null".equals(processId)) {
          sql.SET("fk_process_id = " + processId);
        }
        if (!"null".equals(processGroupId)) {
          sql.SET("fk_process_group_id = " + processGroupId);
        }
        sql.WHERE("version = " + version);
        sql.WHERE("id = " + id);
        sqlStr = sql.toString();
        if (StringUtils.isBlank(id)) {
          sqlStr = "";
        }
      }
    }
    this.reset();
    return sqlStr;
  }

  /**
   * Query mxGraphModel based on id
   *
   * @param id
   * @return
   */
  public String getMxGraphModelById(String id) {
    String sqlStr = "select * from mx_graph_model where id = #{id}";
    if (StringUtils.isNotBlank(id)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("mx_graph_model");
      sql.WHERE("id = " + SqlUtils.preventSQLInjection(id));
      sql.WHERE("enable_flag = 1");
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query mxGraphModel according to flowId
   *
   * @param flowId
   * @return
   */
  public String getMxGraphModelByFlowId(String flowId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(flowId)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("mx_graph_model");
      sql.WHERE("fk_flow_id = " + SqlUtils.preventSQLInjection(flowId));
      sql.WHERE("enable_flag = 1");
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query mxGraphModel according to flowId
   *
   * @param flowGroupId
   * @return
   */
  public String getMxGraphModelByFlowGroupId(String flowGroupId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(flowGroupId)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("mx_graph_model");
      sql.WHERE("fk_flow_group_id = " + SqlUtils.preventSQLInjection(flowGroupId));
      sql.WHERE("enable_flag = 1");
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query mxGraphModel according to processId
   *
   * @param processId
   * @return
   */
  public String getMxGraphModelByProcessId(String processId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(processId)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("mx_graph_model");
      sql.WHERE("fk_process_id = " + SqlUtils.preventSQLInjection(processId));
      sql.WHERE("enable_flag = 1");
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * Query mxGraphModel according to processGroupId
   *
   * @param processGroupId
   * @return
   */
  public String getMxGraphModelByProcessGroupId(String processGroupId) {
    String sqlStr = "SELECT 0";
    if (StringUtils.isNotBlank(processGroupId)) {
      SQL sql = new SQL();
      sql.SELECT("*");
      sql.FROM("mx_graph_model");
      sql.WHERE("fk_process_group_id = " + SqlUtils.preventSQLInjection(processGroupId));
      sql.WHERE("enable_flag = 1");
      sqlStr = sql.toString();
    }
    return sqlStr;
  }

  /**
   * delete 'MxGraphModel' by 'flowId'
   *
   * @param username
   * @param flowId
   * @return
   */
  public String deleteMxGraphModelEnableFlagByFlowId(String username, String flowId) {
    if (StringUtils.isBlank(username)) {
      return "SELECT 0";
    }
    if (StringUtils.isBlank(flowId)) {
      return "SELECT 0";
    }
    SQL sql = new SQL();
    sql.UPDATE("mx_graph_model");
    sql.SET("enable_flag = 0");
    sql.SET("last_update_user = " + SqlUtils.preventSQLInjection(username));
    sql.SET(
        "last_update_dttm = " + SqlUtils.preventSQLInjection(DateUtils.dateTimesToStr(new Date())));
    sql.WHERE("enable_flag = 1");
    sql.WHERE("fk_flow_id = " + SqlUtils.preventSQLInjection(flowId));

    return sql.toString();
  }
}
