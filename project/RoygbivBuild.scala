import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.ec2.model.RunInstancesRequest
import sbt._
import Keys._
import akka.atmos.provisioning._
import DeployPlugin._
import amazon.EC2Deploy

object RoygbivBuild extends Build {
  val Organization = "roygbiv"
  val Version      = "1.0-SNAPSHOT"
  val ScalaVersion = "2.9.1"

  lazy val parentSettings = buildSettings

  lazy val deployDescriptor = new EC2Deploy with SSHUpload {
    val credentials = new PropertiesCredentials(new java.io.File("project/aws.properties"))
    val startupOptions = () => (new RunInstancesRequest).withImageId("ami-9e2e1cea")

    val ec2endpoint = "https://ec2.eu-west-1.amazonaws.com"
    val remoteUser = "ec2-user"
    override val extraArgs = "-i /apps/aws/ec2/Akka_Host.pem"
    val roygbivAlloc = Alloc("roygbiv", 3, startupOptions)

    val svc = Service(
      name = "master",
      launcher = ssh(Seq("./shared/target/scala-2.9.1/shared_2.9.1-1.0-SNAPSHOT.jar",
        "./server/target/scala-2.9.1/server_2.9.1-1.0-SNAPSHOT.jar",
        "./client/target/scala-2.9.1/client_2.9.1-1.0-SNAPSHOT.jar")),
      cmd = "./dist/bin/start",
      alloc = roygbivAlloc,
      N = 2
    )
  }

  lazy val roygbiv = Project(
    id = "roygbiv",
    base = file("."),
    settings = parentSettings ++ deploySettings ++ Seq(deploy := deployDescriptor),
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
