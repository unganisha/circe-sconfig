package io.circe.config

import io.circe.config.build.Info
import typings.node.{fsMod, pathMod}

object Resources {

  val testClassesDirectory: String = Info.test_classDirectory.getPath()

  def readResourceFile(fileName: String): String = {
    val file = pathMod.join(testClassesDirectory, fileName)
    if (!fsMod.existsSync(file))
      throw new RuntimeException(s"Unable to find existing resource file at $file")
    fsMod.readFileSync(file).toString
  }

}
