package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.{Table, TableResult}
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class SQLExecute extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "execute sql"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var sql: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    if (StringUtils.isNotEmpty(sql)) {
      sql
        .split(Constants.SEMICOLON)
        .map(
          t => {
            val result: TableResult = tableEnv.executeSql(t)
            result.print()
          })
    }

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("sql语句，多行sql以';'分隔")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .example("select * from temp;show tables;")
    descriptor = sql :: descriptor

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
