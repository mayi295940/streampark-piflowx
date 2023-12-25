package org.apache.streampark.console.flow.component.testData.mapper;

import org.apache.streampark.console.flow.component.testData.entity.TestData;
import org.apache.streampark.console.flow.component.testData.mapper.provider.TestDataMapperProvider;
import org.apache.streampark.console.flow.component.testData.vo.TestDataVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface TestDataMapper {

  /**
   * add TestData
   *
   * @param testData testData
   * @return Integer
   */
  @InsertProvider(type = TestDataMapperProvider.class, method = "addTestData")
  Integer addTestData(TestData testData);

  /**
   * update TestData
   *
   * @param testData testData
   * @return Integer
   */
  @UpdateProvider(type = TestDataMapperProvider.class, method = "updateTestData")
  Integer updateTestData(TestData testData);

  /**
   * update TestData enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param id id
   * @return Integer
   */
  @UpdateProvider(type = TestDataMapperProvider.class, method = "delTestDataById")
  Integer delTestDataById(boolean isAdmin, String username, String id);

  /**
   * get TestData by id
   *
   * @param id id
   * @return TestData
   */
  @Select("select * from test_data where enable_flag=1 and id=#{id} ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "schemaList",
        many =
            @Many(
                select =
                    "cn.cnic.component.testData.mapper.TestDataSchemaMapper.getTestDataSchemaListByTestDataId",
                fetchType = FetchType.LAZY)),
    @Result(
        column = "id",
        property = "schemaValuesList",
        many =
            @Many(
                select =
                    "cn.cnic.component.testData.mapper.TestDataSchemaValuesMapper.getTestDataSchemaValuesListByTestDataId",
                fetchType = FetchType.LAZY))
  })
  TestData getTestDataById(@Param("id") String id);

  /**
   * get TestData by id, Do not perform related queries
   *
   * @param id id
   * @return TestData
   */
  @Select("select * from test_data where enable_flag=1 and id=#{id} ")
  TestData getTestDataByIdOnly(@Param("id") String id);

  /**
   * get TestDataVo by id
   *
   * @param id id
   * @return TestDataVo
   */
  @Select("select * from test_data where enable_flag=1 and id=#{id} ")
  TestDataVo getTestDataVoById(@Param("id") String id);

  /**
   * search TestData List
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   */
  @SelectProvider(type = TestDataMapperProvider.class, method = "getTestDataList")
  List<TestData> getTestDataList(boolean isAdmin, String username, String param);

  /**
   * search TestDataVo List
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   */
  @SelectProvider(type = TestDataMapperProvider.class, method = "getTestDataList")
  List<TestDataVo> getTestDataVoList(boolean isAdmin, String username, String param);

  /**
   * get TestDataVo by id
   *
   * @param testDataName testDataName
   * @return TestDataVo
   */
  @Select("SELECT name FROM test_data WHERE enable_flag=1 and name=#{testDataName} ")
  String getTestDataName(@Param("testDataName") String testDataName);
}
