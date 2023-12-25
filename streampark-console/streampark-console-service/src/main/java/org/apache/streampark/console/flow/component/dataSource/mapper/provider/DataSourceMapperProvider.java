package org.apache.streampark.console.flow.component.dataSource.mapper.provider;

import java.util.Date;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.base.utils.SqlUtils;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;

public class DataSourceMapperProvider {

  private String id;
  private String lastUpdateDttmStr;
  private String lastUpdateUser;
  private int enableFlag;
  private long version;
  private String dataSourceType;
  private String dataSourceName;
  private String dataSourceDescription;
  private Integer isTemplate;
  private String stopsTemplateBundle;
  private int isAvailable;
  private String imageUrl;

  private boolean preventSQLInjectionDataSource(DataSource dataSource) {
    if (null == dataSource || StringUtils.isBlank(dataSource.getLastUpdateUser())) {
      return false;
    }
    // Mandatory Field
    Long version = dataSource.getVersion();
    Boolean enableFlag = dataSource.getEnableFlag();
    Date lastUpdateDttm = dataSource.getLastUpdateDttm();
    String lastUpdateDttmStr =
        DateUtils.dateTimesToStr(null != lastUpdateDttm ? lastUpdateDttm : new Date());
    this.id = SqlUtils.preventSQLInjection(dataSource.getId());
    this.lastUpdateUser = SqlUtils.preventSQLInjection(dataSource.getLastUpdateUser());
    this.enableFlag = ((null != enableFlag && enableFlag) ? 1 : 0);
    this.version = (null != version ? version : 0L);
    this.lastUpdateDttmStr = SqlUtils.preventSQLInjection(lastUpdateDttmStr);

    // Selection field
    this.dataSourceType = SqlUtils.preventSQLInjection(dataSource.getDataSourceType());
    this.dataSourceName = SqlUtils.preventSQLInjection(dataSource.getDataSourceName());
    this.dataSourceDescription =
        SqlUtils.preventSQLInjection(dataSource.getDataSourceDescription());
    this.isTemplate =
        (null == dataSource.getIsTemplate() ? 0 : (dataSource.getIsTemplate() ? 1 : 0));
    this.stopsTemplateBundle = SqlUtils.preventSQLInjection(dataSource.getStopsTemplateBundle());
    this.isAvailable =
        (null == dataSource.getIsAvailable() ? 0 : (dataSource.getIsAvailable() ? 1 : 0));
    this.imageUrl = SqlUtils.preventSQLInjection(dataSource.getImageUrl());
    return true;
  }

  private void reset() {
    this.id = null;
    this.lastUpdateDttmStr = null;
    this.lastUpdateUser = null;
    this.enableFlag = 1;
    this.version = 0L;
    this.dataSourceType = null;
    this.dataSourceName = null;
    this.dataSourceDescription = null;
    this.isTemplate = null;
    this.stopsTemplateBundle = null;
    this.imageUrl = null;
  }

  public String addDataSource(DataSource dataSource) {
    String sqlStr = "SELECT 0";
    boolean flag = this.preventSQLInjectionDataSource(dataSource);
    if (flag) {
      sqlStr =
          "INSERT INTO data_source "
              + "( "
              + SqlUtils.baseFieldName()
              + ", "
              + "is_template, "
              + "data_source_type, "
              + "data_source_name, "
              + "data_source_description, "
              + "stops_template_bundle, "
              + "is_available, "
              + "image_url "
              + ") "
              + "VALUES "
              + "( "
              + SqlUtils.baseFieldValues(dataSource)
              + ", "
              + isTemplate
              + ", "
              + dataSourceType
              + ", "
              + dataSourceName
              + ", "
              + dataSourceDescription
              + ", "
              + stopsTemplateBundle
              + ", "
              + isAvailable
              + ", "
              + imageUrl
              + " "
              + ") ";
    }
    this.reset();
    return sqlStr;
  }

  public String updateDataSource(DataSource dataSource) {

    String sqlStr = "";
    boolean flag = this.preventSQLInjectionDataSource(dataSource);
    if (flag) {
      SQL sql = new SQL();

      // INSERT_INTO brackets is table name
      sql.UPDATE("data_source");
      // The first string in the SET is the name of the field corresponding to the table in the
      // database
      sql.SET("last_update_dttm = " + lastUpdateDttmStr);
      sql.SET("last_update_user = " + lastUpdateUser);
      sql.SET("version = " + (version + 1));

      // handle other fields
      sql.SET("enable_flag = " + enableFlag);
      sql.SET("data_source_type = " + dataSourceType);
      sql.SET("data_source_name = " + dataSourceName);
      sql.SET("data_source_description = " + dataSourceDescription);
      sql.SET("stops_template_bundle = " + stopsTemplateBundle);
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

  /** Query all data sources */
  public String getDataSourceList(String username, boolean isAdmin) {
    String sqlStr;
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("select * ");
    strBuf.append("from data_source ");
    strBuf.append("where enable_flag = 1 ");
    strBuf.append("and is_template = 0 and stops_template_bundle is null ");
    if (!isAdmin) {
      strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
    }
    strBuf.append("order by crt_dttm desc ");
    sqlStr = strBuf.toString();
    return sqlStr;
  }

  /** Query all data sources */
  public String getDataSourceListParam(String username, boolean isAdmin, String param) {
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("select * ");
    strBuf.append("from data_source ");
    strBuf.append("where enable_flag = 1 ");
    strBuf.append("and is_template = 0 ");
    if (StringUtils.isNotBlank(param)) {
      strBuf.append("and ( ");
      strBuf
          .append("data_source_name LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");

      strBuf
          .append("or data_source_type LIKE CONCAT('%',")
          .append(SqlUtils.preventSQLInjection(param))
          .append(",'%') ");
      strBuf.append(") ");
    }
    if (!isAdmin) {
      strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
    }
    strBuf.append("order by crt_dttm desc ");
    return strBuf.toString();
  }

  /** Query all template data sources */
  public String getDataSourceTemplateList() {
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("data_source");
    sql.WHERE("enable_flag = 1");
    sql.WHERE("is_template = 1");
    sql.ORDER_BY(" data_source_type asc  ");
    return sql.toString();
  }

  /** Query the data source according to the workflow id */
  public String getDataSourceByIdAndUser(String username, boolean isAdmin, String id) {
    String sqlStr = "";
    if (StringUtils.isNotBlank(id)) {
      StringBuilder strBuf = new StringBuilder();
      strBuf.append("select * ");
      strBuf.append("from data_source ");
      strBuf.append("where enable_flag = 1 ");
      strBuf.append("and id = ").append(SqlUtils.preventSQLInjection(id)).append(" ");
      if (!isAdmin) {
        strBuf.append("and crt_user = ").append(SqlUtils.preventSQLInjection(username));
      }
      sqlStr = strBuf.toString();
    }
    return sqlStr;
  }

  /**
   * Query the data source according to the workflow id
   *
   * @param param param
   */
  public String getDataSourceByMap(Map<String, Object> param) {
    String id = MapUtils.getString(param, "id");
    String sqlStr = "";
    if (StringUtils.isNotBlank(id)) {
      sqlStr =
          "select * "
              + "from data_source "
              + "where enable_flag = 1 "
              + "and id = "
              + SqlUtils.preventSQLInjection(id)
              + " ";
    }
    return sqlStr;
  }

  /**
   * Query the data source according to the workflow id
   *
   * @param id id
   */
  public String getDataSourceById(String id) {
    String sqlStr = "";
    if (StringUtils.isNotBlank(id)) {
      sqlStr =
          "select * "
              + "from data_source "
              + "where enable_flag = 1 "
              + "and id = "
              + SqlUtils.preventSQLInjection(id)
              + " ";
    }
    return sqlStr;
  }

  /** Query the data source according to the workflow id */
  public String adminGetDataSourceById(String id) {
    String sqlStr = "";
    if (StringUtils.isNotBlank(id)) {
      sqlStr =
          "select * "
              + "from data_source "
              + "where enable_flag = 1 "
              + "and id = "
              + SqlUtils.preventSQLInjection(id)
              + " ";
    }
    return sqlStr;
  }

  /** Delete according to id logic, set to invalid */
  public String updateEnableFlagById(String username, String id) {
    if (StringUtils.isBlank(username)) {
      return "SELECT 0";
    }
    if (StringUtils.isBlank(id)) {
      return "SELECT 0";
    }
    SQL sql = new SQL();
    sql.UPDATE("data_source");
    sql.SET("enable_flag = 0");
    sql.SET("last_update_user = " + SqlUtils.preventSQLInjection(username));
    sql.SET(
        "last_update_dttm = " + SqlUtils.preventSQLInjection(DateUtils.dateTimesToStr(new Date())));
    sql.WHERE("enable_flag = 1");
    sql.WHERE("is_template = 0");
    sql.WHERE("id = " + SqlUtils.preventSQLInjection(id));

    return sql.toString();
  }

  public String getStopDataSourceForFlowPage(String username, Boolean isAdmin) {
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("select ds.*,st.name ");
    strBuf.append("from data_source ds ");
    strBuf.append("join flow_stops_template st on ds.stops_template_bundle = st.bundle ");
    strBuf.append("where ds.enable_flag = 1 ");
    strBuf.append("and ds.is_template = 0 ");
    strBuf.append("and ds.stops_template_bundle is not null ");
    if (!isAdmin) {
      strBuf.append("and ds.crt_user = ").append(SqlUtils.preventSQLInjection(username));
    }
    strBuf.append(" order by ds.crt_dttm desc ");
    return strBuf.toString();
  }
}
