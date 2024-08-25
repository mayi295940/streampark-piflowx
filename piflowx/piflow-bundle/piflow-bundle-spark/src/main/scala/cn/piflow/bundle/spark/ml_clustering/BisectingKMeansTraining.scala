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

package cn.piflow.bundle.spark.ml_clustering

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.sql.{DataFrame, SparkSession}

class BisectingKMeansTraining extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "BisectingKMeans clustering"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  var training_data_path: String = _
  var model_save_path: String = _
  var maxIter: String = _
  var k: Int = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    // load data stored in libsvm format as a dataframe
    val data = spark.read.format("libsvm").load(training_data_path)

    // Param for maximum number of iterations (>= 0)
    var maxIterValue: Int = 50
    if (maxIter != "") {
      maxIterValue = maxIter.toInt
    }

    // clustering with BisectingKMeans algorithm
    val model = new BisectingKMeans()
      .setMaxIter(maxIterValue)
      .setK(k)
      .fit(data)

    // model persistence
    model.save(model_save_path)

    import spark.implicits._
    val dfOut = Seq(model_save_path).toDF
    dfOut.show()
    out.write(dfOut)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    training_data_path = MapUtil.get(map, key = "training_data_path").asInstanceOf[String]
    model_save_path = MapUtil.get(map, key = "model_save_path").asInstanceOf[String]
    maxIter = MapUtil.get(map, key = "maxIter").asInstanceOf[String]
    k = Integer.parseInt(MapUtil.get(map, key = "k").toString)
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val training_data_path = new PropertyDescriptor()
      .name("training_data_path")
      .displayName("TRAINING_DATA_PATH")
      .defaultValue("")
      .required(true)

    val model_save_path = new PropertyDescriptor()
      .name("model_save_path")
      .displayName("MODEL_SAVE_PATH")
      .description("")
      .defaultValue("")
      .required(true)

    val maxIter = new PropertyDescriptor()
      .name("maxIter")
      .displayName("MAX_ITER")
      .description("Param for maximum number of iterations (>= 0).")
      .defaultValue("")
      .required(false)

    val k = new PropertyDescriptor()
      .name("k")
      .displayName("K")
      .description("The number of clusters. ")
      .defaultValue("")
      .required(true)

    descriptor = training_data_path :: descriptor
    descriptor = model_save_path :: descriptor
    descriptor = maxIter :: descriptor
    descriptor = k :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/ml_clustering/BisetingKmeansTraining.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.MLGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
