package cn.piflow.conf

import cn.piflow.{Constants, IncrementalStop, JobContext}
import cn.piflow.util.{ConfigureUtil, HdfsUtil, PropertyUtil}

/** Created by xjzhu@cnic.cn on 7/15/19 */
abstract class ConfigurableIncrementalStop[DataType]
  extends ConfigurableStop[DataType]
  with IncrementalStop[DataType] {

  override var incrementalPath: String = _

  override def init(flowName: String, stopName: String): Unit = {
    incrementalPath = ConfigureUtil.getIncrementPath().stripSuffix(Constants.SINGLE_SLASH) +
      Constants.SINGLE_SLASH + flowName + Constants.SINGLE_SLASH + stopName
  }

  override def readIncrementalStart(): String = {
    if (!HdfsUtil.exists(incrementalPath))
      HdfsUtil.createFile(incrementalPath)
    val value: String = HdfsUtil.getLine(incrementalPath)
    value
  }

  override def saveIncrementalStart(value: String): Unit = {
    HdfsUtil.saveLine(incrementalPath, value)
  }

}
