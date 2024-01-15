package org.apache.streampark.console.flow.component.testData.mapper;

import org.apache.streampark.console.flow.component.testData.entity.TestDataSchemaValues;
import org.apache.streampark.console.flow.component.testData.mapper.provider.TestDataSchemaValuesMapperProvider;

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

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface TestDataSchemaValuesMapper {

  /**
   * add TestDataSchemaValues
   *
   * @param testDataSchemaValues testDataSchemaValues
   */
  @InsertProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "addTestDataSchemaValues")
  Integer addTestDataSchemaValues(TestDataSchemaValues testDataSchemaValues);

  /**
   * add TestDataSchemaValues list
   *
   * @param testDataSchemaValuesList testDataSchemaValuesList
   */
  @InsertProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "addTestDataSchemaValuesList")
  Integer addTestDataSchemaValuesList(List<TestDataSchemaValues> testDataSchemaValuesList);

  /**
   * update TestDataSchemaValues
   *
   * @param testDataSchemaValues testDataSchemaValues
   */
  @UpdateProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "updateTestDataSchemaValues")
  Integer updateTestDataSchemaValues(TestDataSchemaValues testDataSchemaValues);

  /**
   * update TestDataSchemaValues enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param testDataId testDataId
   */
  @UpdateProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "delTestDataSchemaValuesByTestDataId")
  Integer delTestDataSchemaValuesByTestDataId(boolean isAdmin, String username, String testDataId);

  /**
   * update TestDataSchemaValues enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param schemaId schemaId
   */
  @UpdateProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "delTestDataSchemaValuesBySchemaId")
  Integer delTestDataSchemaValuesBySchemaId(boolean isAdmin, String username, String schemaId);

  /** get TestDataSchemaValues list */
  @Select("select * from test_data_schema_values where enable_flag = 1 order by data_row asc ")
  @Results({
    @Result(
        column = "fk_test_data_schema_id",
        property = "testDataSchema",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.testData.mapper.TestDataSchemaMapper.getTestDataSchemaById",
                fetchType = FetchType.LAZY))
  })
  List<TestDataSchemaValues> getTestDataSchemaValuesList();

  /**
   * get TestDataSchemaValuesList by schemaId
   *
   * @param schemaId schemaId
   */
  @Select(
      "select * from test_data_schema_values "
          + "where enable_flag = 1 "
          + "and fk_test_data_schema_id = #{schemaId} "
          + "order by data_row asc ")
  @Results({
    @Result(
        column = "fk_test_data_schema_id",
        property = "testDataSchema",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.testData.mapper.TestDataSchemaMapper.getTestDataSchemaById",
                fetchType = FetchType.LAZY))
  })
  List<TestDataSchemaValues> getTestDataSchemaValuesListBySchemaId(
      @Param("schemaId") String schemaId);

  @Select(
      "select * from test_data_schema_values where enable_flag = 1 and id = #{id} order by data_row asc ")
  @Results({
    @Result(
        column = "fk_test_data_schema_id",
        property = "testDataSchema",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.testData.mapper.TestDataSchemaMapper.getTestDataSchemaById",
                fetchType = FetchType.LAZY))
  })
  TestDataSchemaValues getTestDataSchemaValuesById(@Param("id") String id);

  /**
   * get TestDataSchemaValuesList by schemaId
   *
   * @param testDataId testDataId
   */
  @Select(
      "select * from test_data_schema_values "
          + "where enable_flag = 1 "
          + "and fk_test_data_id = #{testDataId} "
          + "order by data_row asc ")
  @Results({
    @Result(
        column = "fk_test_data_id",
        property = "testData",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.testData.mapper.TestDataMapper.getTestDataByIdOnly",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "fk_test_data_schema_id",
        property = "testDataSchema",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.testData.mapper.TestDataSchemaMapper.getTestDataSchemaById",
                fetchType = FetchType.LAZY))
  })
  List<TestDataSchemaValues> getTestDataSchemaValuesListByTestDataId(
      @Param("testDataId") String testDataId);

  /**
   * get TestDataSchemaValues custom list
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param testDataId testDataId
   * @param map map
   */
  @SelectProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "getTestDataSchemaValuesCustomList")
  List<LinkedHashMap<String, Object>> getTestDataSchemaValuesCustomList(
      boolean isAdmin, String username, String testDataId, List<LinkedHashMap<String, Object>> map);

  /**
   * get testDataSchemaValuesId custom list
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param testDataId testDataId
   * @param map map
   */
  @SelectProvider(
      type = TestDataSchemaValuesMapperProvider.class,
      method = "getTestDataSchemaValuesCustomListId")
  List<LinkedHashMap<String, Object>> getTestDataSchemaValuesCustomListId(
      boolean isAdmin, String username, String testDataId, List<LinkedHashMap<String, Object>> map);
}
