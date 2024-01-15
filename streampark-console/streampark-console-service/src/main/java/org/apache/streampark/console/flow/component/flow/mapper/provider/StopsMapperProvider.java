package org.apache.streampark.console.flow.component.flow.mapper.provider;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.flow.entity.Stops;
import org.apache.streampark.console.flow.third.vo.flow.ThirdFlowInfoStopVo;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StopsMapperProvider {

  private String id;
  private String lastUpdateDttmStr;
  private String lastUpdateUser;
  private int enableFlag;
  private long version;
  private String bundle;
  private String description;
  private String groups;
  private String name;
  private String engineType;
  private String inports;
  private String inPortType;
  private String outports;
  private String outPortType;
  private String owner;
  private String pageId;
  private Integer checkpoint;
  private int isCustomized;
  private String flowId;
  private String dataSourceId;
  private int isDataSource;
  private int isDisabled;

  private boolean preventSQLInjectionStops(Stops stops) {
    if (null == stops || StringUtils.isBlank(stops.getLastUpdateUser())) {
      return false;
    }
    // Mandatory Field
    String id = stops.getId();
    String lastUpdateUser = stops.getLastUpdateUser();
    Boolean enableFlag = stops.getEnableFlag();
    Long version = stops.getVersion();
    Date lastUpdateDttm = stops.getLastUpdateDttm();
    this.id = SqlUtils.preventSQLInjection(id);
    this.lastUpdateUser = SqlUtils.preventSQLInjection(lastUpdateUser);
    this.enableFlag = ((null != enableFlag && enableFlag) ? 1 : 0);
    this.version = (null != version ? version : 0L);
    String lastUpdateDttmStr =
        DateUtils.dateTimesToStr(null != lastUpdateDttm ? lastUpdateDttm : new Date());
    this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttmStr);

    // Selection field
    this.bundle = SqlUtils.preventSQLInjection(stops.getBundle());
    this.description = SqlUtils.preventSQLInjection(stops.getDescription());
    this.groups = SqlUtils.preventSQLInjection(stops.getGroups());
    this.name = SqlUtils.preventSQLInjection(stops.getName());
    this.engineType = SqlUtils.preventSQLInjection(stops.getEngineType());
    this.inports = SqlUtils.preventSQLInjection(stops.getInports());
    this.inPortType =
        SqlUtils.preventSQLInjection(
            null != stops.getInPortType() ? stops.getInPortType().name() : null);
    this.outports = SqlUtils.preventSQLInjection(stops.getOutports());
    this.outPortType =
        SqlUtils.preventSQLInjection(
            null != stops.getOutPortType() ? stops.getOutPortType().name() : null);
    this.owner = SqlUtils.preventSQLInjection(stops.getOwner());
    this.pageId = SqlUtils.preventSQLInjection(stops.getPageId());
    this.checkpoint = ((null != stops.getIsCheckpoint() && stops.getIsCheckpoint()) ? 1 : 0);
    this.isCustomized = ((null != stops.getIsCustomized() && stops.getIsCustomized()) ? 1 : 0);
    String flowIdStr = (null != stops.getFlow() ? stops.getFlow().getId() : null);
    this.flowId = (null != flowIdStr ? SqlUtils.preventSQLInjection(flowIdStr) : null);
    String dataSourceIdStr = (null != stops.getDataSource() ? stops.getDataSource().getId() : null);
    this.dataSourceId =
        (null != dataSourceIdStr ? SqlUtils.preventSQLInjection(dataSourceIdStr) : null);
    this.isDataSource = ((null != stops.getIsDataSource() && stops.getIsDataSource()) ? 1 : 0);
    this.isDisabled = ((null != stops.getIsDisabled() && stops.getIsDisabled()) ? 1 : 0);
    return true;
  }

  private void reset() {
    this.id = null;
    this.lastUpdateDttmStr = null;
    this.lastUpdateUser = null;
    this.enableFlag = 1;
    this.version = 0L;
    this.bundle = null;
    this.description = null;
    this.groups = null;
    this.name = null;
    this.engineType = null;
    this.inports = null;
    this.inPortType = null;
    this.outports = null;
    this.outPortType = null;
    this.owner = null;
    this.pageId = null;
    this.checkpoint = null;
    this.isCustomized = 0;
    this.flowId = null;
    this.dataSourceId = null;
    this.isDataSource = 0;
    this.isDisabled = 0;
  }

  private StringBuffer splicingInsert() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("INSERT INTO ");
    stringBuffer.append("flow_stops ");
    stringBuffer.append("(");
    stringBuffer.append(SqlUtils.baseFieldName());
    stringBuffer.append(",");
    stringBuffer.append("bundle,");
    stringBuffer.append("description,");
    stringBuffer.append("`groups`,");
    stringBuffer.append("name,");
    stringBuffer.append("engine_type,");
    stringBuffer.append("inports,");
    stringBuffer.append("in_port_type,");
    stringBuffer.append("outports,");
    stringBuffer.append("out_port_type,");
    stringBuffer.append("owner,");
    stringBuffer.append("page_id,");
    stringBuffer.append("is_checkpoint,");
    stringBuffer.append("is_customized,");
    stringBuffer.append("fk_flow_id,");
    stringBuffer.append("fk_data_source_id,");
    stringBuffer.append("is_data_source,");
    stringBuffer.append("is_disabled");
    stringBuffer.append(") ");
    stringBuffer.append("VALUES");
    return stringBuffer;
  }

  /**
   * add Stops
   *
   * @param stops stops
   */
  public String addStops(Stops stops) {
    String sqlStr = "SELECT 0";
    boolean flag = this.preventSQLInjectionStops(stops);
    if (flag) {
      StringBuffer stringBuffer = splicingInsert();
      stringBuffer.append("(");
      stringBuffer.append(SqlUtils.baseFieldValues(stops)).append(",");
      stringBuffer.append(this.bundle).append(",");
      stringBuffer.append(this.description).append(",");
      stringBuffer.append(this.groups).append(",");
      stringBuffer.append(this.name).append(",");
      stringBuffer.append(this.engineType).append(",");
      stringBuffer.append(this.inports).append(",");
      stringBuffer.append(this.inPortType).append(",");
      stringBuffer.append(this.outports).append(",");
      stringBuffer.append(this.outPortType).append(",");
      stringBuffer.append(this.owner).append(",");
      stringBuffer.append(this.pageId).append(",");
      stringBuffer.append(this.checkpoint).append(",");
      stringBuffer.append(this.isCustomized).append(",");
      stringBuffer.append(this.flowId).append(",");
      stringBuffer.append(this.dataSourceId).append(",");
      stringBuffer.append(this.isDataSource).append(",");
      stringBuffer.append(this.isDisabled);
      stringBuffer.append(")");
      sqlStr = stringBuffer.toString();
    }
    this.reset();
    return sqlStr;
  }

  /**
   * Insert list<Stops> Note that the method of spelling sql must use Map to connect Param content
   * to key value.
   *
   * @param map (Content: The key is stopsList and the value is List<Stops>)
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public String addStopsList(Map map) {
    List<Stops> stopsList = (List<Stops>) map.get("stopsList");
    if (null == stopsList || stopsList.size() == 0) {
      return "SELECT 0";
    }
    StringBuffer sql = splicingInsert();
    int i = 0;
    for (Stops stops : stopsList) {
      i++;
      boolean flag = this.preventSQLInjectionStops(stops);
      if (flag) {
        sql.append("(");
        sql.append(SqlUtils.baseFieldValues(stops)).append(",");
        // handle other fields
        sql.append(bundle).append(",");
        sql.append(description).append(",");
        sql.append(groups).append(",");
        sql.append(name).append(",");
        sql.append(engineType).append(",");
        sql.append(inports).append(",");
        sql.append(inPortType).append(",");
        sql.append(outports).append(",");
        sql.append(outPortType).append(",");
        sql.append(owner).append(",");
        sql.append(pageId).append(",");
        sql.append(checkpoint).append(",");
        sql.append(isCustomized).append(",");
        sql.append(flowId).append(",");
        sql.append(dataSourceId).append(",");
        sql.append(this.isDataSource).append(",");
        sql.append(this.isDisabled);
        if (i != stopsList.size()) {
          sql.append("),");
        } else {
          sql.append(")");
        }
      }
      this.reset();
    }
    sql.append(";");
    return sql.toString();
  }

  /**
   * update stops
   *
   * @param stops stops
   */
  public String updateStops(Stops stops) {
    String sqlStr = "";
    boolean flag = this.preventSQLInjectionStops(stops);
    if (flag) {
      SQL sql = new SQL();

      sql.UPDATE("flow_stops");

      sql.SET("last_update_dttm = " + lastUpdateDttmStr);
      sql.SET("last_update_user = " + lastUpdateUser);
      sql.SET("version = " + (version + 1));

      // handle other fields
      sql.SET("enable_flag = " + enableFlag);
      sql.SET("bundle = " + bundle);
      sql.SET("description = " + description);
      sql.SET("`groups` = " + groups);
      sql.SET("name = " + name);
      sql.SET("engine_type = " + engineType);
      sql.SET("inports = " + inports);
      sql.SET("in_port_type = " + inPortType);
      sql.SET("outports = " + outports);
      sql.SET("out_port_type = " + outPortType);
      sql.SET("owner = " + owner);
      sql.SET("is_checkpoint = " + checkpoint);
      sql.SET("fk_data_source_id = " + dataSourceId);
      sql.SET("is_data_source =" + isDataSource);
      sql.SET("is_disabled =" + isDisabled);
      sql.WHERE("version = " + version);
      sql.WHERE("id = " + id);
      sqlStr = sql.toString();
      if (StringUtils.isBlank(id)) {
        sqlStr = "";
      }
    }
    this.reset();
    return sqlStr;
  }

  /** Query all stops data */
  public String getStopsList() {
    return "SELECT * FROM flow_stops WHERE enable_flag=1";
  }

  /**
   * Query StopsList based on flowId
   *
   * @param flowId flow id
   */
  public String getStopsListByFlowId(String flowId) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("fk_flow_id = " + SqlUtils.preventSQLInjection(flowId));
    sqlStr = sql.toString();
    return sqlStr;
  }

  /** Query StopsList based on flowId */
  @SuppressWarnings("rawtypes")
  public String getStopsListByFlowIdAndPageIds(Map map) {
    String flowId = (String) map.get("flowId");
    String[] pageIds = (String[]) map.get("pageIds");
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("fk_flow_id = " + SqlUtils.preventSQLInjection(flowId));
    if (null != pageIds && pageIds.length > 0) {
      StringBuffer pageIdsStr = new StringBuffer();
      pageIdsStr.append("( ");
      for (int i = 0; i < pageIds.length; i++) {
        String pageId = pageIds[i];
        pageIdsStr.append("page_id = " + SqlUtils.preventSQLInjection(pageId));
        if (i < pageIds.length - 1) {
          pageIdsStr.append(" OR ");
        }
      }
      pageIdsStr.append(")");
      sql.WHERE(pageIdsStr.toString());
    }
    sqlStr = sql.toString();
    return sqlStr;
  }

  /**
   * Query according to stopsId
   *
   * @param Id id
   */
  public String getStopsById(String Id) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("id = " + SqlUtils.preventSQLInjection(Id));
    sqlStr = sql.toString();
    return sqlStr;
  }

  /**
   * Modify the stop status information according to flowId and name
   *
   * @param stopVo stopVo
   */
  public String updateStopsByFlowIdAndName(ThirdFlowInfoStopVo stopVo) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.UPDATE("flow_stops");
    Date endTime = DateUtils.strCstToDate(stopVo.getEndTime());
    Date startTime = DateUtils.strCstToDate(stopVo.getStartTime());
    String name = stopVo.getName();
    String state = stopVo.getState();
    String flowId = stopVo.getFlowId();
    if (null != endTime) {
      sql.SET("stop_time = " + SqlUtils.preventSQLInjection(DateUtils.dateTimeToStr(endTime)));
    }
    if (StringUtils.isNotBlank(state)) {
      sql.SET("state = " + SqlUtils.preventSQLInjection(state));
    }
    if (null != startTime) {
      sql.SET("start_time = " + SqlUtils.preventSQLInjection(DateUtils.dateTimeToStr(startTime)));
    }
    sql.WHERE("fk_flow_id = " + SqlUtils.preventSQLInjection(flowId));
    sql.WHERE("name = " + SqlUtils.preventSQLInjection(name));
    sqlStr = sql.toString();
    return sqlStr;
  }

  public String updateStopEnableFlagByFlowId(String username, String id) {
    if (StringUtils.isBlank(username)) {
      return "SELECT 0";
    }
    if (StringUtils.isBlank(id)) {
      return "SELECT 0";
    }
    SQL sql = new SQL();
    sql.UPDATE("flow_stops");
    sql.SET("enable_flag=0");
    sql.SET("last_update_user=" + SqlUtils.preventSQLInjection(username));
    sql.SET(
        "last_update_dttm=" + SqlUtils.preventSQLInjection(DateUtils.dateTimesToStr(new Date())));
    sql.WHERE("enable_flag = 0");
    sql.WHERE("fk_flow_id=" + SqlUtils.preventSQLInjection(id));

    return sql.toString();
  }

  /**
   * Query disabled StopsName list by ids
   *
   * @param Ids Ids
   */
  public String getDisabledStopsNameListByIds(List<String> Ids) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("name");
    sql.FROM("flow_stops");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("is_disabled = 1");
    sql.WHERE("id IN (" + SqlUtils.strListToStr(Ids) + ")");
    sqlStr = sql.toString();
    return sqlStr;
  }

  /** Query disabled StopsName list by ids */
  public String getCannotPublishedStopsNameByIds(List<String> Ids) {
    /**
     * SELECT fs.`name` FROM flow_stops fs LEFT JOIN flow_stops_property fsp ON
     * fs.id=fsp.fk_stops_id WHERE fs.enable_flag = 1 and fs.is_disabled = 0 and fs.is_customized=0
     * and fsp.id IS NULL and fs.id IN
     * ('1fc963ca8264471a8de8ca1d3c2d8586','cbe7efad8ad04cb3827e554f0065de16','399c6aab3fe441538edf939549cdd4ba','fa27e1932d37410886e14d7995f57319');
     */
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("fs.`name`");
    sql.FROM("flow_stops fs");
    sql.LEFT_OUTER_JOIN("flow_stops_property fsp ON fs.id=fsp.fk_stops_id");
    sql.WHERE("fs.enable_flag = 1");
    sql.WHERE("fs.is_disabled = 0");
    sql.WHERE("fs.is_customized = 0");
    sql.WHERE("fsp.id IS NULL");
    sql.WHERE("fs.id IN ( " + SqlUtils.strListToStr(Ids) + ")");
    sqlStr = sql.toString();
    return sqlStr;
  }

  /** Query Stops Bind Datasource list by ids */
  public String getStopsBindDatasourceByIds(List<String> Ids) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("fk_data_source_id IS NOT NULL");
    sql.WHERE("id IN ( " + SqlUtils.strListToStr(Ids) + ")");
    sqlStr = sql.toString();
    return sqlStr;
  }
}
