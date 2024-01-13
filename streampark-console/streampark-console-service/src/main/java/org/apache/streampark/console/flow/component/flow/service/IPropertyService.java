package org.apache.streampark.console.flow.component.flow.service;

import java.util.List;
import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.apache.streampark.console.flow.component.flow.request.UpdatePathRequest;

public interface IPropertyService {

  /**
   * Querying group attribute information based on stopPageId
   *
   * @param stopPageId stopPageId
   */
  String queryAll(String fid, String stopPageId);

  /**
   * Modify stops attribute information
   *
   * @param content content
   */
  String updatePropertyList(String username, String[] content);

  /**
   * Modify stops attribute information
   *
   * @param id id
   * @param content content
   */
  String updateProperty(String username, String content, String id);

  /** query All StopsProperty List; */
  List<Property> getStopsPropertyList();

  /** delete StopsProperty according to ID; */
  int deleteStopsPropertyById(String id);

  /**
   * check stops template
   *
   * @param username username
   * @param stopsId stopsId
   */
  void checkStopTemplateUpdate(String username, String stopsId);

  String saveOrUpdateRoutePath(String username, UpdatePathRequest updatePathRequest);

  /**
   * deleteLastReloadDataByStopsId
   *
   * @param stopId stopId
   */
  String deleteLastReloadDataByStopsId(String stopId);

  String updateStopDisabled(String username, Boolean isAdmin, String id, Boolean enable);

  String previewCreateSql(String fid, String stopPageId);
}
