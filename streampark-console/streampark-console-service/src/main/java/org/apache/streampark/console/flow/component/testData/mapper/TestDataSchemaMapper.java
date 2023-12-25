package org.apache.streampark.console.flow.component.testData.mapper;

import org.apache.streampark.console.flow.component.testData.entity.TestDataSchema;
import org.apache.streampark.console.flow.component.testData.mapper.provider.TestDataSchemaMapperProvider;
import org.apache.streampark.console.flow.component.testData.vo.TestDataSchemaVo;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
public interface TestDataSchemaMapper {

  /**
   * add TestDataSchema
   *
   * @param testDataSchema testDataSchema
   * @return Integer
   */
  @InsertProvider(type = TestDataSchemaMapperProvider.class, method = "addTestDataSchema")
  Integer addTestDataSchema(TestDataSchema testDataSchema);

  /**
   * add TestDataSchema List
   *
   * @param testDataSchemaList testDataSchemaList
   */
  @InsertProvider(type = TestDataSchemaMapperProvider.class, method = "addTestDataSchemaList")
  Integer addTestDataSchemaList(List<TestDataSchema> testDataSchemaList);

  /**
   * update TestDataSchema
   *
   * @param testDataSchema testDataSchema
   */
  @UpdateProvider(type = TestDataSchemaMapperProvider.class, method = "updateTestDataSchema")
  Integer updateTestDataSchema(TestDataSchema testDataSchema);

  /**
   * update TestDataSchema enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param testDataId testDataId
   */
  @UpdateProvider(
      type = TestDataSchemaMapperProvider.class,
      method = "delTestDataSchemaByTestDataId")
  Integer delTestDataSchemaByTestDataId(boolean isAdmin, String username, String testDataId);

  /**
   * update TestDataSchema enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param schemaId schemaId
   */
  @UpdateProvider(type = TestDataSchemaMapperProvider.class, method = "delTestDataSchemaById")
  Integer delTestDataSchemaById(boolean isAdmin, String username, String schemaId);

  /**
   * get TestDataSchema by id
   *
   * @param id id
   */
  @SelectProvider(type = TestDataSchemaMapperProvider.class, method = "getTestDataSchemaById")
  TestDataSchema getTestDataSchemaById(String id);

  /**
   * get TestDataSchema list by testDataId
   *
   * @param testDataId testDataId
   */
  @Select(
      "SELECT * FROM test_data_schema "
          + "WHERE enable_flag=1 "
          + "AND fk_test_data_id = #{testDataId} "
          + "ORDER BY  field_soft ASC ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    // @Result(column = "id", property = "schemaValuesList", many = @Many(select =
    // "cn.cnic.component.testData.mapper.TestDataSchemaValuesMapper.getTestDataSchemaValuesListBySchemaId", fetchType = FetchType.LAZY))
  })
  List<TestDataSchema> getTestDataSchemaListByTestDataId(@Param("testDataId") String testDataId);

  /**
   * get TestDataSchema list by testDataId page
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   * @param testDataId testDataId
   */
  @SelectProvider(
      type = TestDataSchemaMapperProvider.class,
      method = "getTestDataSchemaListByTestDataIdSearch")
  List<TestDataSchema> getTestDataSchemaListByTestDataIdSearch(
      boolean isAdmin, String username, String param, String testDataId);

  /**
   * get TestDataSchemaVo list by testDataId search
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   * @param testDataId testDataId
   */
  @SelectProvider(
      type = TestDataSchemaMapperProvider.class,
      method = "getTestDataSchemaListByTestDataIdSearch")
  List<TestDataSchemaVo> getTestDataSchemaVoListByTestDataIdSearch(
      boolean isAdmin, String username, String param, String testDataId);

  /**
   * get TestDataSchemaId and NameList by testDataId
   *
   * @param testDataId testDataId
   * @return List<Map<String,String>> key1=ID key2=FIELD_NAME
   */
  @Select(
      "SELECT TDS.id AS ID,TDS.field_name as FIELD_NAME "
          + "FROM test_data_schema TDS "
          + "WHERE TDS.enable_flag=1 "
          + "AND TDS.fk_test_data_id = #{testDataId} "
          + "ORDER BY TDS.field_soft ASC ")
  List<LinkedHashMap<String, Object>> getTestDataSchemaIdAndNameListByTestDataId(
      @Param("testDataId") String testDataId);
}
