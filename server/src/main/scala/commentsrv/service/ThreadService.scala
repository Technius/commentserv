package commentserv.service

import doobie.imports._
import scala.collection.immutable.Seq

import commentserv.model._

object ThreadService {
  def threadFrag: Fragment = fr"select * from threads"

  def find(id: ThreadId): ConnectionIO[Option[Thread]] =
    (threadFrag ++ fr"where id = $id").query[Thread].option

  def list: ConnectionIO[Seq[Thread]] =
    threadFrag.query[Thread].to[Seq]

  def create(title: String, slug: String): ConnectionIO[Thread] =
    sql"""insert into threads (title, slug) values ($title, $slug)"""
      .update
      .withUniqueGeneratedKeys[Thread]("id", "title", "slug")
}
