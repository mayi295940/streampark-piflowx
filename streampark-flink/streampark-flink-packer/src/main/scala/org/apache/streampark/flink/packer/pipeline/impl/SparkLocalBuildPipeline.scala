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

package org.apache.streampark.flink.packer.pipeline.impl

import org.apache.streampark.common.enums.SparkJobType
import org.apache.streampark.common.fs.{FsOperator, LfsOperator}
import org.apache.streampark.common.util.Implicits._
import org.apache.streampark.flink.packer.maven.MavenTool
import org.apache.streampark.flink.packer.pipeline._

import java.io.{File, IOException}

/** Building pipeline for spark local application mode */
class SparkLocalBuildPipeline(request: SparkLocalBuildRequest)
  extends BuildPipeline {

  /** the type of pipeline */
  override def pipeType: PipelineTypeEnum =
    PipelineTypeEnum.SPARK_LOCAL

  override def offerBuildParam: SparkLocalBuildRequest = request

  /**
   * the actual build process. the effective steps progress should be implemented in multiple
   * BuildPipeline.execStep() functions.
   */
  /**
   * the actual build process. the effective steps progress should be implemented in multiple
   * BuildPipeline.execStep() functions.
   */
  @throws[Throwable]
  override protected def buildProcess(): ShadedBuildResponse = {
    execStep(1) {
      request.jobType match {
        case SparkJobType.SPARK_SQL =>
          LfsOperator.mkCleanDirs(request.localWorkspace)
        case _ =>
      }
      logInfo(s"Recreate building workspace: ${request.localWorkspace}")
    }.getOrElse(throw getError.exception)

    val mavenJars =
      execStep(2) {
        request.jobType match {
          case SparkJobType.SPARK_SQL =>
            val mavenArts =
              MavenTool.resolveArtifacts(request.dependencyInfo.mavenArts)
            mavenArts.map(_.getAbsolutePath) ++ request.dependencyInfo.extJarLibs
          case _ => List[String]()
        }
      }.getOrElse(throw getError.exception)

    execStep(3) {
      mavenJars.foreach(jar => {
        uploadJarToLfs(FsOperator.lfs, jar, request.localWorkspace)
      })
    }.getOrElse(throw getError.exception)

    ShadedBuildResponse(request.localWorkspace, request.customSparkUserJar)
  }

  @throws[IOException]
  private[this] def uploadJarToLfs(
      fsOperator: FsOperator,
      origin: String,
      target: String): Unit = {
    val originFile = new File(origin)
    if (!fsOperator.exists(target)) {
      fsOperator.mkdirs(target)
    }
    if (originFile.isFile) {
      // check file in upload dir
      fsOperator match {
        case FsOperator.lfs =>
          fsOperator.copy(originFile.getAbsolutePath, target)
      }
    } else {
      fsOperator match {
        case _ =>
      }
    }
  }

}

object SparkLocalBuildPipeline {
  def of(request: SparkLocalBuildRequest): SparkLocalBuildPipeline =
    new SparkLocalBuildPipeline(request)
}
