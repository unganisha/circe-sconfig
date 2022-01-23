package io.circe.config

import io.circe.config.build.Info

import java.io.File
import scala.io.Source

object Resources {

  val testClassesDirectory: String = Info.test_classDirectory.getPath

  def readResourceFile(fileName: String): String = {
    val source = Source.fromFile(new File(testClassesDirectory, fileName))
    val lines = source.getLines().mkString("\n")
    source.close()
    lines
  }

}
