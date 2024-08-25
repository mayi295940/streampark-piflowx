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

/**
 * Here are four common pitfalls2 that can cause inconsistent behavior when overriding equals:
 *   1. Defining equals with the wrong signature. 2. Changing equals without also changing hashCode.
 *      3. Defining equals in terms of mutable fields. 4. Failing to define equals as an equivalence
 *      relation.
 *
 * @param FOS
 * @param ApplyID
 */
class Hierarchy(val FOS: String, val ApplyID: String) extends Serializable {
  override def hashCode(): Int = {
    (FOS + ApplyID).hashCode
  }

  override def equals(obj: Any): Boolean = {
    if (obj == null) false
    else {
      if (!obj.isInstanceOf[Hierarchy]) {
        false
      } else {
        val tmp = obj.asInstanceOf[Hierarchy]
        if (tmp.ApplyID == this.ApplyID && tmp.FOS == this.FOS) true else false
      }
    }
  }

  override def clone(): AnyRef = {
    new Hierarchy(FOS, ApplyID)
  }

  override def toString: String = {
    s"FOS : $FOS , ApplyID : $ApplyID"
  }
}

class HierarchyKeyword(val keyword: String, val hierarchy: Hierarchy) extends Serializable {
  override def hashCode(): Int = {
    (keyword + hierarchy.FOS + hierarchy.ApplyID).hashCode
  }

  override def equals(obj: Any): Boolean = {
    if (obj == null) false
    else {
      if (!obj.isInstanceOf[HierarchyKeyword]) {
        false
      } else {
        val tmp = obj.asInstanceOf[HierarchyKeyword]
        if (tmp.hierarchy.ApplyID == this.hierarchy.ApplyID &&
          tmp.hierarchy.FOS == this.hierarchy.FOS &&
          tmp.keyword == this.keyword) true
        else false
      }
    }
  }

  override def clone(): AnyRef = {
    new HierarchyKeyword(keyword, new Hierarchy(this.hierarchy.FOS, this.hierarchy.ApplyID))
  }

  override def toString: String = {
    s"keyword info => \n" +
      s"name : ${this.keyword} , FOS : ${this.hierarchy.FOS} , ApplyID : ${this.hierarchy.ApplyID}"
  }

  def toSimpleHash: Int = {
    (this.keyword + hierarchy.FOS).hashCode
  }
}
