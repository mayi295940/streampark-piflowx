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

package cn.piflow.bundle.spark.solr

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse
import org.apache.solr.common.SolrInputDocument
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructField

class PutIntoSolr extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Write data to solr"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var solrURL: String = _
  var SolrCollection: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    solrURL = MapUtil.get(map, "solrURL").asInstanceOf[String]
    SolrCollection = MapUtil.get(map, "SolrCollection").asInstanceOf[String]
  }

  var client: HttpSolrClient = _
  var doc: SolrInputDocument = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val df: DataFrame = in.read()

    val SchemaList: List[StructField] = df.schema.toList
    val length: Int = SchemaList.length
    val url = solrURL + Constants.SINGLE_SLASH + SolrCollection

    df.collect()
      .foreach(row => {
        client = new HttpSolrClient.Builder(url).build()
        doc = new SolrInputDocument()
        for (x <- 0 until length) {
          doc.addField(SchemaList(x).name, row.get(x))
        }
        val update: UpdateResponse = client.add(doc)
        client.commit()
      })
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/solr/PutSolr.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.SolrGroup)
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val solrURL = new PropertyDescriptor()
      .name("solrURL")
      .displayName("SolrURL")
      .description("The url of solr")
      .defaultValue("")
      .required(true)
      .example("http://127.0.0.1:8886/solr")
    descriptor = solrURL :: descriptor

    val SolrCollection = new PropertyDescriptor()
      .name("SolrCollection")
      .displayName("SolrCollection")
      .description("The name of collection")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = SolrCollection :: descriptor
    descriptor
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
