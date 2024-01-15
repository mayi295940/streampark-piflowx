package org.apache.streampark.console.flow.component.testData.service.impl;

import org.apache.streampark.console.flow.base.utils.FileUtils;
import org.apache.streampark.console.flow.base.utils.PageHelperUtils;
import org.apache.streampark.console.flow.base.utils.ReturnMapUtils;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.common.constant.MessageConfig;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.testData.domain.TestDataDomain;
import org.apache.streampark.console.flow.component.testData.entity.TestData;
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchema;
import org.apache.streampark.console.flow.component.testData.entity.TestDataSchemaValues;
import org.apache.streampark.console.flow.component.testData.service.ITestDataService;
import org.apache.streampark.console.flow.component.testData.utils.TestDataSchemaUtils;
import org.apache.streampark.console.flow.component.testData.utils.TestDataSchemaValuesUtils;
import org.apache.streampark.console.flow.component.testData.utils.TestDataUtils;
import org.apache.streampark.console.flow.component.testData.vo.TestDataSchemaVo;
import org.apache.streampark.console.flow.component.testData.vo.TestDataVo;
import org.apache.streampark.console.flow.controller.requestVo.SchemaValuesVo;
import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaValuesSaveVo;
import org.apache.streampark.console.flow.controller.requestVo.TestDataSchemaVoRequest;
import org.apache.streampark.console.flow.controller.requestVo.TestDataVoRequest;

import org.apache.commons.lang3.StringUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TestDataServiceImpl implements ITestDataService {

  private final TestDataDomain testDataDomain;

  @Autowired
  public TestDataServiceImpl(TestDataDomain testDataDomain) {
    this.testDataDomain = testDataDomain;
  }

  /**
   * saveOrUpdateTestDataSchema
   *
   * @param username
   * @param isAdmin
   * @param testDataVo
   * @return String
   * @throws Exception
   */
  @Override
  public String saveOrUpdateTestDataAndSchema(
      String username, boolean isAdmin, TestDataVoRequest testDataVo, boolean flag)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == testDataVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    if (StringUtils.isBlank(testDataVo.getName())) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    TestData testData = null;
    String testDataVoId = testDataVo.getId();
    if (StringUtils.isNotBlank(testDataVoId)) {
      testData = testDataDomain.getTestDataById(testDataVoId);
    }
    if (null == testData) {
      testData = TestDataUtils.setTestDataBasicInformation(null, false, username);
    }
    // copy
    BeanUtils.copyProperties(testDataVo, testData);
    // set update info
    testData.setLastUpdateDttm(new Date());
    testData.setLastUpdateUser(username);

    // get testDataSchemaVoList
    List<TestDataSchemaVoRequest> testDataSchemaVoList = testDataVo.getSchemaVoList();

    // update schema
    List<TestDataSchema> testDataSchemaList = testData.getSchemaList();
    List<String> delSchemaIdList = new ArrayList<>();
    if (null == testDataSchemaList) {
      testDataSchemaList = new ArrayList<>();
    }
    if (flag) {
      // update testDataSchemaList
      if (null != testDataSchemaVoList && testDataSchemaVoList.size() > 0) {
        Map<String, TestDataSchema> testDataSchemaDbMap = new HashMap<>();
        for (TestDataSchema testDataSchema : testDataSchemaList) {
          if (null == testDataSchema) {
            continue;
          }
          testDataSchemaDbMap.put(testDataSchema.getId(), testDataSchema);
        }
        List<TestDataSchema> testDataSchemaListNew = new ArrayList<>();
        for (TestDataSchemaVoRequest testDataSchemaVo : testDataSchemaVoList) {
          if (null == testDataSchemaVo) {
            continue;
          }
          // Determine if you need to delete it, if necessary, add it to delSchemaIdList
          if (testDataSchemaVo.isDelete()) {
            delSchemaIdList.add(testDataSchemaVo.getId());
            continue;
          }
          // get TestDataSchema from testDataSchemaDbMap by testDataSchemaVo id
          TestDataSchema testDataSchemaNew = testDataSchemaDbMap.get(testDataSchemaVo.getId());
          // If not, create a new one
          if (null == testDataSchemaNew) {
            testDataSchemaNew =
                TestDataSchemaUtils.setTestDataSchemaBasicInformation(null, false, username);
          }
          // copy data to testDataSchemaNew
          testDataSchemaNew =
              TestDataSchemaUtils.copyDataToTestDataSchema(
                  testDataSchemaVo, testDataSchemaNew, username);
          if (null == testDataSchemaNew) {
            continue;
          }
          testDataSchemaListNew.add(testDataSchemaNew);
        }
        testData.setSchemaList(testDataSchemaListNew);
      }
    } else {
      // update testDataSchemaList
      if (null == testDataSchemaVoList || testDataSchemaVoList.size() <= 0) {
        for (TestDataSchema testDataSchema : testDataSchemaList) {
          if (null == testDataSchema || StringUtils.isBlank(testDataSchema.getId())) {
            continue;
          }
          delSchemaIdList.add(testDataSchema.getId());
        }
      } else {
        Map<String, TestDataSchema> testDataSchemaDbMap = new HashMap<>();
        for (TestDataSchema testDataSchema : testDataSchemaList) {
          if (null == testDataSchema) {
            continue;
          }
          testDataSchemaDbMap.put(testDataSchema.getId(), testDataSchema);
        }
        List<TestDataSchema> testDataSchemaListNew = new ArrayList<>();
        for (TestDataSchemaVoRequest testDataSchemaVo : testDataSchemaVoList) {
          if (null == testDataSchemaVo) {
            continue;
          }
          TestDataSchema testDataSchema = testDataSchemaDbMap.get(testDataSchemaVo.getId());
          if (null == testDataSchema) {
            testDataSchema =
                TestDataSchemaUtils.setTestDataSchemaBasicInformation(
                    testDataSchema, flag, username);
          }
          TestDataSchema copyDataToTestDataSchema =
              TestDataSchemaUtils.copyDataToTestDataSchema(
                  testDataSchemaVo, testDataSchema, username);
          if (null == copyDataToTestDataSchema) {
            continue;
          }
          testDataSchemaDbMap.remove(testDataSchemaVo.getId());
          testDataSchemaListNew.add(copyDataToTestDataSchema);
        }
        testData.setSchemaList(testDataSchemaListNew);
        for (TestDataSchema testDataSchema : testDataSchemaDbMap.values()) {
          if (null == testDataSchema || StringUtils.isBlank(testDataSchema.getId())) {
            continue;
          }
          delSchemaIdList.add(testDataSchema.getId());
        }
      }
    }

    int affectedRows = 0;

    if (StringUtils.isBlank(testDataVoId)) {
      String testDataName = testDataDomain.getTestDataName(testData.getName());
      if (StringUtils.isNotBlank(testDataName)) {
        return ReturnMapUtils.setFailedMsgRtnJsonStr(
            MessageConfig.DUPLICATE_NAME_PLEASE_MODIFY_MSG("testDataName"));
      }
      testData.setId(UUIDUtils.getUUID32());
      affectedRows = testDataDomain.addTestData(testData, username);
    } else {
      affectedRows = testDataDomain.updateTestData(testData, username);
    }
    if (null != delSchemaIdList && delSchemaIdList.size() > 0) {
      affectedRows += testDataDomain.delTestDataSchemaList(delSchemaIdList, isAdmin, username);
    }
    if (affectedRows <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ERROR_MSG());
    }
    Map<String, Object> rtnMap =
        ReturnMapUtils.setSucceededCustomParam("testDataId", testData.getId());
    ReturnMapUtils.appendValues(rtnMap, "testDataName", testData.getName());
    return ReturnMapUtils.appendValuesToJson(rtnMap, "testDataDesc", testData.getDescription());
  }

  /**
   * saveOrUpdateTestDataSchemaValues
   *
   * @param username
   * @param isAdmin
   * @param schemaValuesVo
   * @return String
   * @throws Exception
   */
  @Override
  public String saveOrUpdateTestDataSchemaValues(
      String username, boolean isAdmin, TestDataSchemaValuesSaveVo schemaValuesVo)
      throws Exception {
    // Determine whether it is empty
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    // Determine whether it is empty
    if (null == schemaValuesVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_ERROR_MSG());
    }
    // Determine whether it is empty
    String testDataId = schemaValuesVo.getTestDataId();
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.PARAM_IS_NULL_MSG("testDataId"));
    }
    // Query "TestData" based on "testDataId"
    TestData testDataById = testDataDomain.getTestDataById(testDataId);
    if (null == testDataById) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_BY_ID_XXX_MSG(testDataId));
    }
    // schema List
    List<TestDataSchema> schemaList = testDataById.getSchemaList();
    if (null == schemaList || schemaList.size() <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          MessageConfig.DATA_PROPERTY_IS_NULL_MSG("schema"));
    }
    // "schemaList" converts "Map" (key is FieldName)
    Map<String, TestDataSchema> schemaMapDB = new HashMap<>();
    for (TestDataSchema testDataSchema : schemaList) {
      if (null == testDataSchema) {
        continue;
      }
      schemaMapDB.put(testDataSchema.getFieldName(), testDataSchema);
    }
    // schemaValues list
    List<TestDataSchemaValues> testDataSchemaValuesList = testDataById.getSchemaValuesList();
    // "schemaValue" converts "Map" (key is id)
    Map<String, TestDataSchemaValues> schemaValuesMapDB = new HashMap<>();
    // cycle
    for (TestDataSchemaValues testDataSchemaValues : testDataSchemaValuesList) {
      if (null == testDataSchemaValues) {
        continue;
      }
      schemaValuesMapDB.put(testDataSchemaValues.getId(), testDataSchemaValues);
    }
    List<TestDataSchemaValues> newTestDataSchemaValuesList = new ArrayList<>();
    SchemaValuesVo[] schemaValuesVoList = schemaValuesVo.getSchemaValuesList();
    // new schemaValue list
    if (null != schemaValuesVoList) {
      // cycle
      for (int i = 0; i < schemaValuesVoList.length; i++) {
        // get data object
        SchemaValuesVo schemaValuesVo_i = schemaValuesVoList[i];
        // fieldName
        String fieldName = schemaValuesVo_i.getSchemaName();
        // schemaValuesId
        String schemaValuesId = schemaValuesVo_i.getSchemaValueId();
        String schemaValues = schemaValuesVo_i.getSchemaValue();
        int dataRow = schemaValuesVo_i.getDataRow();
        boolean isDelete = schemaValuesVo_i.isDelete();
        // "TestDataSchemaValues" after modification
        TestDataSchemaValues testDataSchemaValues = null;
        // Determine if the Id is empty, add it if it is empty, or modify it otherwise
        if (StringUtils.isBlank(schemaValuesId)) {
          testDataSchemaValues =
              TestDataSchemaValuesUtils.setTestDataSchemaBasicInformation(
                  testDataSchemaValues, false, username);
          //  Use "fieldName" to get TestDataSchema from "schemaMapDB"
          TestDataSchema testDataSchema = schemaMapDB.get(fieldName);
          // Associative foreign key
          testDataSchemaValues.setTestData(testDataById);
          testDataSchemaValues.setTestDataSchema(testDataSchema);
        } else {
          //  Use "fieldName" to get TestDataSchema from "schemaMapDB"
          testDataSchemaValues = schemaValuesMapDB.get(schemaValuesId);
          if (null == testDataSchemaValues) {
            return ReturnMapUtils.setFailedMsgRtnJsonStr(
                MessageConfig.DATA_PROPERTY_IS_NULL_MSG("schemaValuesIdList"));
          }
          if (isDelete) {
            testDataSchemaValues.setEnableFlag(false);
          }
        }

        testDataSchemaValues.setFieldValue(schemaValues);
        testDataSchemaValues.setDataRow(dataRow);
        testDataSchemaValues.setLastUpdateDttm(new Date());
        testDataSchemaValues.setLastUpdateUser(username);
        newTestDataSchemaValuesList.add(testDataSchemaValues);
      }
    }
    if (null == newTestDataSchemaValuesList || newTestDataSchemaValuesList.size() < 0) {
      return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.NO_DATA_UPDATE_MSG());
    }
    int saveOrUpdateTestDataSchemaValuesList =
        testDataDomain.saveOrUpdateTestDataSchemaValuesList(
            username, newTestDataSchemaValuesList, testDataById);
    if (saveOrUpdateTestDataSchemaValuesList <= 0) {
      return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.UPDATE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.UPDATE_SUCCEEDED_MSG());
  }

  /**
   * checkTestDataName
   *
   * @param username
   * @param isAdmin
   * @param testDataName
   * @return String
   */
  @Override
  public String checkTestDataName(String username, boolean isAdmin, String testDataName) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    String res = testDataDomain.getTestDataName(testDataName);
    if (StringUtils.isNotBlank(res)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(
          MessageConfig.DUPLICATE_NAME_PLEASE_MODIFY_MSG("testDataName"));
    } else {
      return ReturnMapUtils.setSucceededMsgRtnJsonStr(
          MessageConfig.XXX_AVAILABLE_MSG("testDataName"));
    }
  }

  /**
   * delTestData
   *
   * @param username
   * @param isAdmin
   * @param testDataId
   * @return
   */
  @Override
  public String delTestData(String username, boolean isAdmin, String testDataId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is null");
    }
    int i = testDataDomain.delTestData(username, isAdmin, testDataId);
    if (i <= 0) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.DELETE_ERROR_MSG());
    }
    return ReturnMapUtils.setSucceededMsgRtnJsonStr(MessageConfig.DELETE_SUCCEEDED_MSG());
  }

  /**
   * Get TestData list page
   *
   * @param username
   * @param isAdmin
   * @param offset
   * @param limit
   * @param param
   * @return
   */
  @Override
  public String getTestDataListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param is error");
    }
    Page<TestDataVo> page = PageHelper.startPage(offset, limit);
    testDataDomain.getTestDataVoList(isAdmin, username, param);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * Get TestDataSchema list by testDataId
   *
   * @param username
   * @param isAdmin
   * @param param
   * @param testDataId
   * @return
   */
  @Override
  public String getTestDataSchemaList(
      String username, boolean isAdmin, String param, String testDataId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is null");
    }
    TestDataVo testDataVo = testDataDomain.getTestDataVoById(testDataId);
    if (null == testDataVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    List<TestDataSchemaVo> testDataVoList =
        testDataDomain.getTestDataSchemaVoListByTestDataIdSearch(
            isAdmin, username, param, testDataId);
    testDataVo.setSchemaVoList(testDataVoList);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("testData", testDataVo);
  }

  /**
   * Get TestData list page
   *
   * @param username
   * @param isAdmin
   * @param offset
   * @param limit
   * @param param
   * @param testDataId
   * @return
   */
  @Override
  public String getTestDataSchemaListPage(
      String username,
      boolean isAdmin,
      Integer offset,
      Integer limit,
      String param,
      String testDataId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param is error");
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is null");
    }
    TestDataVo testDataVo = testDataDomain.getTestDataVoById(testDataId);
    if (null == testDataVo) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.NO_DATA_MSG());
    }
    Page<TestDataSchemaVo> page = PageHelper.startPage(offset, limit);
    testDataDomain.getTestDataSchemaVoListByTestDataIdSearch(isAdmin, username, param, testDataId);
    Map<String, Object> rtnMap = ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    rtnMap = ReturnMapUtils.appendValues(rtnMap, "testData", testDataVo);
    return PageHelperUtils.setLayTableParamRtnStr(page, rtnMap);
  }

  /**
   * getTestDataSchemaValuesCustomListPage
   *
   * @param username
   * @param isAdmin
   * @param offset
   * @param limit
   * @param param
   * @param testDataId
   * @return
   */
  @Override
  public String getTestDataSchemaValuesCustomListPage(
      String username,
      boolean isAdmin,
      Integer offset,
      Integer limit,
      String param,
      String testDataId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (null == offset || null == limit) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("param is error");
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is error");
    }
    // find title
    List<LinkedHashMap<String, Object>> testDataSchemaIdAndNameListByTestDataId =
        testDataDomain.getTestDataSchemaIdAndNameListByTestDataId(testDataId);
    //
    Page<LinkedHashMap<String, Object>> page = PageHelper.startPage(offset, limit);
    testDataDomain.getTestDataSchemaValuesCustomList(
        isAdmin, username, testDataId, testDataSchemaIdAndNameListByTestDataId);
    Map<String, Object> rtnMap =
        PageHelperUtils.setCustomDataKey(page, "count", "schemaValue", null);
    // find id
    Page<LinkedHashMap<String, Object>> page1 = PageHelper.startPage(offset, limit);
    testDataDomain.getTestDataSchemaValuesCustomListId(
        isAdmin, username, testDataId, testDataSchemaIdAndNameListByTestDataId);
    rtnMap = PageHelperUtils.setCustomDataKey(page1, "count", "schemaValueId", rtnMap);
    rtnMap.put("schema", testDataSchemaIdAndNameListByTestDataId);
    return ReturnMapUtils.appendSucceededToJson(rtnMap);
  }

  /**
   * getTestDataSchemaValuesCustomList
   *
   * @param username
   * @param isAdmin
   * @param param
   * @param testDataId
   * @return
   */
  @Override
  public String getTestDataSchemaValuesCustomList(
      String username, boolean isAdmin, String param, String testDataId) {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is error");
    }
    List<LinkedHashMap<String, Object>> testDataSchemaIdAndNameListByTestDataId =
        testDataDomain.getTestDataSchemaIdAndNameListByTestDataId(testDataId);
    List<LinkedHashMap<String, Object>> testDataSchemaValuesCustomList_id =
        testDataDomain.getTestDataSchemaValuesCustomListId(
            isAdmin, username, testDataId, testDataSchemaIdAndNameListByTestDataId);
    List<LinkedHashMap<String, Object>> testDataSchemaValuesCustomList =
        testDataDomain.getTestDataSchemaValuesCustomList(
            isAdmin, username, testDataId, testDataSchemaIdAndNameListByTestDataId);
    Map<String, Object> setSucceededMsg =
        ReturnMapUtils.setSucceededMsg(MessageConfig.SUCCEEDED_MSG());
    setSucceededMsg.put("schema", testDataSchemaIdAndNameListByTestDataId);
    setSucceededMsg.put("schemaValue", testDataSchemaValuesCustomList);
    setSucceededMsg.put("schemaValueId", testDataSchemaValuesCustomList_id);
    return ReturnMapUtils.toJson(setSucceededMsg);
  }

  /**
   * Upload csv file and save flowTemplate
   *
   * @param username
   * @param testDataId
   * @param header
   * @param schema
   * @param delimiter
   * @param file
   * @return
   * @throws Exception
   */
  @Override
  public String uploadCsvFile(
      String username,
      String testDataId,
      boolean header,
      String schema,
      String delimiter,
      MultipartFile file)
      throws Exception {
    if (StringUtils.isBlank(username)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.ILLEGAL_USER_MSG());
    }
    if (StringUtils.isBlank(testDataId)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testDataId is null");
    }
    if (null == delimiter) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("delimiter is null");
    }
    if (file.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }
    TestData testDataDB = testDataDomain.getTestDataById(testDataId);
    if (null == testDataDB) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("testData is null");
    }
    if (!header && StringUtils.isBlank(schema)) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("schema is null");
    }
    if (header) {
      schema = null;
    }
    Map<String, Object> uploadMap =
        FileUtils.uploadRtnMap(file, SysParamsCache.ENGINE_FLINK_CSV_PATH, null);
    if (null == uploadMap || uploadMap.isEmpty()) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.UPLOAD_FAILED_MSG());
    }
    Integer code = (Integer) uploadMap.get("code");
    if (500 == code) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("failed to upload file");
    }
    String path = (String) uploadMap.get("path");
    // Read the CSV file according to the saved file path and return the CSV string
    LinkedHashMap<String, List<String>> csvMap =
        FileUtils.ParseCsvFileRtnColumnData(path, delimiter, schema);
    if (null == csvMap) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }
    List<TestDataSchema> testDataSchemaList = new ArrayList<>();
    List<TestDataSchemaValues> testDataSchemaValuesList = new ArrayList<>();
    int i = 0;
    for (String fieldName : csvMap.keySet()) {
      TestDataSchema testDataSchema =
          TestDataSchemaUtils.setTestDataSchemaBasicInformation(null, true, username);
      testDataSchema.setFieldName(fieldName);
      testDataSchema.setFieldType("String");
      testDataSchema.setFieldSoft(i + 1);
      testDataSchema.setTestData(testDataDB);
      testDataSchemaList.add(testDataSchema);
      // values
      List<String> fieldNameValueList = csvMap.get(fieldName);
      for (int j = 0; j < fieldNameValueList.size(); j++) {
        String fieldNameValue_j = fieldNameValueList.get(j);
        TestDataSchemaValues testDataSchemaValues =
            TestDataSchemaValuesUtils.setTestDataSchemaBasicInformation(null, true, username);
        testDataSchemaValues.setDataRow(j);
        testDataSchemaValues.setFieldValue(fieldNameValue_j);
        testDataSchemaValues.setTestData(testDataDB);
        testDataSchemaValues.setTestDataSchema(testDataSchema);
        testDataSchemaValuesList.add(testDataSchemaValues);
      }
      i++;
    }
    int affectedRows = testDataDomain.addSchemaList(testDataSchemaList, testDataDB, username);
    if (affectedRows <= 0) {
      testDataDomain.delTestData(username, false, testDataDB.getId());
      return ReturnMapUtils.setFailedMsgRtnJsonStr(MessageConfig.SUCCEEDED_MSG());
    }
    affectedRows +=
        testDataDomain.addTestDataSchemaValuesList(username, testDataSchemaValuesList, testDataDB);
    return ReturnMapUtils.setSucceededMsgRtnJsonStr("successful template upload");
  }
}
