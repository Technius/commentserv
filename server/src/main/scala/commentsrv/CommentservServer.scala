package commentserv

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import com.softwaremill.session.{ SessionConfig, SessionUtil }
import doobie.imports._
import fs2.Task
import scala.util.Success
import scala.concurrent.duration._
import com.redis.RedisClient

object CommentservServer extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  val host = "0.0.0.0"
  val port = 1234

  val xa = DriverManagerTransactor[Task](
    "org.postgresql.Driver", s"jdbc:postgresql://localhost:5432/", "postgres", ""
  )

  val redisClient = RedisClient("localhost", 6379)
  val sessionConfig = SessionConfig.default(SessionUtil.randomServerSecret())
  val routes = new Routes(xa, sessionConfig)

  val bindFut = Http().bindAndHandle(routes.root, host, port)
  bindFut onComplete {
    case Success(_) => println(s"Server started on $host:$port")
    case _ => println("Server failed to start")
  }

  if (io.Source.stdin.hasNext) {
    io.StdIn.readLine()
    println("Shutting down server...")
    redisClient.quit()(akka.util.Timeout(5.seconds))
    system.terminate()
  }
}
