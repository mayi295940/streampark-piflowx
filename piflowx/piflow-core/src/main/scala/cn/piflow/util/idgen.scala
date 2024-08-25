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

package cn.piflow.util

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable.{Map => MMap}

object IdGenerator {

  val map: MMap[String, AtomicInteger] = MMap[String, AtomicInteger]()

  def uuid: String = UUID.randomUUID().toString

  def uuidWithoutSplit: String = uuid.replaceAll("-", "")

//  def nextId[T](implicit manifest: Manifest[T]): Int =
//    map.getOrElseUpdate(manifest.runtimeClass.getName,
//      new AtomicInteger()).incrementAndGet()

  def nextId[T]: Int =
    map.getOrElseUpdate(manifest.runtimeClass.getName, new AtomicInteger()).incrementAndGet()
}
