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

package cn.piflow.bundle.flink.jdbc

import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.enums.DataBaseType
import org.apache.flink.connector.jdbc.JdbcInputFormat
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object JDBCReadTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val url =
      "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true"
    val user: String = "root"
    val password: String = "123456"
    val sql: String = "select id, name from test limit 10"
    val schema: String = "id:String,name:String"

    val jdbcInputFormat = JdbcInputFormat.buildJdbcInputFormat
      .setDrivername(DataBaseType.MySQL8.getDriverClassName)
      .setDBUrl(url)
      .setQuery(sql)
      .setUsername(user)
      .setPassword(password)
      .setRowTypeInfo(RowTypeUtil.getRowTypeInfo(schema))
      .finish()

    val df = env.createInput(jdbcInputFormat)

    df.print()

    env.execute()

  }

}
