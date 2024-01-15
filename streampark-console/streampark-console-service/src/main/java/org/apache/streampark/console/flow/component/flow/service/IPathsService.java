package org.apache.streampark.console.flow.component.flow.service;

import org.apache.streampark.console.flow.component.flow.entity.Flow;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.vo.PathsVo;

import java.util.List;

public interface IPathsService {

  public int deletePathsByFlowId(String username, String id);

  /**
   * Query connection information according to flowId and pageid
   *
   * @param flowId
   * @param pageId
   * @return
   */
  public String getPathsByFlowIdAndPageId(String flowId, String pageId);

  /**
   * Query connection information
   *
   * @param flowId
   * @param from
   * @param to
   * @return
   */
  public List<PathsVo> getPaths(String flowId, String from, String to);

  /**
   * Query the number of connections
   *
   * @param flowId
   * @param from
   * @param to
   * @return
   */
  public Integer getPathsCounts(String flowId, String from, String to);

  /**
   * Save update connection information
   *
   * @param pathsVo
   * @return
   */
  public int upDatePathsVo(String username, PathsVo pathsVo);

  /**
   * Insert list<Paths>
   *
   * @param pathsList
   * @return
   */
  public int addPathsList(String username, List<Paths> pathsList, Flow flow);
}
