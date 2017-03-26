package commentserv.service

import doobie.imports._
import scala.collection.immutable.Seq

import commentserv.model._

object CommentService {
  def list(id: ThreadId): ConnectionIO[Seq[Comment]] =
    sql"select id, author, content from comments c where thread = $id"
      .query[Comment]
      .to[Seq]

  def create(thread: ThreadId, user: UserId, content: String): ConnectionIO[Comment] =
    sql"""
    insert into comments (thread, author, content)
    values ($thread, $user, $content)
    """
      .update
      .withUniqueGeneratedKeys[Comment]("id", "author", "content")
}
