package org.apache.streampark.console.flow.component.flow.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.apache.streampark.console.flow.base.util.UUIDUtils;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.vo.PathsVo;

public class PathsUtil {
  /**
   * pathsList Po To Vo
   *
   * @param pathsList
   * @return
   */
  public static List<PathsVo> pathsListPoToVo(List<Paths> pathsList) {
    List<PathsVo> pathsVoList = null;
    if (null != pathsList && pathsList.size() > 0) {
      pathsVoList = new ArrayList<PathsVo>();
      for (Paths paths : pathsList) {
        if (null != paths) {
          PathsVo pathsVo = new PathsVo();
          BeanUtils.copyProperties(paths, pathsVo);
          pathsVoList.add(pathsVo);
        }
      }
    }
    return pathsVoList;
  }

  /**
   * pathsVoList Vo To Po
   *
   * @param pathsVoList
   * @return
   */
  public static List<Paths> pathsListVoToPo(String username, List<PathsVo> pathsVoList) {
    List<Paths> pathsList = null;
    if (null != pathsVoList && pathsVoList.size() > 0) {
      pathsList = new ArrayList<Paths>();
      for (PathsVo pathsVo : pathsVoList) {
        if (null != pathsVo) {
          Paths paths = new Paths();
          BeanUtils.copyProperties(pathsVo, paths);
          paths.setId(UUIDUtils.getUUID32());
          paths.setCrtDttm(new Date());
          paths.setCrtUser(username);
          paths.setLastUpdateDttm(new Date());
          paths.setLastUpdateUser(username);
          paths.setEnableFlag(true);
          pathsList.add(paths);
        }
      }
    }
    return pathsList;
  }
}
