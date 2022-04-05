package io.circe.config

import typings.node.{fsMod, pathMod}

object Resources extends ResourceBase {

  def listFiles(directory: String): List[String] =
    fsMod.readdirSync(directory).map(pathMod.join(directory, _)).toList

  def readResourceFile(fileName: String): String = {
    val file = pathMod.join(testClassesDirectory, fileName)
    readFile(file)
  }

  def readFile(file: String): String = {
    if (!fsMod.existsSync(file))
      throw new RuntimeException(s"Unable to find existing resource file at $file")
    fsMod.readFileSync(file).toString
  }

}
