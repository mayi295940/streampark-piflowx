/*
 * Copyright (c) 2021 The StreamX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.streamxhub.streamx.flink.kubernetes.model

/**
 * Pod template for flink k8s cluster
 *
 * @author Al-assad
 */
case class K8sPodTemplates(podTemplate: String = "", jmPodTemplate: String = "", tmPodTemplate: String = "")

object K8sPodTemplates {

  def empty: K8sPodTemplates = new K8sPodTemplates()

  def of(podTemplate: String, jmPodTemplate: String, tmPodTemplate: String): K8sPodTemplates =
    K8sPodTemplates(safeGet(podTemplate), safeGet(jmPodTemplate), safeGet(tmPodTemplate))

  private[this] def safeGet(content: String) = {
    content match {
      case null => ""
      case x if x.trim.isEmpty => ""
      case x => x
    }
  }

}