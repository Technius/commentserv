package commentserv

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{ MediaTypes, StatusCodes, StatusCode }
import fs2.Task
import upickle.default.write

import model._

trait ApiResponseMarshalling {
  implicit def apiResponseTEM: ToEntityMarshaller[ApiResponse] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(write(_)(ApiResponse.readWriter))

  implicit def apiResponseTRM: ToResponseMarshaller[ApiResponse] =
    Marshaller
      .fromStatusCodeAndHeadersAndValue(apiResponseTEM)
      .compose(r => (getStatusCode(r), List.empty, r))

  import ApiResponse._
  def getStatusCode(r: ApiResponse): StatusCode = r match {
    case _: AllThreads => StatusCodes.OK
    case _: AllComments => StatusCodes.OK
    case _: FoundThread => StatusCodes.OK
    case _: FoundUser => StatusCodes.OK
    case _: CommentNotFound => StatusCodes.NotFound
    case _: ThreadNotFound => StatusCodes.NotFound
    case _: UserNotFound => StatusCodes.NotFound
    case _: CreatedThread => StatusCodes.Created
    case _: UserAlreadyExists => StatusCodes.BadRequest
    case _: Registered => StatusCodes.Created
    case _: PostedComment => StatusCodes.Created
    case LoggedIn => StatusCodes.OK
    case LoggedOut => StatusCodes.OK
    case AuthenticationFailure => StatusCodes.Forbidden
  }
}

trait TaskMarshalling {
  implicit def taskMarshaller[A,B](implicit m: Marshaller[A, B]): Marshaller[Task[A], B] =
    Marshaller(implicit ec => task => task.unsafeRunAsyncFuture().flatMap(m(_)))
}
object TaskMarshalling extends TaskMarshalling
