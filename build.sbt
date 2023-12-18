import Dependencies.*
import Dependencies.{scalaTest, newtype, `cats-effect`, h2, logback, liquibase, quill, config, circe, tapir, doobie, sttp, tethys, tofu}
ThisBuild / scalaVersion     := "2.13.11"
ThisBuild / version          := "0.1.0-SNAPSHOT"

Compile / compile / scalacOptions ++= Seq(
  "-Werror",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused",
  "-Wvalue-discard",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Xlint:-implicit-recursion",
  "-unchecked",
)

lazy val root = (project in file("."))
  .settings(
    name := "CRUD",
    libraryDependencies ++= List(
      scalaTest,
      newtype,
      `cats-effect`,
      h2,
      logback,
      liquibase,
      quill,
      config
    ) ++ circe.modules ++ tapir.modules ++ doobie.modules ++ sttp.modules ++ tethys.modules ++ tofu.modules,
      scalacOptions += "-Ymacro-annotations",
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "migrations"
  )

