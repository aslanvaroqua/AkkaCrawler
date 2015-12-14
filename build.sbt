name := "z-worker"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-contrib" % "2.2.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test")