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

class KeywordStatus(
    val keywordFrequency: Int,
    val absTextFrequency: Int,
    val titleTextFrequency: Int,
    val keywordAmount: Int,
    val isNewWord: Boolean)
  extends Serializable {
  val percentage: Double = keywordFrequency / keywordAmount
  val weight: Double = KeywordStatus.weight(keywordFrequency, absTextFrequency, titleTextFrequency)
  val count: Int = keywordFrequency + absTextFrequency + titleTextFrequency

}
object KeywordStatus {
  def weight(keywordCount: Int, abstractCount: Int, titleCount: Int): Double = {
    1.0 * keywordCount + 0.2 * abstractCount + 0.4 * titleCount
  }
}
