package org.apache.streampark.console.flow.third.service;


import org.apache.streampark.console.flow.third.vo.sparkJar.SparkJarVo;

public interface ISparkJar {

  public String getSparkJarPath();

  public SparkJarVo mountSparkJar(String sparkJarName);

  public SparkJarVo unmountSparkJar(String sparkJarMountId);
}
