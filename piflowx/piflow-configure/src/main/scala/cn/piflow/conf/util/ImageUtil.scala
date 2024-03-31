package cn.piflow.conf.util

import com.sksamuel
import com.sksamuel.scrimage
import com.sksamuel.scrimage.Image

import java.io.{BufferedInputStream, File}

object ImageUtil {

  System.setProperty("java.awt.headless", "true")

  def getImage(imagePath: String, bundle: String = ""): Array[Byte] = {
    if (bundle == "") {
      try {
        val classLoader = this.getClass.getClassLoader
        val imageInputStream = classLoader.getResourceAsStream(imagePath)
        val input = new BufferedInputStream(imageInputStream)
        Image.fromStream(input).bytes(sksamuel.scrimage.writer)
      } catch {
        case ex: Exception => {
          println(ex);
          Array[Byte]()
        }
      }
    } else {
      val pluginManager = PluginManager.getInstance
      pluginManager.getConfigurableStopIcon(imagePath, bundle)
    }
  }

  def saveImage(imageBytes: Array[Byte], savePath: String) = {
    Image(imageBytes).output(new File(savePath))(scrimage.writer)
  }

}
