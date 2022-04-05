package io.circe.config

import io.circe.config.build.Info

abstract class ResourceBase {

  val testClassesDirectory: String = Info.test_classDirectory.getPath

  val sharedTestResourcesDirectory: String =
    Info.test_resourceDirectory.getPath.replaceFirst("([/\\\\])js|jvm([/\\\\])", "$1shared$2")

  def listFiles(directory: String): List[String]

  def readResourceFile(fileName: String): String

  def readFile(file: String): String
}
