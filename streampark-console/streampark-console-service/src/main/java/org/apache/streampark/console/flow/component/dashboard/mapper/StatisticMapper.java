package org.apache.streampark.console.flow.component.dashboard.mapper;

import org.apache.streampark.console.flow.component.dashboard.mapper.provider.StatisticProvider;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticMapper {

  /**
   * query flow progress statistic info
   *
   * @return statistic info map
   */
  @SelectProvider(type = StatisticProvider.class, method = "getFlowProcessStatisticInfo")
  List<Map<String, String>> getFlowProcessStatisticInfo();

  /**
   * query flow count
   *
   * @return flowCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getFlowCount")
  int getFlowCount();

  /**
   * query group progress statistic info
   *
   * @return statistic info map
   */
  @SelectProvider(type = StatisticProvider.class, method = "getGroupProcessStatisticInfo")
  List<Map<String, String>> getGroupProcessStatisticInfo();

  /**
   * query group count
   *
   * @return groupCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getGroupCount")
  int getGroupCount();

  /**
   * query schedule statistic info
   *
   * @return statistic info map
   */
  @SelectProvider(type = StatisticProvider.class, method = "getScheduleStatisticInfo")
  List<Map<String, String>> getScheduleStatisticInfo();

  /**
   * query template count
   *
   * @return templateCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getTemplateCount")
  int getTemplateCount();

  /**
   * query datasource count
   *
   * @return datasourceCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getDataSourceCount")
  int getDataSourceCount();

  /**
   * query stops hub count
   *
   * @return stopsHubCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getStopsHubCount")
  int getStopsHubCount();

  /**
   * query stops count
   *
   * @return stopsCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getStopsCount")
  int getStopsCount();

  /**
   * query stops group count
   *
   * @return stopsGroupCount
   */
  @SelectProvider(type = StatisticProvider.class, method = "getStopsGroupCount")
  int getStopsGroupCount();
}
