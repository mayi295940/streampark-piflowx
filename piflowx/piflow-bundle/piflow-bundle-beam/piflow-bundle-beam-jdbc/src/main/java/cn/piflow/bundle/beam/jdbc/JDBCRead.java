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

package cn.piflow.bundle.beam.jdbc;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import cn.piflow.Constants;
import cn.piflow.JobContext;
import cn.piflow.JobInputStream;
import cn.piflow.JobOutputStream;
import cn.piflow.ProcessContext;
import cn.piflow.conf.ConfigurableStop;
import cn.piflow.conf.Port;
import cn.piflow.conf.StopGroup;
import cn.piflow.conf.bean.PropertyDescriptor;
import cn.piflow.conf.util.ImageUtil;
import cn.piflow.enums.DataBaseType;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scala.collection.JavaConverters;

public class JDBCRead extends ConfigurableStop<PCollection<Row>> {

    private String jdbcUrl = "";
    private String driver = "";
    private String username = "";
    private String password = "";
    private String table = "";
    private String query = "";
    private Integer fetchSize;

    @Override
    public void perform(
                        JobInputStream<PCollection<Row>> in,
                        JobOutputStream<PCollection<Row>> out,
                        JobContext<PCollection<Row>> pec) {

        Pipeline pipeline = (Pipeline) pec.get("org.apache.beam.sdk.Pipeline");

        JdbcIO.ReadRows readRows =
            JdbcIO.readRows()
                .withDataSourceConfiguration(
                    JdbcIO.DataSourceConfiguration.create(driver, jdbcUrl)
                        .withUsername(username)
                        .withPassword(password));

        if (fetchSize != null && fetchSize > 0) {
            readRows = readRows.withFetchSize(fetchSize);
        }

        if (StringUtils.isEmpty(query) && StringUtils.isNotBlank(table)) {
            query = "SELECT * FROM " + table;
        }

        readRows = readRows.withQuery(query);

        out.write(pipeline.apply(readRows));
    }

    @Override
    public void setProperties(scala.collection.immutable.Map<String, Object> map) {
        Map<String, Object> javaMap = JavaConverters.mapAsJavaMap(map);
        jdbcUrl = MapUtils.getString(javaMap, "jdbcUrl", "");
        driver = MapUtils.getString(javaMap, "driver", "");
        username = MapUtils.getString(javaMap, "username", "");
        password = MapUtils.getString(javaMap, "password", "");
        table = MapUtils.getString(javaMap, "table", "");
        query = MapUtils.getString(javaMap, "query", "");
        fetchSize = MapUtils.getInteger(javaMap, "fetchSize", 0);
    }

    @Override
    public void initialize(ProcessContext<PCollection<Row>> ctx) {
    }

    @Override
    public String authorEmail() {
        return "";
    }

    @Override
    public String description() {
        return "使用JDBC驱动向任意类型的关系型数据库读取数据";
    }

    @Override
    public scala.collection.immutable.List<String> inportList() {
        ArrayList<String> groups = Lists.newArrayList(Port.DefaultPort());
        return JavaConverters.asScalaBuffer(groups).toList();
    }

    @Override
    public scala.collection.immutable.List<String> outportList() {
        ArrayList<String> groups = Lists.newArrayList(Port.DefaultPort());
        return JavaConverters.asScalaBuffer(groups).toList();
    }

    @Override
    public scala.collection.immutable.List<PropertyDescriptor> getPropertyDescriptor() {

        List<PropertyDescriptor> list = new ArrayList<>();

        PropertyDescriptor jdbcUrl =
            new PropertyDescriptor()
                .name("jdbcUrl")
                .displayName("JdbcUrl")
                .description("JDBC数据库url。")
                .defaultValue("")
                .required(true)
                .example("jdbc:mysql://127.0.0.1:3306/test");

        list.add(jdbcUrl);

        PropertyDescriptor driver =
            new PropertyDescriptor()
                .name("driver")
                .displayName("Driver")
                .description("用于连接到此URL的JDBC驱动类名")
                .defaultValue("")
                .required(true)
                .example(DataBaseType.MySQL8.getDriverClassName());

        list.add(driver);

        PropertyDescriptor username =
            new PropertyDescriptor()
                .name("username")
                .displayName("Username")
                .description("JDBC用户名。")
                .defaultValue("")
                .required(true)
                .example("root");
        list.add(username);

        PropertyDescriptor password =
            new PropertyDescriptor()
                .name("password")
                .displayName("password")
                .description("JDBC密码。")
                .defaultValue("")
                .required(true)
                .example("12345")
                .sensitive(true);
        list.add(password);

        PropertyDescriptor table =
            new PropertyDescriptor()
                .name("table")
                .displayName("table")
                .description("连接到JDBC表的名称。")
                .defaultValue("")
                .required(false)
                .example("test");
        list.add(table);

        PropertyDescriptor query =
            new PropertyDescriptor()
                .name("query")
                .displayName("query")
                .description("连接到JDBC表的名称。")
                .defaultValue("")
                .required(false)
                .example("select * from test");
        list.add(query);

        PropertyDescriptor fetchSize =
            new PropertyDescriptor()
                .name("fetchSize")
                .displayName("FetchSize")
                .description("每次循环读取时应该从数据库中获取的行数。如果指定的值为 '0'，则该配置项会被忽略。")
                .defaultValue("")
                .dataType("Integer")
                .required(false)
                .example("500");

        list.add(fetchSize);

        return JavaConverters.asScalaBuffer(list).toList();
    }

    @Override
    public byte[] getIcon() {
        return ImageUtil.getImage("icon/jdbc/MysqlRead.png", "");
    }

    @Override
    public scala.collection.immutable.List<String> getGroup() {
        ArrayList<String> groups = Lists.newArrayList(StopGroup.JdbcGroup());
        return JavaConverters.asScalaBuffer(groups).toList();
    }

    @Override
    public String getEngineType() {
        return Constants.ENGIN_BEAM();
    }
}
