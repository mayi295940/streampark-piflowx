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
