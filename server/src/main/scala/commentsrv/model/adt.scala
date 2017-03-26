package commentserv.model

import scala.collection.immutable.Seq

case class Thread(id: ThreadId, title: String, slug: String)

case class Comment(id: CommentId, author: UserId, content: String)
