package cn.piflow.launcher.flink

import cn.piflow.Flow
import cn.piflow.util.{ConfigureUtil, PropertyUtil}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object FlinkLauncher {

  def launchYarnSession[DataType](flow: Flow[DataType]): StreamExecutionEnvironment = {

    val env = StreamExecutionEnvironment.createRemoteEnvironment(
      PropertyUtil.getPropertyValue("flink.host"),
      PropertyUtil.getPropertyValue("flink.port").toInt,
      ConfigureUtil.getPiFlowBundlePath())

    env
  }

  def launchYarnCluster[DataType](flow: Flow[DataType]): String = {
    val appId = FlinkYarnClusterLauncher.launch(flow)
    appId
  }

  /*def launchYarnCluster(flow: Flow) : String = {

    val flinkHomeDir = "E:\\project\\flink-1.12.2"
    val piflowBundle = ConfigureUtil.getPiFlowBundlePath()
    val dependencyClassPathList: util.List[URL] = new util.ArrayList[URL]
    val mainClass = "cn.piflow.api.StartFlinkFlowMain"
    var flowJson = flow.getFlowJson()
    val packagedProgram = new PackagedProgram(new File(piflowBundle),dependencyClassPathList,mainClass,flowJson)

    val flinkConfDir = flinkHomeDir + "/conf"
    val flinkLibDir = flinkHomeDir + "/lib"

    val flinkConfiguration = GlobalConfiguration.loadConfiguration(flinkConfDir)

    val yarnConfiguration = new YarnConfiguration()
    yarnConfiguration.set("yarn.resourcemanager.hostname",  PropertyUtil.getPropertyValue("yarn.resourcemanager.hostname"))
    yarnConfiguration.set("fs.defaultFS",PropertyUtil.getPropertyValue("fs.defaultFS"))

    val jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, flinkConfiguration,1,false)

    val flinkLibJars = new File(flinkLibDir).listFiles()
    if(flinkLibJars != null){
      for( jar <- flinkLibJars){
        val url = jar.toURI.toURL
        if(!url.toString.contains("flink-dist")){
          jobGraph.addJar(new  org.apache.flink.core.fs.Path(url.toString))
        }
      }
    }

    val clusterSpecification = createClusterSpecification("")

    //创建Flink的YarnClusterDescriptor
    val yarnClusterDescriptor = createYarnClusterDescriptor(flinkConfiguration, yarnConfiguration, flinkLibDir, flinkConfDir)


    val clusterClient = yarnClusterDescriptor.deployJobCluster(clusterSpecification, jobGraph, true)
    val applicationId = clusterClient.getClusterClient.getClusterId.toString
    val flinkJobId = jobGraph.getJobID.toString
    //任务成功提交之后会返回一个applicationId
    System.out.println("applicationId:" + applicationId + ",flinkJobId=" + flinkJobId)

    applicationId
  }

  def createClusterSpecification(resourceConfig :String ) = {
    var jobmanagerMemoryMb = 1024
    var taskmanagerMemoryMb = 1024
    var numberTaskManagers = 1
    var slotsPerTaskManager = 1

    /*if (StringUtils.isNotBlank(resourceConfig)) {

      val config = JSON.parseFull(resourceConfig) match {
        case Some(x)  => x
        case None => throw new IllegalArgumentException
      }

      //val config = JSON.parseObject(resourceConfig)
      numberTaskManagers = config.getString("container").toInt
      slotsPerTaskManager = config.getString("core").toInt
      taskmanagerMemoryMb = config.getString("memoryGB").toInt * 1024
    }*/

    /*if (StringUtils.isNotBlank(jobManager))
      jobmanagerMemoryMb = jobManager.toInt*/

    val ret =  new ClusterSpecification.ClusterSpecificationBuilder()
      .setMasterMemoryMB(jobmanagerMemoryMb)
      .setTaskManagerMemoryMB(taskmanagerMemoryMb)
      .setSlotsPerTaskManager(slotsPerTaskManager)
      .createClusterSpecification
    ret
  }

  def createYarnClusterDescriptor (flinkConfiguration: Configuration, yarnConfiguration: YarnConfiguration, flinkLibDir: String, flinkConfDir: String) : YarnClusterDescriptor=
  {
    val yarnClient = YarnClient.createYarnClient
    yarnClient.init(yarnConfiguration)
    yarnClient.start()

    val yarnClusterInformationRetriever = YarnClientYarnClusterInformationRetriever.create(yarnClient);
    val yarnClusterDescriptor = new YarnClusterDescriptor(flinkConfiguration, yarnConfiguration,  yarnClient, yarnClusterInformationRetriever,false)
    //将flink的dist jar添加到YarnClusterDescriptor
    val flinkLibJars = new File(flinkLibDir).listFiles
    if (flinkLibJars != null) {

      val loop = new Breaks
      loop.breakable{
        for (jar <- flinkLibJars) {
          val url = jar.toURI.toURL
          if (url.toString.contains("flink-dist")) {
            yarnClusterDescriptor.setLocalJarPath(new Path(url.toString))
            loop.break()
          }
        }
      }
    }
     yarnClusterDescriptor
  }

  class YarnClusterInformationRetrieverImpl extends YarnClusterInformationRetriever{
    override def getMaxVcores: Int = {
      return 2
    }
  }*/

}
