package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import scala.collection.mutable.{Map => MMap}

class Faker extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "根据每列提供的Data Faker表达式生成模拟数据。"
  override val inportList: List[String] = List(Port.NonePort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: List[Map[String, Any]] = _
  private var count: Int = _
  private var ratio: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    count = MapUtil.get(map, "count", "10").asInstanceOf[String].toInt
    ratio = MapUtil.get(map, "ratio", "1").asInstanceOf[String].toInt
    schema = MapUtil.get(map, "schema").asInstanceOf[List[Map[String, Any]]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val count = new PropertyDescriptor()
      .name("count")
      .displayName("Count")
      .description(
        "The number of rows to produce. If this is options is set, the source is bounded otherwise it is unbounded and runs indefinitely.")
      .defaultValue("")
      .required(false)
      .dataType(Int.toString())
      .example("10")
      .order(1)
    descriptor = count :: descriptor

    val ratio = new PropertyDescriptor()
      .name("ratio")
      .displayName("Ratio")
      .description("The maximum rate at which the source produces records.")
      .defaultValue("10000")
      .required(false)
      .dataType(Int.toString())
      .example("10")
      .order(2)
    descriptor = ratio :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("schema")
      .defaultValue("")
      .required(true)
      .language(Language.DataFakerSchema)
      .order(3)
      .example("[{\"filedName\":\"name\",\"filedType\":\"STRING\",\"expression\":\"<superhero.name>\",\"comment\":\"姓名\"},{\"filedName\":\"power\",\"filedType\":\"STRING\",\"expression\":\"<superhero.power>\",\"nullRate\":0.5},{\"filedName\":\"age\",\"filedType\":\"INT\",\"expression\":\"<number.numberBetween ''0'',''1000''>\"},{\"filedName\":\"timeField\",\"computedColumnExpression\":\"PROCTIME()\"},{\"filedName\":\"timestamp1\",\"filedType\":\"TIMESTAMP(3)\",\"expression\":\"<date.past ''15'',''SECONDS''>\"},{\"filedName\":\"timestamp2\",\"filedType\":\"TIMESTAMP(3)\",\"expression\":\"<date.past ''15'',''5'',''SECONDS''>\"},{\"filedName\":\"timestamp3\",\"filedType\":\"TIMESTAMP(3)\",\"expression\":\"<date.future ''15'',''5'',''SECONDS''>\"},{\"filedName\":\"time\",\"filedType\":\"TIME\",\"expression\":\"<time.future ''15'',''5'',''SECONDS''>\"},{\"filedName\":\"date1\",\"filedType\":\"DATE\",\"expression\":\"<date.birthday>\"},{\"filedName\":\"date2\",\"filedType\":\"DATE\",\"expression\":\"<date.birthday ''1'',''100''>\"},{\"filedName\":\"order_status\",\"filedType\":\"STRING\",\"expression\":\"<Options.option ''RECEIVED'',''SHIPPED'',''CANCELLED'')>\"}]")

    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns, conf) = getWithColumnsAndConf(schema)

    val tmpTable = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'faker',
         | $conf
         | 'number-of-rows'='$count',
         | 'rows-per-second'='$ratio'
         |)""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

  }

  private def getWithColumnsAndConf(schema: List[Map[String, Any]]): (String, String) = {
    var columns = List[String]()
    var conf = List[String]()

    schema.foreach(
      item => {

        val filedMap = MMap(item.toSeq: _*)

        val filedName = MapUtil.get(filedMap, "filedName").toString
        columns = columns :+ s"`$filedName` "

        val filedType = filedMap.getOrElse("filedType", "").toString
        if (StringUtils.isNotBlank(filedType)) {
          columns = columns :+ s"$filedType"
        }

        val computedColumnExpression = filedMap.getOrElse("computedColumnExpression", "").toString
        if (StringUtils.isEmpty(filedType) && StringUtils.isNotBlank(computedColumnExpression)) {
          columns = columns :+ s"AS $computedColumnExpression"
        }

        val comment = filedMap.getOrElse("comment", "").toString
        if (StringUtils.isNotBlank(comment)) {
          columns = columns :+ s" COMMENT '$comment'"
        }

        columns = columns :+ Constants.COMMA

        var expression = filedMap.getOrElse("expression", "").toString
        if (StringUtils.isNotBlank(expression)) {
          expression = expression.trim.replaceFirst("<", "").dropRight(1)
          conf = s"'fields.$filedName.expression' = '#{$expression}'," :: conf
        }

        val nullRate = filedMap.getOrElse("nullRate", "").toString
        if (StringUtils.isNotBlank(nullRate)) {
          conf = s"'fields.$filedName.null-rate' = '$nullRate'," :: conf
        }

        val length = filedMap.getOrElse("length", "").toString
        if (StringUtils.isNotBlank(length)) {
          conf = s"'fields.$filedName.length' = '$length'," :: conf
        }

      })

    (columns.mkString("").trim.dropRight(1), conf.mkString(""))
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
