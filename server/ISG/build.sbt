name := """ISG"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.neo4j" % "neo4j-ogm-core" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-http-driver" % "2.0.1",
  "org.neo4j" % "neo4j-ogm-embedded-driver" % "2.0.1",
  "org.neo4j" % "neo4j-ogm" % "2.0.1"
)


fork in run := true