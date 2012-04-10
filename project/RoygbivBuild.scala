import sbt._
import Keys._
import com.typesafe.sbtscalariform.ScalariformPlugin
import com.typesafe.sbtscalariform.ScalariformPlugin.ScalariformKeys

object RoygbivBuild extends Build {
  val Organization = "roygbiv"
  val Version      = "1.0-SNAPSHOT"
  val ScalaVersion = "2.9.1"

  lazy val parentSettings = buildSettings

  lazy val roygbiv = Project(
    id = "roygbiv",
    base = file("."),
    settings = parentSettings,
    aggregate = Seq(shared, server, client)
  )

  lazy val shared = Project(
    id = "shared",
    base = file("shared"),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.roygbiv)
  )

  lazy val server = Project(
    id = "server",
    base = file("server"),
    dependencies = Seq(shared),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.roygbiv
    )
  )

  lazy val client = Project(
    id = "client",
    base = file("client"),
    dependencies = Seq(shared),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.roygbiv
    )
  )

  lazy val web = Project(
    id = "web",
    base = file("web"),
    dependencies = Seq(shared, server),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.roygbiv ++ Dependencies.playLibs)
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion
  )

  lazy val defaultSettings = buildSettings ++ formatSettings ++ Seq(
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-optimise", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    // disable parallel tests
    parallelExecution in Test := false
  )

  lazy val formatSettings = ScalariformPlugin.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}

object Dependencies {
  import Dependency._

  val roygbiv = Seq(akkaActor, akkaKernel, akkaRemote, sjson, slf4j, logback, scalatest, junit)

  val playLibs = Seq(play)
}

object Dependency {

  object Version {
    val Akka      = "2.0"
    val Scalatest = "1.6.1"
    val Slf4j     = "1.6.0"
    val JUnit     = "4.5"
    val Logback   = "0.9.24"
    val Sjson     = "0.15"
    val Play      = "2.0"
  }

  val akkaActor	    = "com.typesafe.akka"         % "akka-actor"          % Version.Akka
  val akkaKernel	  = "com.typesafe.akka"         % "akka-kernel"         % Version.Akka
  val akkaRemote    = "com.typesafe.akka"         % "akka-remote"         % Version.Akka
  val slf4j         = "org.slf4j"                 % "slf4j-api"           % Version.Slf4j
  val logback       = "ch.qos.logback"            % "logback-classic"     % Version.Logback
  val sjson         = "net.debasishg"             % "sjson_2.9.1"         % Version.Sjson
  val play          = "play"                      % "play_2.9.1"          % Version.Play

  // ---- Test dependencies ----

  val scalatest   = "org.scalatest"       % "scalatest_2.9.0"          % Version.Scalatest 	% "test"
  val junit       = "junit"               % "junit"                    % Version.JUnit    	% "test"
}
