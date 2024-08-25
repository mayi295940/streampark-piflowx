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
import org.apache.spark.ml.clustering.{DistributedLDAModel, LDAModel, LocalLDAModel}
import org.apache.spark.sql.{DataFrame, SparkSession}

class LDAPrediction extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "06whuxx@163.com"
  val description: String = "Use an existing LDAModel to predict"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  var test_data_path: String = _
  var model_path: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    // load data stored in libsvm format as a dataframe
    val data = spark.read.format("libsvm").load(test_data_path)
    // data.show()

    // load model
    val model = LocalLDAModel.load(model_path)

    val predictions = model.transform(data)
    predictions.show()
    out.write(predictions)

  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    test_data_path = MapUtil.get(map, key = "test_data_path").asInstanceOf[String]
    model_path = MapUtil.get(map, key = "model_path").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val test_data_path = new PropertyDescriptor()
      .name("test_data_path")
      .displayName("TEST_DATA_PATH")
      .defaultValue("")
      .required(true)

    val model_path = new PropertyDescriptor()
      .name("model_path")
      .displayName("MODEL_PATH")
      .defaultValue("")
      .required(true)

    descriptor = test_data_path :: descriptor
    descriptor = model_path :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/ml_clustering/LDAPrediction.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.MLGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
