package org.apache.streampark.console.flow.component.testData.utils;

import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchemaValues;

import java.util.Date;

public class TestDataSchemaValuesUtils {

  /**
   * set TestDataSchemaValues baseInfo
   *
   * @param testDataSchemaValues
   * @param isSetId
   * @param username
   * @return
   */
  public static TestDataSchemaValues setTestDataSchemaBasicInformation(
      TestDataSchemaValues testDataSchemaValues, boolean isSetId, String username) {
    if (null == testDataSchemaValues) {
      testDataSchemaValues = new TestDataSchemaValues();
    }
    if (isSetId) {
      testDataSchemaValues.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    testDataSchemaValues.setCrtDttm(new Date());
    testDataSchemaValues.setCrtUser(username);
    testDataSchemaValues.setLastUpdateDttm(new Date());
    testDataSchemaValues.setLastUpdateUser(username);
    testDataSchemaValues.setVersion(0L);
    return testDataSchemaValues;
  }
}
