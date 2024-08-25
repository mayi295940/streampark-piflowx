/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.piflow.bundle.spark.mongodb

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class PutMongoDB extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Put data to mongodb"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var ip: String = _
  var port: String = _
  var dataBase: String = _
  var collection: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark: SparkSession = pec.get[SparkSession]()
    val df: DataFrame = in.read()

    df.write
      .options(Map("spark.mongodb.output.uri" -> ("mongodb://" + ip + ":" + port +
        Constants.SINGLE_SLASH + dataBase + "." + collection)))
      .mode("append")
      .format("com.mongodb.spark.sql")
      .save()
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    ip = MapUtil.get(map, "ip").asInstanceOf[String]
    port = MapUtil.get(map, "port").asInstanceOf[String]
    dataBase = MapUtil.get(map, "dataBase").asInstanceOf[String]
    collection = MapUtil.get(map, "collection").asInstanceOf[String]
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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/mongoDB/PutMongo.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Mongodb)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
