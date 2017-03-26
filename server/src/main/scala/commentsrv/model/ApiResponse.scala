package commentserv.model

import upickle.default._

sealed trait ApiResponse
object ApiResponse {
  case class AllThreads(threads: Seq[Thread]) extends ApiResponse
  case class AllComments(thread: ThreadId, comment: Seq[Comment]) extends ApiResponse
  case class FoundThread(thread: Thread, comments: Seq[Comment]) extends ApiResponse
  case class FoundUser(user: User) extends ApiResponse

  case class CreatedThread(thread: Thread) extends ApiResponse
  case class PostedComment(comment: Comment) extends ApiResponse

  case class UserNotFound(id: UserId) extends ApiResponse
  case class CommentNotFound(id: ThreadId) extends ApiResponse
  case class ThreadNotFound(id: ThreadId) extends ApiResponse

  case class Registered(user: UserId) extends ApiResponse
  case class UserAlreadyExists(loginName: String) extends ApiResponse
  case object LoggedIn extends ApiResponse
  case object LoggedOut extends ApiResponse
  case object AuthenticationFailure extends ApiResponse

  implicit val readWriter: ReadWriter[ApiResponse] = macroRW[ApiResponse]
}
