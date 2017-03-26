package commentserv.service

import doobie.imports._
import scala.collection.immutable.Seq

import commentserv.model._

object UserService {
  def userFrag: Fragment = fr"select id, login_name from users"

  def create(loginName: String, username: String): ConnectionIO[User] =
    fr"insert into users (login_name, username) values ($loginName, $username)"
      .update
      .withUniqueGeneratedKeys[User]("id", "login_name", "username")

  def findById(id: UserId): ConnectionIO[Option[User]] =
    (userFrag ++ fr"where id = $id").query[User].option

  def find(loginName: String): ConnectionIO[Option[User]] =
    (userFrag ++ fr"where login_name = $loginName").query[User].option
}
