package org.apache.streampark.console.flow.component.dataSource.domain;

import org.apache.streampark.console.flow.base.utils.LoggerUtil;
import org.apache.streampark.console.flow.base.utils.UUIDUtils;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSourceProperty;
import org.apache.streampark.console.flow.component.dataSource.mapper.DataSourceMapper;
import org.apache.streampark.console.flow.component.dataSource.mapper.DataSourcePropertyMapper;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class DataSourceDomain {

  private Logger logger = LoggerUtil.getLogger();

  private final DataSourceMapper dataSourceMapper;
  private final DataSourcePropertyMapper dataSourcePropertyMapper;
  private StopsComponentMapper stopsComponentMapper;

  @Autowired
  public DataSourceDomain(
      DataSourceMapper dataSourceMapper,
      DataSourcePropertyMapper dataSourcePropertyMapper,
      StopsComponentMapper stopsComponentMapper) {
    this.dataSourceMapper = dataSourceMapper;
    this.dataSourcePropertyMapper = dataSourcePropertyMapper;
    this.stopsComponentMapper = stopsComponentMapper;
  }

  /**
   * saveOrUpdate
   *
   * @param dataSource
   * @return
   * @throws Exception
   */
  public DataSource saveOrUpdate(DataSource dataSource) throws Exception {
    if (null == dataSource) {
      throw new Exception("dataSource is null");
    }
    if (StringUtils.isBlank(dataSource.getId())) {
      return insertDataSource(dataSource);
    } else {
      return updateDataSource(dataSource);
    }
  }

  /**
   * Insert DataSource
   *
   * @param dataSource
   * @return
   */
  public DataSource insertDataSource(DataSource dataSource) throws Exception {
    if (null == dataSource) {
      throw new Exception("dataSource is null");
    }
    if (StringUtils.isBlank(dataSource.getId())) {
      dataSource.setId(UUIDUtils.getUUID32());
    }
    // Duplicate checking
    List<String> isExitsName =
        dataSourceMapper.getDataSourceByDataSourceName(
            dataSource.getDataSourceName(), dataSource.getId());
    if (isExitsName != null && isExitsName.size() > 0) {
      throw new Exception("dataSourceName already exists，please change dataSourceName");
    }
    // if DataSourceType = 'STOP',insert image,for flowPage use
    if ("STOP".equals(dataSource.getDataSourceType())
        && StringUtils.isNotEmpty(dataSource.getStopsTemplateBundle())) {
      // Get "Stops" component image url by bundle
      String imgUrl =
          stopsComponentMapper.getStopsComponentImageUrlByBundle(
              dataSource.getStopsTemplateBundle());
      dataSource.setImageUrl(imgUrl);
    }
    int addDataSource = dataSourceMapper.addDataSource(dataSource);
    if (addDataSource <= 0) {
      throw new Exception("dataSource insert failed");
    }
    List<DataSourceProperty> dataSourcePropertyList = dataSource.getDataSourcePropertyList();
    if (null == dataSourcePropertyList || dataSourcePropertyList.size() <= 0) {
      return dataSource;
    }
    for (DataSourceProperty dataSourceProperty : dataSourcePropertyList) {
      if (null == dataSourceProperty) {
        continue;
      }
      dataSourceProperty.setDataSource(dataSource);
    }
    insertDataSourcePropertyList(dataSourcePropertyList);
    return dataSource;
  }

  /**
   * insertDataSourcePropertyList
   *
   * @param dataSourcePropertyList
   * @return
   * @throws Exception
   */
  public int insertDataSourcePropertyList(List<DataSourceProperty> dataSourcePropertyList)
      throws Exception {
    if (null == dataSourcePropertyList || dataSourcePropertyList.size() == 0) {
      return 0;
    }
    int affectedRows = dataSourcePropertyMapper.addDataSourcePropertyList(dataSourcePropertyList);
    if (affectedRows <= 0) {
      throw new Exception("dataSourcePropertyList insert failed");
    }
    logger.debug("insertDataSourcePropertyList Affected Rows : " + affectedRows);
    return affectedRows;
  }

  /**
   * insertDataSourceProperty
   *
   * @param dataSourceProperty
   * @return
   * @throws Exception
   */
  public DataSourceProperty insertDataSourceProperty(DataSourceProperty dataSourceProperty)
      throws Exception {
    int affectedRows = dataSourcePropertyMapper.addDataSourceProperty(dataSourceProperty);
    if (affectedRows <= 0) {
      throw new Exception("dataSourceProperty insert failed");
    }
    logger.debug("insertDataSourceProperty Affected Rows : " + affectedRows);
    return dataSourceProperty;
  }

  /**
   * update DataSource
   *
   * @param dataSource
   * @return
   */
  public DataSource updateDataSource(DataSource dataSource) throws Exception {
    if (null == dataSource) {
      throw new Exception("dataSource is null");
    }
    if (StringUtils.isBlank(dataSource.getId())) {
      throw new Exception("dataSource id is null");
    }
    List<String> isExitsName =
        dataSourceMapper.getDataSourceByDataSourceName(
            dataSource.getDataSourceName(), dataSource.getId());
    if (isExitsName != null && isExitsName.size() > 0) {
      throw new Exception("dataSourceName already exists，please change dataSourceName");
    }
    int updateDataSource = dataSourceMapper.updateDataSource(dataSource);
    if (updateDataSource <= 0) {
      return null;
    }
    List<DataSourceProperty> dataSourcePropertyList = dataSource.getDataSourcePropertyList();
    if (null != dataSourcePropertyList && dataSourcePropertyList.size() > 0) {
      for (DataSourceProperty dataSourceProperty : dataSourcePropertyList) {
        if (null == dataSourceProperty) {
          continue;
        }
        dataSourceProperty.setDataSource(dataSource);
        saveOrUpdateDataSourceProperty(dataSourceProperty);
      }
    }
    return dataSource;
  }

  /**
   * saveOrUpdateDataSourceProperty
   *
   * @param dataSourceProperty
   * @return
   * @throws Exception
   */
  public DataSourceProperty saveOrUpdateDataSourceProperty(DataSourceProperty dataSourceProperty)
      throws Exception {
    if (null == dataSourceProperty) {
      return null;
    }
    int affectedRows = 0;
    if (StringUtils.isBlank(dataSourceProperty.getId())) {
      dataSourceProperty.setId(UUIDUtils.getUUID32());
      affectedRows = dataSourcePropertyMapper.addDataSourceProperty(dataSourceProperty);
      if (affectedRows <= 0) {
        throw new Exception("datasource insert failed");
      }
    } else {
      affectedRows = dataSourcePropertyMapper.updateDataSourceProperty(dataSourceProperty);
      logger.debug("insertDataSourceProperty Affected Rows : " + affectedRows);
    }
    return dataSourceProperty;
  }

  /**
   * getDataSourceById
   *
   * @param username
   * @param isAdmin
   * @param id
   * @return
   */
  public DataSource getDataSourceById(String username, boolean isAdmin, String id) {
    return dataSourceMapper.getDataSourceByIdAndUser(username, isAdmin, id);
  }

  /**
   * getDataSourceTemplateList
   *
   * @return
   */
  public List<DataSource> getDataSourceTemplateList() {
    return dataSourceMapper.getDataSourceTemplateList();
  }

  /**
   * getDataSourceList
   *
   * @param username
   * @param isAdmin
   * @return
   */
  public List<DataSource> getDataSourceList(String username, boolean isAdmin) {
    return dataSourceMapper.getDataSourceList(username, isAdmin);
  }

  /**
   * getDataSourceVoListParam
   *
   * @param username
   * @param isAdmin
   * @param param
   * @return
   */
  public List<DataSourceVo> getDataSourceVoListParam(
      String username, boolean isAdmin, String param) {
    return dataSourceMapper.getDataSourceVoListParam(username, isAdmin, param);
  }

  /**
   * @param dataSourceId
   * @return
   */
  public List<DataSourceProperty> getDataSourcePropertyListByDataSourceId(String dataSourceId) {
    return dataSourcePropertyMapper.getDataSourcePropertyListByDataSourceId(dataSourceId);
  }

  public int updateEnableFlagByDatasourceId(String username, String id) {
    return dataSourcePropertyMapper.updateEnableFlagByDatasourceId(username, id);
  }

  /**
   * getStopDataSourceForFlowPage
   *
   * @param username
   * @param isAdmin
   * @return
   */
  public List<DataSourceVo> getStopDataSourceForFlowPage(String username, boolean isAdmin) {
    return dataSourceMapper.getStopDataSourceForFlowPage(username, isAdmin);
  }
}
