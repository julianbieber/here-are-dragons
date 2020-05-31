package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.Controller
import dao.UserDAO

import scala.concurrent.{ExecutionContext, Future}

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

  def withUserAuto[A](request: Request)(f: Int => A)(implicit codec: JsonValueCodec[A]): Response = {
    withUser(request)({ userId =>
      val r = f(userId)
      val responseString = writeToString(r)
      response.ok(responseString)
    })
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

  def withUserAsyncAuto[A](request: Request)(f: Int => Future[A])(implicit ec: ExecutionContext, codec: JsonValueCodec[A]): Future[Response] = {
    withUserAsync(request)(f(_).map{ r =>
      val responseString = writeToString(r)
      response.ok(responseString)
    })
  }

}
