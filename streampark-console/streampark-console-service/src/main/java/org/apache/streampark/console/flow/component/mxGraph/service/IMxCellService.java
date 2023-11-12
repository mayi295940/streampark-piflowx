package org.apache.streampark.console.flow.component.mxGraph.service;

import org.apache.streampark.console.flow.component.mxGraph.entity.MxCell;

public interface IMxCellService {

  public int deleteMxCellById(String username, String id);

  public MxCell getMeCellById(String id);
}
