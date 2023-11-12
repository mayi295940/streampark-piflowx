package org.apache.streampark.console.flow.component.testData.utils;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.apache.streampark.console.flow.base.util.UUIDUtils;
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchema;
import org.apache.streampark.console.flow.controller.requestVo.RequestTestDataSchemaVo;

public class TestDataSchemaUtils {

  public static TestDataSchema setTestDataSchemaBasicInformation(
      TestDataSchema testDataSchema, boolean isSetId, String username) {
    if (null == testDataSchema) {
      testDataSchema = new TestDataSchema();
    }
    if (isSetId) {
      testDataSchema.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    testDataSchema.setCrtDttm(new Date());
    testDataSchema.setCrtUser(username);
    testDataSchema.setLastUpdateDttm(new Date());
    testDataSchema.setLastUpdateUser(username);
    testDataSchema.setVersion(0L);
    return testDataSchema;
  }

  /**
   * testDataSchemaVo data to testDataSchema
   *
   * @param testDataSchemaVo
   * @param testDataSchema
   * @return
   */
  public static TestDataSchema copyDataToTestDataSchema(
          RequestTestDataSchemaVo testDataSchemaVo, TestDataSchema testDataSchema, String username) {
    if (null == testDataSchemaVo || StringUtils.isBlank(username)) {
      return null;
    }
    if (null == testDataSchema) {
      return null;
    }
    // copy
    BeanUtils.copyProperties(testDataSchemaVo, testDataSchema);
    testDataSchema.setLastUpdateDttm(new Date());
    testDataSchema.setLastUpdateUser(username);
    return testDataSchema;
  }
}
