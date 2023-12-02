// See README.md for license details.

scalaVersion     := "2.13.8"
version          := "0.1.0"
organization     := "FLF"

val chiselVersion = "3.5.4"

lazy val root = (project in file("."))
  .settings(
    name := "PimpMyCounter",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.5.4" % "test",
      "Martoni" %% "fpgamacro" % "0.2.1-SNAPSHOT",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
  )
