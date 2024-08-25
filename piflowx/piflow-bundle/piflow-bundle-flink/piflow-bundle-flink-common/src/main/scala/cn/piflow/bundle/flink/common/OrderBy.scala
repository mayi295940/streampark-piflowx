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

package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.{ApiExpression, Table}
import org.apache.flink.table.api.Expressions.$

class OrderBy extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "ORDER BY组件使结果行根据指定的表达式进行排序。"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var expression: String = _
  private var offset: Int = _
  private var fetch: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    expression = MapUtil.get(map, "expression").asInstanceOf[String]
    offset = MapUtil.get(map, "offset", "0").asInstanceOf[String].toInt
    fetch = MapUtil.get(map, "fetch", "0").asInstanceOf[String].toInt
  }

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val inputTable = in.read()
    var resultTable = inputTable

    if (StringUtils.isNotEmpty(expression)) {
      val fields = expression.split(Constants.COMMA).map(x => x.trim)
      val array = new Array[ApiExpression](fields.length)
      for (x <- fields.indices) {
        val orderExpression: Array[String] = fields(x).split(Constants.ARROW_SIGN).map(x => x.trim)
        orderExpression(1).toLowerCase match {
          case "desc" => array(x) = $(orderExpression(0)).desc()
          case "asc" => array(x) = $(orderExpression(0)).asc()
        }
      }
      resultTable = resultTable.orderBy(array: _*)
    }

    if (offset > 0) {
      resultTable = resultTable.offset(offset)
    }
    if (fetch > 0) {
      resultTable = resultTable.fetch(fetch)
    }

    out.write(resultTable)
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val condition = new PropertyDescriptor()
      .name("expression")
      .displayName("Expression")
      .description("在流模式下运行时，表的主要排序顺序必须按时间属性升序。所有后续的orders都可以自由选择。但是批处理模式没有这个限制。")
      .defaultValue("")
      .required(false)
      .order(1)
      .example("name->desc,age->asc")
    descriptor = condition :: descriptor

    val offset = new PropertyDescriptor()
      .name("offset")
      .displayName("offset")
      .description("Offset操作根据偏移位置来限定（可能是已排序的）结果集。")
      .defaultValue("")
      .required(false)
      .order(2)
      .example("10")
    descriptor = offset :: descriptor

    val fetch = new PropertyDescriptor()
      .name("fetch")
      .displayName("fetch")
      .description("Fetch操作将（可能已排序的）结果集限制为前n行。")
      .defaultValue("")
      .required(false)
      .order(3)
      .example("10")
    descriptor = fetch :: descriptor

    descriptor

  }

  override def getIcon(): Array[Byte] = {
    // todo 图片
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
