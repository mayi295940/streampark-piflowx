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

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import com.mongodb.{MongoClient, MongoCredential, ServerAddress}
import com.mongodb.client.{FindIterable, MongoCollection, MongoCursor, MongoDatabase}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.bson.Document

import java.util

import scala.collection.mutable.ArrayBuffer

class GetMongo extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Get data from mongodb"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var addresses: String = _
  var credentials: String = _
  var dataBase: String = _
  var collection: String = _
  var sql: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val session: SparkSession = pec.get[SparkSession]()
    val addressesArr: util.ArrayList[ServerAddress] = new util.ArrayList[ServerAddress]()
    val ipANDport: Array[String] = addresses.split(",").map(x => x.trim)

    for (x <- ipANDport.indices) {
      if (x % 2 == 0) {
        addressesArr.add(new ServerAddress(ipANDport(x), ipANDport(x + 1).toInt))
      }
    }

    val credentialsArr: util.ArrayList[MongoCredential] = new util.ArrayList[MongoCredential]()
    if (credentials.nonEmpty) {
      val name_database_password: Array[String] = credentials.split(",").map(x => x.trim)
      for (x <- name_database_password.indices) {
        if (x % 3 == 0) {
          credentialsArr.add(
            MongoCredential.createScramSha1Credential(
              name_database_password(x),
              name_database_password(x + 1),
              name_database_password(x + 2).toCharArray))
        }
      }
    }

    val client: MongoClient = new MongoClient(addressesArr, credentialsArr)
    val db: MongoDatabase = client.getDatabase(dataBase)
    val col: MongoCollection[Document] = db.getCollection(collection)

    val documents: FindIterable[Document] = col.find()
    val dataIterator: MongoCursor[Document] = documents.iterator()
    var document: Document = null
    var fileNamesArr: Array[String] = Array()
    var rowArr: ArrayBuffer[ArrayBuffer[String]] = ArrayBuffer()
    while (dataIterator.hasNext) {
      var dataArr: ArrayBuffer[String] = ArrayBuffer()
      document = dataIterator.next()
      val fileNamesSet: util.Set[String] = document.keySet()
      fileNamesArr = fileNamesSet.toArray.map(_.asInstanceOf[String])
      for (x <- 1 until fileNamesArr.length) {
        dataArr += document.get(fileNamesArr(x)).toString
      }
      rowArr += dataArr
    }

    val names: ArrayBuffer[String] = ArrayBuffer()
    for (n <- (1 until fileNamesArr.length)) {
      names += fileNamesArr(n)
    }
    val fields: Array[StructField] =
      names.toArray.map(d => StructField(d, StringType, nullable = true))
    val schema: StructType = StructType(fields)

    val rows: ArrayBuffer[Row] = rowArr.map(r => {
      Row.fromSeq(r.toSeq)
    })

    val rdd: RDD[Row] = session.sparkContext.makeRDD(rows)
    val df: DataFrame = session.createDataFrame(rdd, schema)

    df.createTempView(collection)
    if (sql.length == 0) {
      sql = "select * from " + collection
    }

    val finalDF: DataFrame = session.sql(sql).toDF()
    out.write(finalDF)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    addresses = MapUtil.get(map, "addresses").asInstanceOf[String]
    credentials = MapUtil.get(map, "credentials").asInstanceOf[String]
    dataBase = MapUtil.get(map, "dataBase").asInstanceOf[String]
    collection = MapUtil.get(map, "collection").asInstanceOf[String]
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val addresses = new PropertyDescriptor()
      .name("addresses")
      .displayName("addresses")
      .description("Database connection address, you need to fill in:" +
        " IP1, port 1, IP2, port 2")
      .defaultValue("")
      .required(true)
    descriptor = addresses :: descriptor

    val credentials = new PropertyDescriptor()
      .name("credentials")
      .displayName("credentials")
      .description("To connect credentials, you need to write like this: " +
        "user name 1, table name 1, password 1, username 2, table name 2, password 2")
      .defaultValue("")
      .required(false)
    descriptor = credentials :: descriptor

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
      .description("We take the collection you need as a form, and you can find what you want.")
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

  override def getEngineType: String = Constants.ENGIN_SPARK

}
