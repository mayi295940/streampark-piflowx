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

package org.apache.streampark.console.base.config;

import org.apache.streampark.console.base.interceptor.UploadFileTypeInterceptor;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired private UploadFileTypeInterceptor uploadFileTypeInterceptor;

  private static final String[] CORS_MAPPINGS_ALLOWED_METHODS = {
    HttpMethod.POST.name(),
    HttpMethod.GET.name(),
    HttpMethod.PUT.name(),
    HttpMethod.OPTIONS.name(),
    HttpMethod.DELETE.name()
  };

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new ByteArrayHttpMessageConverter());
    converters.add(new StringHttpMessageConverter());
    converters.add(new ResourceHttpMessageConverter());
    converters.add(new AllEncompassingFormHttpMessageConverter());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods(CORS_MAPPINGS_ALLOWED_METHODS)
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);

    WebMvcConfigurer.super.addCorsMappings(registry);
  }

  @Bean
  public Module jacksonModule() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(Long.class, ToStringSerializer.instance);
    module.addSerializer(Long.TYPE, ToStringSerializer.instance);
    return module;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    String storagePathHead = System.getProperty("user.dir");

    String imagesPathFlink = ("file:" + storagePathHead + "/storage/flink/image/");
    String videosPathFlink = ("file:" + storagePathHead + "/storage/flink/video/");
    String xmlPathFlink = ("file:" + storagePathHead + "/storage/flink/xml/");

    String imagesPathSpark = ("file:" + storagePathHead + "/storage/spark/image/");
    String videosPathSpark = ("file:" + storagePathHead + "/storage/spark/video/");
    String xmlPathSpark = ("file:" + storagePathHead + "/storage/spark/xml/");

    registry
        .addResourceHandler("/images/**", "/videos/**", "/xml/**")
        .addResourceLocations(
            imagesPathFlink,
            videosPathFlink,
            xmlPathFlink,
            imagesPathSpark,
            videosPathSpark,
            xmlPathSpark);

    registry
        .addResourceHandler("/**")
        .addResourceLocations("classpath:/META-INF/resources/")
        .addResourceLocations("classpath:/resources/")
        .addResourceLocations("classpath:/static/")
        .addResourceLocations("classpath:/public/");

    WebMvcConfigurer.super.addResourceHandlers(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(uploadFileTypeInterceptor)
        .excludePathPatterns(
            Arrays.asList(
                "/flink/app/upload",
                "/resource/upload",
                "/components/**",
                "/js/**",
                "/css/**",
                "/img/**",
                "/images/**",
                "/img/*"));
  }
}
