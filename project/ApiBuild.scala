import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._
import scala.Some
import com.typesafe.sbt.SbtAtmos.{AtmosKeys, Atmos, atmosSettings}
import com.typesafe.sbt.SbtAtmos.AtmosKeys._

object ApiBuild extends Build {

  //Resolvers
  lazy val commonResolvers = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Codahale Repo" at "http://repo.codahale.com",
    "Sonatype Repo" at "https://oss.sonatype.org/content/repositories/releases/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Excilys" at "http://repository.excilys.com/content/groups/public",
    "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
    "repo.novus rels" at "http://repo.novus.com/releases/",
    "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/",
    "Mandubian repository releases" at "https://github.com/mandubian/mandubian-mvn/raw/master/releases/",
    Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver
      .ivyStylePatterns)
  )

  //Dependencies
  lazy val commonDependencies = Seq(
    "org.specs2" %% "specs2" % "1.14" % "test",
    "junit" % "junit" % "4.10" % "test",
    "ch.qos.logback" % "logback-classic" % "1.0.9",
    "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test",
    "play" %% "play-json" % "2.2-SNAPSHOT",
    "org.twitter4j" % "twitter4j-core" % "[3.0,)",
    "com.github.nscala-time" %% "nscala-time" % "0.2.0",
    "javax.mail" % "mail" % "1.4.2",
    "org.scalaz" %% "scalaz-core" % "7.0.1",
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "com.typesafe.akka" % "akka-testkit_2.10" % "2.2.3",
    "com.typesafe.akka" %% "akka-remote" % "2.2.3",
    "commons-lang" % "commons-lang" % "2.6"
  )

  //Build Settings applied to all projects
  lazy val commonBuildSettings = Seq(
    organization := "com.wishclouds.api",
    scalaVersion := "2.10.2",
    resolvers ++= commonResolvers
  )

  //Settings applied to all projects
  lazy val defaultSettings =
    Defaults.defaultSettings ++ assemblySettings ++ commonBuildSettings ++ Seq(
      libraryDependencies ++= commonDependencies,
      fork in test := true, //Fork a new JVM for running tests
      javaOptions in(Test, run) += "-XX:MaxPermSize=1g",
      javaOptions in(Test, run) += "-Xmx4G",
      scalacOptions ++= Seq("-deprecation", "-feature")
    )


  //Project Dependencies
  lazy val serverDependencies = Seq(
    "net.databinder" %% "unfiltered" % "0.6.8",
    "net.databinder" %% "unfiltered-filter" % "0.6.8",
    "net.databinder" %% "unfiltered-netty" % "0.6.8",
    "net.databinder" %% "unfiltered-netty-server" % "0.6.8",
    "commons-daemon" % "commons-daemon" % "1.0.10"
  )

  lazy val resourcesDependencies = Seq(
    "net.databinder" %% "unfiltered" % "0.6.8",
    "net.databinder" %% "unfiltered-netty" % "0.6.8",
    "net.databinder" %% "unfiltered-netty-server" % "0.6.8",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
    "net.databinder.dispatch" %% "dispatch-json4s-native" % "0.11.0",
    "org.mongodb" %% "casbah" % "2.6.3",
    "com.novus" %% "salat" % "1.9.2",
    "com.notnoop.apns" % "apns" % "0.2.3",
    "com.sksamuel.elastic4s" % "elastic4s_2.10" % "0.90.5.1",
    "net.debasishg" %% "redisreact" % "0.1",
    "org.jsoup" % "jsoup" % "1.7.2",
    "net.databinder.dispatch" % "dispatch-jsoup_2.10" % "0.11.0",
    "com.amazonaws" % "aws-java-sdk" % "1.6.1"
  )

  //Main project configuration
  lazy val root = Project(
    id = "z-e-worker",
    base = file("."),
    settings = defaultSettings ++ Seq(
      mainClass in(Compile, run) := Some("com.wishclouds.explorer.Main"),
      mainClass in assembly := Some("com.wishclouds.explorer.Main"),
      test in assembly := {},
      javaOptions in (run) += "-Dakka.loglevel=DEBUG -Dakka.actor.debug.receive=on",
      javaOptions in(Test, run) += "-Xmx4G",
      mergeStrategy in assembly <<= (mergeStrategy in assembly) {
        (old) => {
          case PathList(ps@_*) if ps.last endsWith "mime.types" => MergeStrategy.first
          case x => old(x)
        }
      }
    ) ++ Seq(
      exportJars := true,
      libraryDependencies ++= resourcesDependencies ++ serverDependencies,
      artifact in(Compile, assembly) ~= {
        (art: Artifact) => art
      }
    )
  ).configs(Atmos)
    .settings(atmosSettings: _*)

}
