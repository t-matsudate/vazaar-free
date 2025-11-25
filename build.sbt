lazy val commonSettings = Seq(
  organization := "io.github.t-matsudate",
  scalaVersion := "3.7.4",
  libraryDependencies ++= Seq(
    "org.scala-lang" %% "scala3-library" % "3.7.4",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.specs2" %% "specs2-core" % "5.7.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.19.0" % Test,
    "org.specs2" %% "specs2-scalacheck" % "5.7.0" % Test,
    "org.eclipse.jetty" % "jetty-server" % "12.1.4" % "provided",
    "jakarta.servlet" % "jakarta.servlet-api" % "6.1.0" % "provided",
  )
)

lazy val core = (project in file("core"))
  .settings(
    name := "vazaar-core",
    version := "0.0.1",
    commonSettings,
  )
