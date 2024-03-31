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
