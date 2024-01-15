package org.apache.streampark.console.flow.component.dataSource.mapper;

import org.apache.streampark.console.flow.component.dataSource.entity.DataSourceProperty;
import org.apache.streampark.console.flow.component.dataSource.mapper.provider.DataSourcePropertyMapperProvider;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourcePropertyVo;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface DataSourcePropertyMapper {

  /**
   * Add a single DataSourceProperty
   *
   * @param dataSourceProperty dataSourceProperty
   */
  @InsertProvider(type = DataSourcePropertyMapperProvider.class, method = "addDataSourceProperty")
  int addDataSourceProperty(DataSourceProperty dataSourceProperty);

  /**
   * Insert list<datasourceproperty> note that the way to spell SQL must use a map to connect Param
   * content as a key value</datasourceproperty>
   *
   * @param dataSourcePropertyList dataSourcePropertyList
   */
  @InsertProvider(
      type = DataSourcePropertyMapperProvider.class,
      method = "addDataSourcePropertyList")
  int addDataSourcePropertyList(
      @Param("dataSourcePropertyList") List<DataSourceProperty> dataSourcePropertyList);

  /**
   * update dataSourceProperty
   *
   * @param dataSourceProperty dataSourceProperty
   */
  @UpdateProvider(
      type = DataSourcePropertyMapperProvider.class,
      method = "updateDataSourceProperty")
  int updateDataSourceProperty(DataSourceProperty dataSourceProperty);

  @SelectProvider(
      type = DataSourcePropertyMapperProvider.class,
      method = "getDataSourcePropertyListByDataSourceId")
  List<DataSourceProperty> getDataSourcePropertyListByDataSourceId(String dataSourceId);

  /**
   * Delete dataSourceProperty according to Id logic
   *
   * @param id id
   */
  @UpdateProvider(type = DataSourcePropertyMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String username, String id);

  /**
   * Delete the dataSourceProperty according to the datasourceId logic
   *
   * @param id id
   */
  @UpdateProvider(
      type = DataSourcePropertyMapperProvider.class,
      method = "updateEnableFlagByDatasourceId")
  int updateEnableFlagByDatasourceId(String username, String id);

  @SelectProvider(
      type = DataSourcePropertyMapperProvider.class,
      method = "getDataSourcePropertyListByDataSourceId")
  List<DataSourcePropertyVo> getDataSourcePropertyVoListByDataSourceId(String dataSourceId);
}
