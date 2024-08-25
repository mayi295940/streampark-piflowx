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

import javax.script.{Compilable, ScriptEngineManager}

import java.util.{Map => JMap}

import scala.collection.JavaConversions
import scala.collection.JavaConversions._
import scala.collection.immutable.StringOps
import scala.collection.mutable.{ArrayBuffer, Map => MMap}

trait ScriptEngine {
  def compile(funcText: String, nums: Int = 1): CompiledFunction;
}

trait CompiledFunction {
  def invoke(args: Seq[Any]): Any;
}

trait FunctionLogic {
  def perform(value: Seq[Any]): Any;
}

object ScriptEngine {
  val JAVASCRIPT = "javascript";
  val SCALA = "scala";
  val engines = Map[String, ScriptEngine](JAVASCRIPT -> new JavaScriptEngine());

  def logic(script: String, lang: String = ScriptEngine.JAVASCRIPT): FunctionLogic =
    new FunctionLogic with Serializable {
      val cached = ArrayBuffer[CompiledFunction]();

      override def perform(args: Seq[Any]): Any = {
        if (cached.isEmpty) {
          try {
            val engine = ScriptEngine.get(lang);
            cached += engine.compile(script);
          } catch {
            case e: Throwable =>
              throw new ScriptExecutionException(e, script, args);
          }
        }

        try {
          cached(0).invoke(args);
        } catch {
          case e: Throwable =>
            throw new ScriptExecutionException(e, script, args);
        };
      }
    }

  def get(lang: String) = engines(lang);
}

class JavaScriptEngine extends ScriptEngine {
  val engine = new ScriptEngineManager().getEngineByName("javascript");

  val tools = {
    val map = MMap[String, AnyRef]();
    map += "$" -> Predef;
    map.toMap;
  }

  def compile(funcText: String, nums: Int): CompiledFunction = {
    val args = (1 to nums).map("arg" + _).mkString(",");
    val wrapped = s"($funcText)($args)";
    new CompiledFunction() {
      val compiled = engine.asInstanceOf[Compilable].compile(wrapped);

      def invoke(args: Seq[Any]): Any = {
        val bindings = engine.createBindings();
        bindings.asInstanceOf[JMap[String, Any]].putAll(tools);
        bindings
          .asInstanceOf[JMap[String, Any]]
          .putAll(
            JavaConversions.mapAsJavaMap(
              args.zip(1 to args.length).map(x => ("arg" + x._2, x._1)).toMap));

        val value = compiled.eval(bindings);
        value;
      }
    }
  }
}

object Predef {
  def StringOps(s: String) = new StringOps(s);

  def Row(value1: Any) = _row(value1);

  def Row(value1: Any, value2: Any) = _row(value1, value2);

  // todo
  //  private def _row(values: Any*) = org.apache.spark.sql.Row(values: _*);
  private def _row(values: Any*) = null

  def Row(value1: Any, value2: Any, value3: Any) = _row(value1, value2, value3);

  def Row(value1: Any, value2: Any, value3: Any, value4: Any) =
    _row(value1, value2, value3, value4);

  def Array() = new java.util.ArrayList();
}

class ScriptExecutionException(cause: Throwable, sourceScript: String, args: Any)
  extends RuntimeException(s"script execution error, script: $sourceScript, args: $args", cause) {}

class ScriptCompilationException(cause: Throwable, sourceScript: String)
  extends RuntimeException(s"script compilation error, script: $sourceScript", cause) {}
