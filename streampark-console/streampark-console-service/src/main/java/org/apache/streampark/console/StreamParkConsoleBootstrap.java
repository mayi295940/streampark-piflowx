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

package org.apache.streampark.console;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 *
 * <pre>
 *
 *      _____ __                                             __
 *     / ___// /_________  ____ _____ ___  ____  ____ ______/ /__
 *     \__ \/ __/ ___/ _ \/ __ `/ __ `__ \/ __ \  __ `/ ___/ //_/
 *    ___/ / /_/ /  /  __/ /_/ / / / / / / /_/ / /_/ / /  / ,<
 *   /____/\__/_/   \___/\__,_/_/ /_/ /_/ ____/\__,_/_/  /_/|_|
 *                                     /_/
 *
 *   WebSite:  https://streampark.apache.org
 *   GitHub :  https://github.com/apache/incubator-streampark
 *
 *   [StreamPark] Make stream processing easier ô~ô!
 *
 * </pre>
 */
@Slf4j
@PropertySources({
  @PropertySource(value = "classpath:flow/apiConfig.properties", encoding = "utf-8"),
  @PropertySource(value = "classpath:flow/baseConfig.properties", encoding = "utf-8"),
  @PropertySource(value = "classpath:flow/messageConfig.properties", encoding = "utf-8"),
  @PropertySource(value = "classpath:flow/docker.properties", encoding = "utf-8")
})
@MapperScan(basePackages = "org.apache.streampark.console.flow.**.mapper.*.*")
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
public class StreamParkConsoleBootstrap {

  public static void main(String[] args) {
    new SpringApplicationBuilder(StreamParkConsoleBootstrap.class).run(args);
  }
}
