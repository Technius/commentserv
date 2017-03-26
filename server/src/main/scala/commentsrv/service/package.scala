package commentserv

package object service {
  type RedisIO[A] = cats.free.Free[RedisOp, A]
}
