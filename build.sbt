enablePlugins(ScalaJSPlugin)

name := "Keter"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "org.webjars" % "jquery" % "1.10.2"
)

jsDependencies += "org.webjars" % "jquery" % "1.10.2" / "jquery.js"

skip in packageJSDependencies := false

lazy val sitePath = settingKey[String]("Directory for the site")

lazy val site = taskKey[Unit]("Publish site task")

sitePath := "site"

site := {
  import java.nio.file.{Files, StandardCopyOption}
  (fastOptJS in Compile).value
  val targetDirectory = target.value / sitePath.value
  val sourceJS = target.value / "scala-2.11" / "keter-fastopt.js"
  val sourceMap = target.value / "scala-2.11" / "keter-fastopt.js.map"
  val sourceJSDeps = target.value / "scala-2.11" / "keter-jsdeps.js"
  val targetJS = targetDirectory / "keter.js"
  val targetMap = targetDirectory / sourceMap.name
  val targetJSDeps = targetDirectory / sourceJSDeps.name
  targetJS.mkdirs()
  Files.copy(sourceJS.toPath, targetJS.toPath, StandardCopyOption.REPLACE_EXISTING)
  Files.copy(sourceMap.toPath, targetMap.toPath, StandardCopyOption.REPLACE_EXISTING)
  Files.copy(sourceJSDeps.toPath, targetJSDeps.toPath, StandardCopyOption.REPLACE_EXISTING)
  (resourceDirectory in Compile).value.listFiles() foreach { file =>
    val targetFile = targetDirectory / file.name
    Files.copy(file.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)
  }
}