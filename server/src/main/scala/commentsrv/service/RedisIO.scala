package commentserv.service

import cats.free._
import com.redis.serialization.{ Reader, Writer }

sealed trait RedisOp[A]
object RedisOp {
  case class Put[A](key: String, value: A)(implicit val writer: Writer[A]) extends RedisOp[Unit]
  case class Get[A](key: String)(implicit val reader: Reader[A]) extends RedisOp[Option[A]]
  case class Delete(key: String) extends RedisOp[Unit]
}

object RedisIO {
  import RedisOp._

  def put[A: Writer](key: String, value: A) = Free.liftF[RedisOp, Unit](Put(key, value))
  def get[A: Reader](key: String) = Free.liftF[RedisOp, Option[A]](Get(key))
  def delete(key: String) = Free.liftF[RedisOp, Unit](Delete(key))
}
