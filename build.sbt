organization := "io.github.unganisha"
homepage := Some(url("https://github.com/unganisha/circe-sconfig"))
licenses += "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
scmInfo := Some(
  ScmInfo(
    url("https://github.com/unganisha/circe-sconfig"),
    "scm:git:git@github.com:unganisha/circe-sconfig.git"
  )
)

val Versions = new {
  val scala2 = "2.13.8"
  val catsEffect = "2.5.4"
  val circe = "0.14.1"
  val sconfig = "1.4.9"
  val discipline = "1.4.0"
  val scalaCheck = "1.15.4"
  val scalaTest = "3.2.11"
  val scalaTestPlus = "3.2.11.0"
  val sjavatime = "1.1.9"
}

val commonSettings = Seq(
  organization := (LocalRootProject / organization).value,
  crossScalaVersions := (LocalRootProject / crossScalaVersions).value,
  scalaVersion := (LocalRootProject / scalaVersion).value,
  homepage := (LocalRootProject / homepage).value,
  licenses := (LocalRootProject / licenses).value,
  scmInfo := (LocalRootProject / scmInfo).value,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:postfixOps",
    "-language:higherKinds",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xsource:3",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:imports"
  ),
  Compile / console / scalacOptions --= Seq("-Ywarn-unused-import", "-Ywarn-unused:imports"),
  Test / console / scalacOptions := (Compile / console / scalacOptions).value
)

val commonJvmSettings = Seq(
  doctestTestFramework := DoctestTestFramework.ScalaTest,
  doctestMarkdownEnabled := true,
  Test / fork := true,
  Test / javaOptions := Seq("-Xmx3G")
)

val commonJsSettings = Seq(
  doctestGenTests := Nil
)

val notPublished = Seq(
  publish := Unit,
  publishLocal := Unit,
  publishArtifact := false
)

val publishSettings = Seq(
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := (_ => false),
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  developers := List(
    Developer("jonas", "Jonas Fonseca", "jonas.fonseca@gmail.com", url("https://github.com/jonas"))
  )
)

val docSettings = Seq(
  autoAPIMappings := true,
  Compile / doc / scalacOptions := Seq(
    "-groups",
    "-implicits",
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath
  )
)

val buildServerSettings = List(
  githubWorkflowScalaVersions := (LocalRootProject / crossScalaVersions).value,
  githubWorkflowJavaVersions := Seq(
    JavaSpec.graalvm("21.3.0", "11"),
    JavaSpec.graalvm("21.3.0", "17")
  ),
  githubWorkflowBuildMatrixAdditions += "platform" -> List("jvm", "js"),
  githubWorkflowPublishTargetBranches := Nil,
  githubWorkflowArtifactUpload := false,
  githubWorkflowBuildMatrixFailFast := Some(false),
  githubWorkflowUseSbtThinClient := false,
  githubWorkflowBuildPreamble := List(
    WorkflowStep.Sbt(
      List("scalafmtCheckAll"),
      Some("verify-formatting"),
      Some("Verify Code Formatting")
    )
  ),
  githubWorkflowBuild := List(
    WorkflowStep.Sbt(
      List("circe-sconfig/test"),
      id = Some("execute-jvm-tests"),
      name = Some("Execute JVM Platform Unit Tests"),
      cond = Some("matrix.platform == 'jvm'")
    ),
    WorkflowStep.Sbt(
      List("circe-sconfigJS/test"),
      id = Some("execute-js-tests"),
      name = Some("Execute Javascript Platform Unit Tests"),
      cond = Some("matrix.platform == 'js'")
    )
  )
)

val versionSettings =
  versionWithGit :+ (git.useGitDescribe := true)

val scalaVersionSettings = Seq(
  scalaVersion := (LocalRootProject / scalaVersion).value,
  crossScalaVersions := (LocalRootProject / crossScalaVersions).value
)

lazy val localRoot =
  (project in file("."))
    .aggregate(`circe-sconfig`.jvm, `circe-sconfig`.js)
    .settings(notPublished)
    .settings(
      crossScalaVersions := List(Versions.scala2),
      scalaVersion := crossScalaVersions.value.last
    )

lazy val `circe-sconfig` =
  crossProject(JVMPlatform, JSPlatform)
    .withoutSuffixFor(JVMPlatform)
    .in(file("core"))
    .settings(commonSettings)
    .jvmSettings(commonJvmSettings)
    .jsSettings(commonJsSettings)
    .jsConfigure(_.enablePlugins(ScalablyTypedConverterPlugin))
    .jsSettings(
      Test / npmDependencies ++= fromPackageJson(
        "@types/node"
      ).value,
      libraryDependencies ++= Seq(
        "org.ekrich" %%% "sjavatime" % Versions.sjavatime % Test,
        ("org.scala-js" %%% "scalajs-weakreferences" % "1.0.0" % Test).cross(CrossVersion.for3Use2_13)
      )
    )
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys ++= Seq(Test / classDirectory),
      buildInfoPackage := "io.circe.config.build",
      buildInfoObject := "Info"
    )
    .settings(
      description := "Yet another Typesafe Config decoder",
      libraryDependencies ++= Seq(
        "org.ekrich" %%% "sconfig" % Versions.sconfig,
        "io.circe" %%% "circe-core" % Versions.circe,
        "io.circe" %%% "circe-parser" % Versions.circe,
        "io.circe" %%% "circe-generic" % Versions.circe % Test,
        "io.circe" %%% "circe-testing" % Versions.circe % Test,
        "org.typelevel" %%% "cats-effect" % Versions.catsEffect % Test,
        "org.typelevel" %%% "discipline-core" % Versions.discipline % Test,
        "org.scalacheck" %%% "scalacheck" % Versions.scalaCheck % Test,
        "org.scalatest" %%% "scalatest" % Versions.scalaTest % Test,
        "org.scalatestplus" %%% "scalacheck-1-15" % Versions.scalaTestPlus % Test
      )
    )

enablePlugins(GitPlugin)
inThisBuild(versionSettings)
inThisBuild(scalaVersionSettings)
inThisBuild(buildServerSettings)
