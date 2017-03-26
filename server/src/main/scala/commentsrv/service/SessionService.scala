package commentserv.service

import akka.util.Timeout
import com.redis._
import com.redis.serialization.Format
import cats.data.Kleisli
import cats.~>
import fs2.{ Strategy, Task }
import fs2.interop.cats._
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.FiniteDuration

import commentserv.model._

object SessionService {
  type Action[A] = Kleisli[Task, RedisIOInterpreter, A]
  import RedisIO._

  implicit val sessionFormat = new Format[Session] {
    def read(str: String) = upickle.default.read[Session](str)
    def write(session: Session) = upickle.default.write[Session](session)
  }

  def login(userId: UserId): Action[Unit] = wrap {
    put("sessiondata-" + userId, Session())
  }

  def checkLogin(userId: UserId): Action[Boolean] = wrap {
    get[Session]("sessiondata-" + userId).map(opt => opt.isDefined)
  }

  def logout(userId: UserId): Action[Unit] = wrap {
    delete("sessiondata-" + userId)
  }

  private def wrap[A](fa: RedisIO[A]): Action[A] = Kleisli(fa.foldMap(_))
}


class RedisIOInterpreter(redisClient: RedisClient, timeoutDuration: FiniteDuration)
    (implicit ec: ExecutionContext)
    extends (RedisOp ~> Task) {

  import RedisOp._
  implicit val timeout = Timeout(timeoutDuration)
  implicit val strategy = Strategy.fromExecutionContext(ec)

  override def apply[A](fa: RedisOp[A]): Task[A] = Task.fromFuture(toFuture(fa))

  def toFuture[A](fa: RedisOp[A]): Future[A] = fa match {
    case p @ Put(key, value) => {
      implicit val w = p.writer
      redisClient.set(key, value).map(_ => ())
    }
    case g @ Get(key) => {
      implicit val r = g.reader
      redisClient.get(key)
    }
    case Delete(key) => redisClient.del(key).map(_ => ())
  }
}
