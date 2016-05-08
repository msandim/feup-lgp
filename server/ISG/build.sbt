name := """ISG"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.neo4j" % "neo4j-ogm-core" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-http-driver" % "2.0.1",
  "org.neo4j" % "neo4j-ogm" % "2.0.1",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4"
)
