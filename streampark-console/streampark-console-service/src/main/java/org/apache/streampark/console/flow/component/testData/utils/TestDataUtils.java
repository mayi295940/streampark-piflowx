package org.apache.streampark.console.flow.component.testData.utils;

import java.util.Date;
import org.apache.streampark.console.flow.base.util.UUIDUtils;
import org.apache.streampark.console.flow.component.testData.entity.TestData;

public class TestDataUtils {

  public static TestData setTestDataBasicInformation(
      TestData testData, boolean isSetId, String username) {
    if (null == testData) {
      testData = new TestData();
    }
    if (isSetId) {
      testData.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    testData.setCrtDttm(new Date());
    testData.setCrtUser(username);
    testData.setLastUpdateDttm(new Date());
    testData.setLastUpdateUser(username);
    testData.setVersion(0L);
    return testData;
  }
}
