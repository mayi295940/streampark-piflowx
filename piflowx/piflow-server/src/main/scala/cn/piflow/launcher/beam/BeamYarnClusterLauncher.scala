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

package cn.piflow.launcher.beam

import cn.piflow.Flow
import cn.piflow.util.{FileUtil, FlowFileUtil, PropertyUtil}
import org.apache.flink.client.deployment.{ClusterDeploymentException, ClusterSpecification}
import org.apache.flink.client.deployment.application.ApplicationConfiguration
import org.apache.flink.client.program.ClusterClientProvider
import org.apache.flink.configuration._
import org.apache.flink.configuration.MemorySize.MemoryUnit.MEGA_BYTES
import org.apache.flink.yarn.{YarnClientYarnClusterInformationRetriever, YarnClusterDescriptor}
import org.apache.flink.yarn.configuration.{YarnConfigOptions, YarnDeploymentTarget}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.yarn.api.records.ApplicationId
import org.apache.hadoop.yarn.client.api.YarnClient
import org.apache.hadoop.yarn.conf.YarnConfiguration

import java.util
import java.util.Collections

object BeamYarnClusterLauncher {

  def launch[DataType](flow: Flow[DataType]): String = {

    val flowFileName = flow.getFlowName
    val flowFile = FlowFileUtil.getFlowFilePath(flowFileName)
    FileUtil.writeFile(flow.getFlowJson, flowFile)
    // flink的本地配置目录，为了得到flink的配置
    val configurationDirectory = "/data/flink-1.12.2/conf"
    // 存放flink集群相关的jar包目录
    val flinkLibs = PropertyUtil.getPropertyValue("fs.defaultFS") + "/user/flink/lib"
    // 用户jar
    val userJarPath =
      PropertyUtil.getPropertyValue("fs.defaultFS") + "/user/flink/piflow-server-0.9.jar"
    // String userJarPath = "file://" + ConfigureUtil.getPiFlowBundlePath().replace("\\", Constants.SINGLE_SLASH);
    // 用户依赖的jar
    val flinkDistJar =
      PropertyUtil.getPropertyValue("fs.defaultFS") + "/user/flink/flink-yarn_2.11-1.12.2.jar"
    val yarnClient = YarnClient.createYarnClient
    val entries = new org.apache.hadoop.conf.Configuration
    // entries.addResource(new Path("/data/hadoop-2.6.0/etc/hadoop/core-site.xml"));
    // entries.addResource(new Path("/data/hadoop-2.6.0/etc/hadoop/hdfs-site.xml"));
    // entries.addResource(new Path("/data/hadoop-2.6.0/etc/hadoop/yarn-site.xml"));
    entries.set(
      "yarn.resourcemanager.hostname",
      PropertyUtil.getPropertyValue("yarn.resourcemanager.hostname"))
    entries.set("fs.defaultFS", PropertyUtil.getPropertyValue("fs.defaultFS"))
    val yarnConfiguration = new YarnConfiguration(entries)
    yarnClient.init(yarnConfiguration)
    yarnClient.start()
    val clusterInformationRetriever = YarnClientYarnClusterInformationRetriever.create(yarnClient)

    // 获取flink的配置
    val flinkConfiguration = GlobalConfiguration.loadConfiguration(configurationDirectory)
    // flinkConfiguration.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true)
    flinkConfiguration.set(PipelineOptions.JARS, Collections.singletonList(userJarPath))

    val remoteLib = new Path(flinkLibs)
    flinkConfiguration.set(
      YarnConfigOptions.PROVIDED_LIB_DIRS,
      Collections.singletonList(remoteLib.toString))
    flinkConfiguration.set(YarnConfigOptions.FLINK_DIST_JAR, flinkDistJar)
    val shipFiles = new util.ArrayList[String]
    shipFiles.add(flowFile)
    flinkConfiguration.set(YarnConfigOptions.SHIP_FILES, shipFiles)
    // 设置为application模式
    flinkConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName)
    // yarn application name
    flinkConfiguration.set(YarnConfigOptions.APPLICATION_NAME, flow.getFlowName)
    flinkConfiguration.set(
      JobManagerOptions.TOTAL_PROCESS_MEMORY,
      MemorySize.parse("1024", MEGA_BYTES))
    flinkConfiguration.set(
      TaskManagerOptions.TOTAL_PROCESS_MEMORY,
      MemorySize.parse("1024", MEGA_BYTES))
    val clusterSpecification =
      new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification
    // 设置用户jar的参数和主类
    val args = Array(flow.getFlowName)
    val applicationClassName = "cn.piflow.api.StartFlowMain"
    val appConfig = new ApplicationConfiguration(args, applicationClassName)
    val yarnClusterDescriptor = new YarnClusterDescriptor(
      flinkConfiguration,
      yarnConfiguration,
      yarnClient,
      clusterInformationRetriever,
      true)
    var clusterClientProvider: ClusterClientProvider[ApplicationId] = null
    try
      clusterClientProvider =
        yarnClusterDescriptor.deployApplicationCluster(clusterSpecification, appConfig)
    catch {
      case e: ClusterDeploymentException =>
        e.printStackTrace()
    }
    val clusterClient = clusterClientProvider.getClusterClient
    val applicationId = clusterClient.getClusterId
    println(applicationId)
    applicationId.toString
  }
}
