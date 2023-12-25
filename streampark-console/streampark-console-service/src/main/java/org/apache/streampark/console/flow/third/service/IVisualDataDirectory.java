package org.apache.streampark.console.flow.third.service;

import java.util.Map;

public interface IVisualDataDirectory {

  public Map<String, Object> getVisualDataDirectoryData(String appId, String stopName);
}
