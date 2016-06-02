name := """ISG"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "org.neo4j" % "neo4j-ogm-core" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-http-driver" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-embedded-driver" % "2.0.1",
  "org.neo4j" % "neo4j-ogm" % "2.0.1",
  "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4",
  "com.opencsv" % "opencsv" % "3.7"
)

javaSource in Compile := baseDirectory.value / "app"

javaSource in Test := baseDirectory.value / "test"

baseDirectory in Test := baseDirectory.value / "test" / "resources"
fork in run := true
fork in Test := true
