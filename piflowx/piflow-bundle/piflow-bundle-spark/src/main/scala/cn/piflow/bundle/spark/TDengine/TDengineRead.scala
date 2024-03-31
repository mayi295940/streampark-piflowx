package cn.piflow.bundle.spark.TDengine

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class TDengineRead extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "llei@cnic.com"
  override val description: String = "Read data from TDengine"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var driver: String = _
  var url: String = _
  var user: String = _
  var password: String = _
  var dbtable: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", url)
      .option("driver", driver)
      .option("dbtable", dbtable)
      .option("user", user)
      .option("password", password)
      .load()

    out.write(jdbcDF)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    driver = MapUtil.get(map, key = "driver").asInstanceOf[String]
    url = MapUtil.get(map, key = "url").asInstanceOf[String]
    user = MapUtil.get(map, key = "user").asInstanceOf[String]
    password = MapUtil.get(map, key = "password").asInstanceOf[String]
    dbtable = MapUtil.get(map, key = "dbtable").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("The Driver of TDengine database")
      .defaultValue("com.taosdata.jdbc.TSDBDriver")
      .required(true)
      .example("com.taosdata.jdbc.TSDBDriver")
    descriptor = driver :: descriptor

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url, for example jdbc:TAOS://127.0.0.1:6030/dbname")
      .defaultValue("")
      .required(true)
      .example("jdbc:TAOS://127.0.0.1:6030/dbname")
    descriptor = url :: descriptor

    val user = new PropertyDescriptor()
      .name("user")
      .displayName("User")
      .description("The user name of database")
      .defaultValue("")
      .required(true)
      .example("root")
    descriptor = user :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of database")
      .defaultValue("")
      .required(true)
      .example("123456")
      .sensitive(true)
    descriptor = password :: descriptor

    val dbtable = new PropertyDescriptor()
      .name("dbtable")
      .displayName("DBTable")
      .description("The table you want to write")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = dbtable :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/TDengineRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
