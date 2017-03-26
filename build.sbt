import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "co.technius.commentserv",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Xfuture",
        "-Yno-adapted-args",
        "-Xfatal-warnings"
      )
    )),
    name := "Hello",
    libraryDependencies += scalaTest % Test
  )
  .aggregate(server)

lazy val server = (project in file("server")).
  settings(
    name := "commentserv-server",
    libraryDependencies ++= Seq(
      upickle,
      akkaHttp,
      cats,
      fs2,
      doobie,
      postgres,
      scalaTest % Test,
      doobieScalatestCats % Test
    ),
    flywayUrl := "jdbc:postgresql://localhost:5432/",
    flywayLocations := Seq("classpath:db/migration"),
    flywayUser := "postgres"
  )

onLoad in Global := (onLoad in Global).value andThen (Command.process("project server", _))
