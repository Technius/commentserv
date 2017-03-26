package commentserv

import cats.implicits._
import cats.data.OptionT
import doobie.imports._
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.server.Directives._
import fs2.Task

import service._
import model._

class Routes(xa: Transactor[Task]) extends TaskMarshalling with ApiResponseMarshalling {
  def root = threads

  val threads =
    pathPrefix("threads") {
      path(IntNumber) { id =>
        threadRoute(id)
      } ~
      post {
        (parameters('title, 'slug)) { (title, slug) =>
          complete {
            ThreadService.create(title, slug).transact(xa).map(ApiResponse.CreatedThread.apply)
          }
        }
      } ~
      get {
        complete {
          ThreadService.list.transact(xa).map(ApiResponse.AllThreads.apply)
        }
      }
    }

  def threadRoute(id: ThreadId) =
    complete {
      val optt = for {
        t <- OptionT(ThreadService.find(id))
        comments <- OptionT.liftF(CommentService.list(id))
      } yield (t, comments)

      optt.value.transact(xa) map {
        case Some((thread, comments)) => ApiResponse.FoundThread(thread, comments)
        case None => ApiResponse.ThreadNotFound(id)
      }
    }

}

trait ApiResponseMarshalling {
  import upickle.default.write
  import akka.http.scaladsl.model.{ MediaTypes, StatusCodes, StatusCode }
  implicit def apiResponseTEM: ToEntityMarshaller[ApiResponse] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(write(_)(ApiResponse.readWriter))

  implicit def apiResponseTRM: ToResponseMarshaller[ApiResponse] =
    Marshaller
      .fromStatusCodeAndHeadersAndValue(apiResponseTEM)
      .compose(r => (getStatusCode(r), List.empty, r))

  import ApiResponse._
  def getStatusCode(r: ApiResponse): StatusCode = r match {
    case _: AllThreads => StatusCodes.OK
    case _: FoundThread => StatusCodes.OK
    case _: FoundComment => StatusCodes.OK
    case _: CommentNotFound => StatusCodes.NotFound
    case _: ThreadNotFound => StatusCodes.NotFound
    case _: CreatedThread => StatusCodes.Created
  }
}

trait TaskMarshalling {
  implicit def taskMarshaller[A,B](implicit m: Marshaller[A, B]): Marshaller[Task[A], B] =
    Marshaller(implicit ec => task => task.unsafeRunAsyncFuture().flatMap(m(_)))
}
object TaskMarshalling extends TaskMarshalling
