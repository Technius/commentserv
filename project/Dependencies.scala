import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.5"
  lazy val cats = "org.typelevel" %% "cats" % "0.9.0"
  lazy val fs2 = "co.fs2" %% "fs2-core" % "0.9.4"
  lazy val doobie = "org.tpolecat" %% "doobie-core-cats" % "0.4.1"
  lazy val doobieScalatestCats = "org.tpolecat" %% "doobie-scalatest-cats" % "0.4.1"
  lazy val postgres = "org.postgresql" % "postgresql" % "42.0.0"

  lazy val upickle = "com.lihaoyi" %% "upickle" % "0.4.3"
}
