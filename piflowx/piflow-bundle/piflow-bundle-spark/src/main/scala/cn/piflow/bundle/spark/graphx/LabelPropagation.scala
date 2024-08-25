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

package cn.piflow.bundle.spark.graphx

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.sql.{DataFrame, SparkSession}

class LabelPropagation extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "Compute sub graphs"
  var edgePortIn: String = "edgesIn"
  var vertexPortIn: String = "vertexIn"
  val inportList: List[String] = List(edgePortIn, vertexPortIn)

  var edgePortOut: String = "edgesOut"
  var vertexPortOut: String = "vertexOut"
  val outportList: List[String] = List(edgePortOut, vertexPortOut)
  var maxIter: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sc = spark.sparkContext
    val edge = in.read(edgePortIn).asInstanceOf[EdgeRDD[Int]]
    val vertex = in.read(vertexPortIn).asInstanceOf[VertexRDD[Int]]
    val graph = Graph(vertex, edge)

    var maxIterValue: Int = 50
    if (maxIter != "") {
      maxIterValue = maxIter.toInt
    }

    val res = LabelPropagation.run(graph, maxIterValue)

    import spark.sqlContext.implicits._
    out.write(edgePortOut, res.edges.toDF())
    out.write(vertexPortOut, res.vertices.toDF())
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    maxIter = MapUtil.get(map, "maxIter").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val maxIter = new PropertyDescriptor()
      .name("maxIter")
      .displayName("MAX_ITER")
      .defaultValue("")
      .allowableValues(Set(""))
      .required(false)
      .example("20")
    descriptor = maxIter :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/graphx/LabelPropagation.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.GraphX)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
