package io.circe.config

import java.io.File
import scala.io.Source

object Resources extends ResourceBase {

  def listFiles(directory: String): List[String] = new File(directory).listFiles.map(_.getPath).toList

  def resourceFile(fileName: String): File = new File(testClassesDirectory, fileName)

  def readResourceFile(fileName: String): String = readFile(resourceFile(fileName).getPath)

  def readFile(file: String): String = {
    val source = Source.fromFile(file)
    val lines = source.getLines().mkString("\n")
    source.close()
    lines
  }
}
