package org.apache.streampark.console.flow.component.system.service;

public interface ISysInitRecordsService {

  public boolean isInBootPage();

  public String initComponents(String currentUser);

  public String threadMonitoring(String currentUser);
}
