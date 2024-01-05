package org.apache.streampark.console.flow.component.dataSource.mapper;

import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.dataSource.mapper.provider.DataSourceMapperProvider;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface DataSourceMapper {

  /**
   * add DataSource
   *
   * @param dataSource dataSource
   */
  @InsertProvider(type = DataSourceMapperProvider.class, method = "addDataSource")
  int addDataSource(DataSource dataSource);

  /**
   * update DataSource
   *
   * @param dataSource dataSource
   */
  @UpdateProvider(type = DataSourceMapperProvider.class, method = "updateDataSource")
  int updateDataSource(DataSource dataSource);

  /** query all DataSource */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceList")
  List<DataSource> getDataSourceList(String username, boolean isAdmin);

  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceListParam")
  List<DataSource> getDataSourceListParam(String username, boolean isAdmin, String param);

  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceListParam")
  List<DataSourceVo> getDataSourceVoListParam(String username, boolean isAdmin, String param);

  /** query all TemplateDataSource */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceTemplateList")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(
        column = "id",
        property = "dataSourcePropertyList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper.getDataSourcePropertyListByDataSourceId",
                fetchType = FetchType.LAZY))
  })
  List<DataSource> getDataSourceTemplateList();

  /**
   * query DataSource by DataSourceId
   *
   * @param id DataSourceId
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceByMap")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(
        column = "id",
        property = "dataSourcePropertyList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper.getDataSourcePropertyListByDataSourceId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "stops_template_bundle",
        property = "stopsComponent",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getDataSourceStopsComponentByBundle",
                fetchType = FetchType.LAZY))
  })
  DataSource getDataSourceByIdAndUser(@Param("username") String username,
                                      @Param("isAdmin") boolean isAdmin,
                                      @Param("id") String id);

  /**
   * query DataSource by DataSourceId
   *
   * @param id DataSourceId
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(
        column = "id",
        property = "dataSourcePropertyList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper.getDataSourcePropertyListByDataSourceId",
                fetchType = FetchType.LAZY))
  })
  DataSource getDataSourceById(String id);

  /**
   * query DataSource by DataSourceId
   *
   * @param id DataSourceId
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "adminGetDataSourceById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(
        column = "id",
        property = "dataSourcePropertyList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper.getDataSourcePropertyListByDataSourceId",
                fetchType = FetchType.LAZY))
  })
  DataSource adminGetDataSourceById(String id);

  @UpdateProvider(type = DataSourceMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String username, String id);

  @SelectProvider(type = DataSourceMapperProvider.class, method = "getStopDataSourceForFlowPage")
  @Results({
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(column = "name", property = "stopsName")
  })
  List<DataSourceVo> getStopDataSourceForFlowPage(String username, boolean isAdmin);

  /** Query "stop template bundle" of all "stop" data sources */
  @Select(
      "select DISTINCT(stops_template_bundle) from data_source where  stops_template_bundle is not null")
  List<String> getAllStopDataSourceBundle();

  /**
   * Change 'datasource' to available / unavailable
   *
   * @param bundle stop的bundle
   * @param isAvailable 1:available;0:unavailable
   * @author leilei
   */
  @Update(
      "update data_source set is_available = #{isAvailable} where stops_template_bundle = #{bundle}")
  int updateDataSourceIsAvailableByBundle(
      @Param("isAvailable") int isAvailable, @Param("bundle") String bundle);

  /**
   * Modify 'image url' of 'datasource'
   *
   * @param bundle bundle
   * @param imageUrl image url
   * @author leilei
   */
  @Update("update data_source set image_url = #{imageUrl} where stops_template_bundle = #{bundle}")
  int updateDataSourceImageUrlByBundle(String bundle, String imageUrl);

  /**
   * getDataSource Id by dataSourceName
   *
   * @param dataSourceName dataSourceName
   */
  @Select(
      "select id from data_source where data_source_name = #{dataSourceName} and id !=#{id} and is_template = 0 and enable_flag = 1 ")
  List<String> getDataSourceByDataSourceName(
      @Param("dataSourceName") String dataSourceName, @Param("id") String id);

  /**
   * query DataSource by DataSourceId
   *
   * @param id DataSourceId
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getDataSourceById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "stops_template_bundle", property = "stopsTemplateBundle"),
    @Result(
        column = "id",
        property = "dataSourcePropertyList",
        many =
            @Many(
                select =
                    "org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper.getDataSourcePropertyVoListByDataSourceId",
                fetchType = FetchType.LAZY))
  })
  DataSourceVo getDataSourceVoById(String id);
}
