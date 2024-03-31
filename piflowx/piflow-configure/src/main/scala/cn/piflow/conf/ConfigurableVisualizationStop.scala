package cn.piflow.conf

import cn.piflow.{Constants, VisualizationStop}
import cn.piflow.util.ConfigureUtil

abstract class ConfigurableVisualizationStop[DataType]
  extends ConfigurableStop[DataType]
  with VisualizationStop[DataType] {

  override var visualizationPath: String = _
  override var processId: String = _
  override var stopName: String = _

  override def init(stopName: String): Unit = {
    this.stopName = stopName
  }

  override def getVisualizationPath(processId: String): String = {
    visualizationPath = ConfigureUtil
      .getVisualizationPath()
      .stripSuffix(
        Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + processId + Constants.SINGLE_SLASH + stopName
    visualizationPath
  }

}
