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

package cn.piflow.bundle.spark.util.objects

class Result(val resultSeq: Seq[(Hierarchy, KeywordStatus, String)], val keywordName: String)
  extends Serializable {
  def print: Seq[(String, String, String, String, Int, Double, Double, Int, Int, Int)] = {
    resultSeq.map(f => {
      (
        f._1.ApplyID,
        f._1.FOS,
        f._3,
        keywordName,
        f._2.count,
        f._2.percentage,
        f._2.weight,
        f._2.titleTextFrequency,
        f._2.absTextFrequency,
        f._2.keywordFrequency)
    })
  }
}
