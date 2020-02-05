enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

name := "Keter"

version := "0.0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.lihaoyi" %%% "utest" % "0.7.2",
  "org.scala-js" %%% "scalajs-dom" % "0.9.8",
  "org.webjars" % "rot.js" % "0.5.0",
  "org.scala-lang.modules" %%% "scala-collection-contrib" % "0.2.1"
)

npmDependencies in Compile += "rot-js" -> "0.6.2"
npmDependencies in Compile += "webpack" -> "1.14.0"

scalaJSUseMainModuleInitializer := true

testFrameworks += new TestFramework("utest.runner.Framework")

skip in packageJSDependencies := false

lazy val sitePath = settingKey[String]("Directory for the site")

lazy val site = taskKey[Unit]("Publish site task")

sitePath := "site"

site := {
  import java.nio.file.{Files, StandardCopyOption}
  (webpack in fullOptJS in Compile).value
  val targetDirectory = target.value / sitePath.value
  val sourceJS = target.value / "scala-2.13" / "scalajs-bundler" / "main" / "keter-opt-bundle.js"
  val sourceMap = target.value / "scala-2.13" / "scalajs-bundler" / "main" / "keter-opt-bundle.js.map"
  val targetJS = targetDirectory / "keter.js"
  val targetMap = targetDirectory / sourceMap.name
  targetJS.mkdirs()
  Files.copy(sourceJS.toPath, targetJS.toPath, StandardCopyOption.REPLACE_EXISTING)
  Files.copy(sourceMap.toPath, targetMap.toPath, StandardCopyOption.REPLACE_EXISTING)
  (resourceDirectory in Compile).value.listFiles() foreach { file =>
    val targetFile = targetDirectory / file.name
    Files.copy(file.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)
  }
}
