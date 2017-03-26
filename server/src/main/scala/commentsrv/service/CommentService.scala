package commentserv.service

import doobie.imports._
import scala.collection.immutable.Seq

import commentserv.model._

object CommentService {
  def list(id: ThreadId): ConnectionIO[Seq[Comment]] =
    sql"select id, author, content from comments c where thread = $id"
      .query[Comment]
      .to[Seq]
}
