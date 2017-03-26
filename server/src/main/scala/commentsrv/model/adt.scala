package commentserv.model

import scala.collection.immutable.Seq

case class User(id: UserId, loginName: String, username: String)

case class Thread(id: ThreadId, title: String, slug: String)

case class Comment(id: CommentId, author: UserId, content: String)

case class AuthData(id: UserId, loginName: String, salt: String, hash: String)
