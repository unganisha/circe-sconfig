import _root_.io.circe
import _root_.io.circe.generic.auto._
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt.{Def, _}


object NpmDependenciesPlugin extends AutoPlugin {

  override def trigger = allRequirements

  override def requires: Plugins = ScalaJSPlugin

  override def buildSettings: Seq[Setting[_]] = {
    autoImport.packageJsonDependencies := {
      case class PackageJson(dependencies: Map[String, String])
      val packageJson =
        circe.parser.decode[PackageJson](IO.read((LocalRootProject / baseDirectory).value / "package.json"))
          .getOrElse(throw new RuntimeException("Unable to decode package.json"))
      packageJson.dependencies
    }
  }

  object autoImport {
    val packageJsonDependencies = settingKey[Map[String, String]]("Collection of dependencies defined in package.json")

    def fromPackageJson(dependencies: String*): Def.Initialize[Seq[(String, String)]] = Def.setting {
      val parsed = packageJsonDependencies.value
      dependencies.map(dep => dep -> parsed(dep))
    }
  }


}
