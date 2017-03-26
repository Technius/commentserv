package commentserv.model

import upickle.default._

sealed trait ApiResponse
object ApiResponse {
  case class AllThreads(threads: Seq[Thread]) extends ApiResponse
  case class FoundThread(thread: Thread, comments: Seq[Comment]) extends ApiResponse
  case class FoundComment(thread: ThreadId, comment: Comment) extends ApiResponse

  case class CreatedThread(thread: Thread) extends ApiResponse

  case class CommentNotFound(id: ThreadId) extends ApiResponse
  case class ThreadNotFound(id: ThreadId) extends ApiResponse

  implicit val readWriter: ReadWriter[ApiResponse] = macroRW[ApiResponse]
}
