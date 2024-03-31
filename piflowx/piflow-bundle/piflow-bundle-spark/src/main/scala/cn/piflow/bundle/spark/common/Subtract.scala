package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.StructType

class Subtract extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String =
    "Delete the existing data in the right table from the left table"
  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Subtract.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val leftDF = in.read(Port.LeftPort)
    val rightDF = in.read(Port.RightPort)
    val outDF = leftDF.except(rightDF)

    out.write(outDF)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
