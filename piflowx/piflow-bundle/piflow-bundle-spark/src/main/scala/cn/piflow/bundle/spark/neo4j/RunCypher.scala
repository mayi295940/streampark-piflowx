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

package cn.piflow.bundle.spark.neo4j

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame
import org.neo4j.driver.v1._

class RunCypher extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "anhong12@cnic.cn"
  override val description: String = "Run cql on neo4j"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var url: String = _
  var userName: String = _
  var password: String = _
  var cql: String = ""

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val driver: Driver = GraphDatabase.driver(url, AuthTokens.basic(userName, password))
    var session: Session = null

    try {
      session = driver.session()
      session.run(cql)
    } finally {
      session.close()
      driver.close()
    }
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    userName = MapUtil.get(map, "userName").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    cql = MapUtil.get(map, "cql").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("url")
      .description("The url of neo4j")
      .defaultValue("")
      .required(true)
      .example("bolt://0.0.1.1:7687")
    descriptor = url :: descriptor

    val userName = new PropertyDescriptor()
      .name("userName")
      .displayName("UserName")
      .description("The user of neo4j")
      .defaultValue("")
      .required(true)
      .example("neo4j")
    descriptor = userName :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of neo4j")
      .defaultValue("")
      .required(true)
      .sensitive(true)
      .example("123456")
    descriptor = password :: descriptor

    val cql = new PropertyDescriptor()
      .name("cql")
      .displayName("cql")
      .description(" The Cypher")
      .defaultValue("")
      .required(true)
      .example("match(n:user) where n.userid ='11' set n.userclass =5")
    descriptor = cql :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/neo4j/RunCypher.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Neo4jGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
