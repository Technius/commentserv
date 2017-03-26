package commentserv

import com.softwaremill.session.{ SessionConfig, SessionManager }
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import cats.implicits._
import cats.data.OptionT
import doobie.imports._
import akka.http.scaladsl.server.Directives._
import fs2.Task

import service._
import model._

class Routes(xa: Transactor[Task], sessionConfig: SessionConfig) extends TaskMarshalling with ApiResponseMarshalling {
  implicit val sessionManager = new SessionManager[UserId](sessionConfig)

  def root = threads

  val threads =
    pathPrefix("threads") {
      pathPrefix(IntNumber) { id =>
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
    } ~
    pathPrefix("auth") {
      path("register") {
        parameters('login_name.as[String], 'username.as[String]) { (loginName, username) =>
          complete {
            UserService.create(loginName, username)
              .transact(xa).map(user => ApiResponse.Registered(user.id))
          }
        }
      } ~
      path("login") {
        post {
          parameters('login_name.as[String]) { (loginName) =>
            val optt = for {
              user <- OptionT(UserService.find(loginName))
            } yield user

            import scala.concurrent.ExecutionContext.Implicits.global //TODO stupid hack
            onSuccess(optt.value.transact(xa).unsafeRunAsyncFuture) {
              case Some(user) =>
                setSession(oneOff, usingCookies, user.id) {
                  complete {
                    ApiResponse.LoggedIn
                  }
                }
              case _ =>
                  complete(ApiResponse.AuthenticationFailure)
            }
          }
        }
      } ~
      path("check") {
        requiredSession(oneOff, usingCookies) { userId =>
          complete("Yup")
        } ~
        complete("Nope")
      } ~
      path("logout") {
        requiredSession(oneOff, usingCookies) { userId =>
          invalidateSession(oneOff, usingCookies) {
            complete {
              ApiResponse.LoggedOut
            }
          }
        }
      }
    }

  def threadRoute(id: ThreadId) =
    pathPrefix("comments") {
      commentRoute(id)
    } ~
    get {
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

  def commentRoute(threadId: ThreadId) =
    get {
      complete {
        CommentService.list(threadId)
          .transact(xa)
          .map(comments => ApiResponse.AllComments(threadId, comments))
      }
    } ~
    post {
      requiredSession(oneOff, usingCookies) { userId =>
        parameters('content.as[String]) { content =>
          complete {
            CommentService.create(threadId, userId, content)
              .transact(xa)
              .map(ApiResponse.PostedComment.apply)
          }
        }
      }
    }
}

