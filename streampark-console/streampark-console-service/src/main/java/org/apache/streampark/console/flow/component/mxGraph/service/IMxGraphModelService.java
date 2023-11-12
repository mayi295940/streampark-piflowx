package org.apache.streampark.console.flow.component.mxGraph.service;

import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphVo;

public interface IMxGraphModelService {

  public String saveDataForTask(String username, String imageXML, String loadId, String operType);

  /**
   * save or add flowGroup
   *
   * @param imageXML
   * @param loadId
   * @param operType
   * @param flag
   * @return
   */
  public String saveDataForGroup(
      String username, String imageXML, String loadId, String operType, boolean flag);

  /**
   * addMxCellAndData
   *
   * @param mxGraphVo
   * @param username
   * @return
   */
  public String addMxCellAndData(MxGraphVo mxGraphVo, String username);
}
