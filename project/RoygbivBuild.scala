import sbt._
import Keys._

object RoygbivBuild extends Build {
  val Organization = "roygbiv"
  val Version      = "1.0-SNAPSHOT"
  val ScalaVersion = "2.9.1"

  lazy val atmos = Project(
    id = "roygbiv",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.roygbiv
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion
  )

  lazy val defaultSettings = buildSettings ++ Seq(    
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-optimise", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    // disable parallel tests
    parallelExecution in Test := false
  )
}

object Dependencies {
  import Dependency._

  val roygbiv = Seq(akkaKernel, akkaRemote, sjson, slf4j, logback, scalatest, junit)
}

object Dependency {

  object Version {
    val Akka      = "1.2"
    val Scalatest = "1.6.1"
    val Slf4j     = "1.6.0"
    val JUnit     = "4.5"
    val Logback   = "0.9.24"
    val Sjson     = "0.15"
  }

  val akkaKernel	  = "se.scalablesolutions.akka" % "akka-kernel"        % Version.Akka
  val akkaRemote    = "se.scalablesolutions.akka" % "akka-remote"        % Version.Akka
  val slf4j         = "org.slf4j"                 % "slf4j-api"          % Version.Slf4j
  val logback       = "ch.qos.logback"            % "logback-classic"    % Version.Logback
  val sjson         = "net.debasishg"             % "sjson_2.9.1"        % Version.Sjson

  // ---- Test dependencies ----

  val scalatest   = "org.scalatest"       % "scalatest_2.9.0"          % Version.Scalatest 	% "test"
  val junit       = "junit"               % "junit"                    % Version.JUnit    	% "test"
}
