package cn.piflow.bundle.spark.mongodb

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class GetMongoDB extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Get data from mongodb"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var ip: String = _
  var port: String = _
  var dataBase: String = _
  var collection: String = _
  var sql: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    ip = MapUtil.get(map, "ip").asInstanceOf[String]
    port = MapUtil.get(map, "port").asInstanceOf[String]
    dataBase = MapUtil.get(map, "dataBase").asInstanceOf[String]
    collection = MapUtil.get(map, "collection").asInstanceOf[String]
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val ip = new PropertyDescriptor()
      .name("ip")
      .displayName("ip")
      .description("IP address,for example:0.0.0.1")
      .defaultValue("")
      .required(true)
    descriptor = ip :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("port")
      .description("the port")
      .defaultValue("")
      .required(true)
    descriptor = port :: descriptor

    val dataBase = new PropertyDescriptor()
      .name("dataBase")
      .displayName("dataBase")
      .description("data base")
      .defaultValue("")
      .required(true)
    descriptor = dataBase :: descriptor

    val collection = new PropertyDescriptor()
      .name("collection")
      .displayName("collection")
      .description("collection")
      .defaultValue("")
      .required(true)
    descriptor = collection :: descriptor

    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("sql")
      .description("We take the collection you need as a form, " +
        "and you can find what you want.You can also give up filling in this item," +
        "and you will get all the data for this collection")
      .defaultValue("")
      .language(Language.Sql)
      .required(false)
    descriptor = sql :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/mongoDB/GetMongo.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Mongodb)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val session: SparkSession = pec.get[SparkSession]()

    val df: DataFrame = session.read
      .format("com.mongodb.spark.sql")
      .options(
        Map(
          "spark.mongodb.input.uri" -> ("mongodb://" + ip + ":" + port +
            Constants.SINGLE_SLASH + dataBase + "." + collection))
      )
      .load()

    df.createTempView(collection)
    if (sql.isEmpty) {
      sql = "select * from " + collection
    }

    val finalDF: DataFrame = session.sql(sql).toDF()
    out.write(finalDF)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
