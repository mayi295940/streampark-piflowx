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

import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.schemas.Schema;
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
import java.util.stream.Collectors;

import scala.collection.JavaConverters;

public class JDBCWrite extends ConfigurableStop<PCollection<Row>> {

    private String jdbcUrl = "";
    private String driver = "";
    private String username = "";
    private String password = "";
    private String table = "";

    @Override
    public void perform(
                        JobInputStream<PCollection<Row>> in,
                        JobOutputStream<PCollection<Row>> out,
                        JobContext<PCollection<Row>> pec) {

        PCollection<Row> input = in.read();
        Schema schema = input.getSchema();

        List<String> fieldNames = schema.getFieldNames();
        int size = fieldNames.size();

        List<String> valueExpression = fieldNames.stream().map(x -> "?").collect(Collectors.toList());

        input.apply(
            JdbcIO.<Row>write()
                .withDataSourceConfiguration(
                    JdbcIO.DataSourceConfiguration.create(driver, jdbcUrl)
                        .withUsername(username)
                        .withPassword(password))
                .withStatement(
                    String.format(
                        "insert into %s values(%s)", table, StringUtils.join(valueExpression, ",")))
                .withPreparedStatementSetter(
                    (JdbcIO.PreparedStatementSetter<Row>) (row, query) -> {
                        for (int i = 0; i < size; i++) {
                            query.setString(i + 1, row.getValue(i).toString());
                        }
                    }));
    }

    @Override
    public void setProperties(scala.collection.immutable.Map<String, Object> map) {
        Map<String, Object> javaMap = JavaConverters.mapAsJavaMap(map);
        jdbcUrl = MapUtils.getString(javaMap, "jdbcUrl", "");
        driver = MapUtils.getString(javaMap, "driver", "");
        username = MapUtils.getString(javaMap, "username", "");
        password = MapUtils.getString(javaMap, "password", "");
        table = MapUtils.getString(javaMap, "table", "");
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
        return "使用JDBC驱动向任意类型的关系型数据库写入数据";
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

        return JavaConverters.asScalaBuffer(list).toList();
    }

    @Override
    public byte[] getIcon() {
        return ImageUtil.getImage("icon/jdbc/MysqlWrite.png", "");
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
