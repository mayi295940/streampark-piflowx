package cn.piflow.bundle.spark.visualization

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class PieChart extends ConfigurableVisualizationStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with pie chart. "
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  // var x:String =_
  var dimension: String = _
  var indicator: String = _
  var indicatorOption: String = _

  override var visualizationType: String = VisualizationType.PieChart

  override def setProperties(map: Map[String, Any]): Unit = {
    // x=MapUtil.get(map,key="x").asInstanceOf[String]
    dimension = MapUtil.get(map, key = "dimension").asInstanceOf[String]
    indicator = MapUtil.get(map, key = "indicator").asInstanceOf[String]
    indicatorOption = MapUtil.get(map, key = "indicatorOption").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val dimension = new PropertyDescriptor()
      .name("dimension")
      .displayName("Dimension")
      .description("The dimension of pie chart")
      .defaultValue("")
      .example("type")
      .required(true)

    val indicator = new PropertyDescriptor()
      .name("indicator")
      .displayName("Indicator")
      .description("The indicator of pie chart")
      .defaultValue("")
      .example("project")
      .required(true)

    val indicatorOption = new PropertyDescriptor()
      .name("indicatorOption")
      .displayName("IndicatorOption")
      .description("The indicator option of pie chart")
      .allowableValues(Set("COUNT", "SUM", "AVG", "MAX", "MIN"))
      .defaultValue("COUNT")
      .example("COUNT")
      .required(true)

    descriptor = dimension :: descriptor
    descriptor = indicator :: descriptor
    descriptor = indicatorOption :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/pie-chart.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Visualization)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val dataFrame = in.read()
    dataFrame.createOrReplaceTempView("PieChart")
    val sqlText =
      "select " + dimension + "," + indicatorOption + "(" + indicator + ") from PieChart group by " + dimension;
    println("PieChart Sql: " + sqlText)
    val pieChartDF = spark.sql(sqlText)
    out.write(pieChartDF.repartition(1))
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
