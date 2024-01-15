package org.apache.streampark.console.flow.component.stopsComponent.mapper.provider;

import org.apache.streampark.console.flow.base.utils.SqlUtils;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class StopsComponentGroupProvider {

  /** 查詢所有組 */
  public String getStopGroupList(@Param("engineType") String engineType) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops_groups");
    sql.WHERE("enable_flag = 1 and engine_type = '" + engineType + "' ");
    sql.ORDER_BY(" group_name ");
    sqlStr = sql.toString();
    return sqlStr;
  }

  public String getStopGroupByGroupNameList(
      @Param("groupName") List<String> groupName, @Param("engineType") String engineType) {
    return "select * from flow_stops_groups where group_name in ("
        + SqlUtils.strListToStr(groupName)
        + ") and enable_flag = 1 and engine_type = '"
        + engineType
        + "'";
  }
}
