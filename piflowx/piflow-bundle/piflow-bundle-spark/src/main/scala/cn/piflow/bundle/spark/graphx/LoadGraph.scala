package cn.piflow.bundle.spark.graphx

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.graphx.{GraphLoader, PartitionStrategy}
import org.apache.spark.sql.{DataFrame, SparkSession}

class LoadGraph extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "Load data and construct a graphx"
  val inportList: List[String] = List(Port.DefaultPort)

  var edgePort: String = "edges"
  var vertexPort: String = "vertex"
  val outportList: List[String] = List(edgePort, vertexPort)
  var dataPath: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sc = spark.sparkContext

    import spark.sqlContext.implicits._
    var graph = GraphLoader
      .edgeListFile(sc, dataPath, canonicalOrientation = true)
      .partitionBy(PartitionStrategy.RandomVertexCut)
    // TODO:can not transfer EdgeRdd to Dataset
    out.write(edgePort, graph.edges.toDF())
    out.write(vertexPort, graph.vertices.toDF())

  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    dataPath = MapUtil.get(map, "dataPath").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val dataPath = new PropertyDescriptor()
      .name("dataPath")
      .displayName("Data_Path")
      .defaultValue("")
      .allowableValues(Set(""))
      .required(true)
      .example("hdfs://192.168.3.138:8020/work/test/test.csv")
    descriptor = dataPath :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/graphx/LoadGraph.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.GraphX)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
