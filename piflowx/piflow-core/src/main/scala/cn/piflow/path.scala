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

package cn.piflow

import scala.collection.mutable.ArrayBuffer

trait PathHead {
  def via(ports: (String, String)): PathVia;

  def to(stopTo: String): Path;
}

trait Path extends PathHead {
  def toEdges(): Seq[Edge];

  def addEdge(edge: Edge): Path;
}

trait PathVia {
  def to(stopTo: String): Path;
}

class PathImpl() extends Path {
  val edges = ArrayBuffer[Edge]();

  override def toEdges(): Seq[Edge] = edges.toSeq;

  override def addEdge(edge: Edge): Path = {
    edges += edge;
    this;
  }

  val thisPath = this;

  override def via(ports: (String, String)): PathVia = new PathVia() {
    override def to(stopTo: String): Path = {
      edges += Edge(edges.last.stopTo, stopTo, ports._1, ports._2);
      thisPath;
    }
  }

  override def to(stopTo: String): Path = {
    edges += Edge(edges.last.stopTo, stopTo, "", "");
    this;
  }
}

case class Edge(stopFrom: String, stopTo: String, outport: String, inport: String) {
  override def toString() = {
    s"[$stopFrom]-($outport)-($inport)-[$stopTo]";
  }
}

object Path {
  def from(stopFrom: String): PathHead = {
    new PathHead() {
      override def via(ports: (String, String)): PathVia = new PathVia() {
        override def to(stopTo: String): Path = {
          val path = new PathImpl();
          path.addEdge(Edge(stopFrom, stopTo, ports._1, ports._2));
          path;
        }
      }

      override def to(stopTo: String): Path = {
        val path = new PathImpl();
        path.addEdge(Edge(stopFrom, stopTo, "", ""));
        path;
      }
    };
  }

  def of(path: (Any, String)): Path = {
    val pi = new PathImpl();

    def _addEdges(path: (Any, String)): Unit = {
      val value1 = path._1;

      value1 match {
        case str: String =>
          // String->String
          pi.addEdge(Edge(str, path._2, "", ""));
        case tuple: (Any, String) =>
          _addEdges(tuple);
          pi.addEdge(Edge(tuple._2, path._2, "", ""));
        case _ =>
          throw new InvalidPathException(value1);
      }
    }

    _addEdges(path)
    pi
  }
}
