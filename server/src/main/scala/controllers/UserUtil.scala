package controllers

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.Controller
import dao.UserDAO

import scala.concurrent.Future

trait UserUtil extends Controller {
  def userDAO: UserDAO

  def withUser(request: Request)(f: Int => Response): Response = {
    val userId = request.headerMap.get("X-userId").map(_.toInt)
    val token = request.headerMap.get("X-token")

    (userId, token) match {
      case (Some(u), Some(t)) if userDAO.isLoggedIn(u, t) =>
        f(u)
      case _ =>
        response.unauthorized
    }
  }

  def withUserAsync(request: Request)(f: Int => Future[Response]): Future[Response] = {
    val userId = request.headerMap.get("X-userId").map(_.toInt)
    val token = request.headerMap.get("X-token")

    (userId, token) match {
      case (Some(u), Some(t)) if userDAO.isLoggedIn(u, t) =>
        f(u)
      case _ =>
        Future.successful(response.unauthorized)
    }
  }

}
