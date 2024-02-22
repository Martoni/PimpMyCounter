// See README.md for license details.

scalaVersion     := "2.13.8"
version          := "0.1.0"
organization     := "FLF"

val chiselVersion = "3.5.6"

lazy val root = (project in file("."))
  .settings(
    name := "PimpMyCounter",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.5.6" % "test",
      "Martoni" %% "fpgamacro" % "0.2.2",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
  )
