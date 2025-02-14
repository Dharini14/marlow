ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "marlow-bank-app"
  )

libraryDependencies ++= Seq(
  // Akka HTTP for building REST API
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "com.typesafe" % "config" % "1.4.2",

  // MySQL JDBC driver
  "mysql" % "mysql-connector-java" % "8.0.33",

  // Slick for database access
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",

  // Kafka client library
  "org.apache.kafka" %% "kafka" % "3.5.0",
  //"org.apache.kafka" %% "util" % "3.0.0",
  //"com.typesafe.akka" %% "akka-stream-kafka" % "4.0.1",

  // Circe for JSON handling
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",

  // Logback for logging
  "ch.qos.logback" % "logback-classic" % "1.4.11",

  //Test suite
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.10" % Test,
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "org.mockito" %% "mockito-scala" % "1.17.22" % Test

)
