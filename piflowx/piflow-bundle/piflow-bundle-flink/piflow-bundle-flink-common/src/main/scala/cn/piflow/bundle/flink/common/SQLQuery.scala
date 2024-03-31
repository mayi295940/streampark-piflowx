package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class SQLQuery extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "执行sql查询语句"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var sql: String = _
  private var registerSourceViewName: String = _
  private var registerResultViewName: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    if (StringUtils.isNotBlank(registerSourceViewName)) {
      val inputTable: Table = in.read()
      tableEnv.createTemporaryView(registerSourceViewName, inputTable)
    }

    val resultTable = tableEnv.sqlQuery(sql)

    // 将结果注册为临时视图
    if (StringUtils.isNotBlank(registerResultViewName)) {
      tableEnv.createTemporaryView(registerResultViewName, resultTable)
    }

    out.write(resultTable)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
    registerSourceViewName = MapUtil.get(map, "registerSourceViewName", "").asInstanceOf[String]
    registerResultViewName = MapUtil.get(map, "registerResultViewName", "").asInstanceOf[String]
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val registerSourceViewName = new PropertyDescriptor()
      .name("registerSourceViewName")
      .displayName("registerSourceViewName")
      .description("将输入源注册为flink虚拟表,然后针对虚拟表进行查询计算。如果不需要，则不配置，比如sql中使用的表已经注册过。")
      .defaultValue("")
      .required(false)
      .order(1)
      .example("input_temp")
    descriptor = registerSourceViewName :: descriptor

    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("Sql string")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .order(2)
      .example("select * from temp")
    descriptor = sql :: descriptor

    val registerResultViewName = new PropertyDescriptor()
      .name("registerResultViewName")
      .displayName("registerResultViewName")
      .description("将结果table注册为flink虚拟表,以便后续使用。如果不需要，则不配置。")
      .defaultValue("")
      .required(false)
      .order(3)
      .example("output_temp")

    descriptor = registerResultViewName :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
